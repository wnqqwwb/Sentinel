/*
 * Copyright 1999-2019 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker;

import java.util.List;
import java.util.concurrent.atomic.LongAdder;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.statistic.base.LeapArray;
import com.alibaba.csp.sentinel.slots.statistic.base.WindowWrap;
import com.alibaba.csp.sentinel.util.AssertUtil;

import static com.alibaba.csp.sentinel.slots.block.RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT;
import static com.alibaba.csp.sentinel.slots.block.RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO;

/**
 * @author Eric Zhao
 * @since 1.8.0
 */
public class ExceptionCircuitBreaker extends AbstractCircuitBreaker {

    private final int strategy;
    private final int minRequestAmount;
    private final double threshold;

    private final LeapArray<SimpleErrorCounter> stat;

    public ExceptionCircuitBreaker(DegradeRule rule) {
        this(rule, new SimpleErrorCounterLeapArray(1, rule.getStatIntervalMs()));
    }

    ExceptionCircuitBreaker(DegradeRule rule, LeapArray<SimpleErrorCounter> stat) {
        super(rule);
        this.strategy = rule.getGrade();
        boolean modeOk = strategy == DEGRADE_GRADE_EXCEPTION_RATIO || strategy == DEGRADE_GRADE_EXCEPTION_COUNT;
        AssertUtil.isTrue(modeOk, "rule strategy should be error-ratio or error-count");
        AssertUtil.notNull(stat, "stat cannot be null");
        this.minRequestAmount = rule.getMinRequestAmount();
        this.threshold = rule.getCount();
        this.stat = stat;
    }

    @Override
    protected void resetStat() {
        // Reset current bucket (bucket count = 1).
        stat.currentWindow().value().reset();
    }

    @Override
    public void onRequestComplete(Context context) {
        Entry entry = context.getCurEntry();
        if (entry == null) {
            return;
        }
        Throwable error = entry.getError();
        // 获取当前时间窗口
        SimpleErrorCounter counter = stat.currentWindow().value();
        // 当前请求也是异常的，则异常次数+1
        if (error != null) {
            counter.getErrorCount().add(1);
        }
        // 没有异常，总数+1
        counter.getTotalCount().add(1);

        // 处理断路器状态变更
        handleStateChangeWhenThresholdExceeded(error);
    }

    private void handleStateChangeWhenThresholdExceeded(Throwable error) {
        // 当前断路器是OPEN，则无需处理
        if (currentState.get() == State.OPEN) {
            return;
        }

        // 当前断路器是HALF_OPEN
        if (currentState.get() == State.HALF_OPEN) {
            // 此次请求没有异常，那么去关闭断路器
            // In detecting request
            if (error == null) {
                fromHalfOpenToClose();
            } else {
                // 还是异常，则将断路器从HALF_OPEN变更到OPEN，并重新计算时间下一次重试时间
                fromHalfOpenToOpen(1.0d);
            }
            return;
        }

        // 当前断路器是CLOSED状态
        List<SimpleErrorCounter> counters = stat.values();
        long errCount = 0;
        long totalCount = 0;
        for (SimpleErrorCounter counter : counters) {
            errCount += counter.errorCount.sum();
            totalCount += counter.totalCount.sum();
        }
        // 请求总是没有达到最小请求数，直接返回
        if (totalCount < minRequestAmount) {
            return;
        }
        double curCount = errCount;
        // 如果是使用异常比例数，则计算出异常的次数
        if (strategy == DEGRADE_GRADE_EXCEPTION_RATIO) {
            // Use errorRatio
            curCount = errCount * 1.0d / totalCount;
        }
        // 异常数达到阈值，将断路器状态置为OPEN
        if (curCount > threshold) {
            transformToOpen(curCount);
        }
    }

    static class SimpleErrorCounter {
        private LongAdder errorCount;
        private LongAdder totalCount;

        public SimpleErrorCounter() {
            this.errorCount = new LongAdder();
            this.totalCount = new LongAdder();
        }

        public LongAdder getErrorCount() {
            return errorCount;
        }

        public LongAdder getTotalCount() {
            return totalCount;
        }

        public SimpleErrorCounter reset() {
            errorCount.reset();
            totalCount.reset();
            return this;
        }

        @Override
        public String toString() {
            return "SimpleErrorCounter{" +
                "errorCount=" + errorCount +
                ", totalCount=" + totalCount +
                '}';
        }
    }

    static class SimpleErrorCounterLeapArray extends LeapArray<SimpleErrorCounter> {

        public SimpleErrorCounterLeapArray(int sampleCount, int intervalInMs) {
            super(sampleCount, intervalInMs);
        }

        @Override
        public SimpleErrorCounter newEmptyBucket(long timeMillis) {
            return new SimpleErrorCounter();
        }

        @Override
        protected WindowWrap<SimpleErrorCounter> resetWindowTo(WindowWrap<SimpleErrorCounter> w, long startTime) {
            // Update the start time and reset value.
            w.resetTo(startTime);
            w.value().reset();
            return w;
        }
    }
}
