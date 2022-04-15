package com.cracker.api.mc.retry.strategy;

import com.cracker.api.mc.common.util.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单的9级梯级重试策略
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-16
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class LadderRetryStrategy implements RetryStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(LadderRetryStrategy.class);
    private final ConcurrentHashMap<Integer, Long> delayLevelTable;
    private int maxDelayLevel;
    private static final String DEFAULT_DELAY_LEVEL_STRING = "10s 1m 10m 30m 2h 4h 6h 10h 24h";



    public LadderRetryStrategy() {
        this(DEFAULT_DELAY_LEVEL_STRING);
    }

    /**
     * 可以自定义梯级策略
     * @param delayLevelString 梯级策略
     */
    public LadderRetryStrategy(String delayLevelString) {
        this.delayLevelTable = new ConcurrentHashMap<>();
        this.parseDelayLevel(delayLevelString);
    }


    private void parseDelayLevel(String delayLevelString) {

        if (delayLevelString == null || delayLevelString.isEmpty()) {
            LOGGER.error("RetryStrategy init fail, delayLevelString is null");
            throw new IllegalArgumentException("RetryStrategy init fail, delayLevelString is null");
        }

        Map<String, Long> timeUnitTable = new HashMap<>(16);
        timeUnitTable.put("s", 1000L);
        timeUnitTable.put("m", 60000L);
        timeUnitTable.put("h", 3600000L);
        timeUnitTable.put("d", 86400000L);

        try {
            String[] levelString = delayLevelString.split(Symbol.BLANK_STRING);

            for (int i = 0,len = levelString.length; i < len; ++i) {
                String value = levelString[i];
                String ch = value.substring(value.length() - 1);
                Long tu = timeUnitTable.get(ch);
                int level = i + 1;
                if (level > this.maxDelayLevel) {
                    this.maxDelayLevel = level;
                }

                long num = Long.parseLong(value.substring(0, value.length() - 1));
                long delayTimeMilis = tu * num;
                this.delayLevelTable.put(level, delayTimeMilis);
            }

            LOGGER.info("RetryScheduler use LadderRetryStrategy delayLevelString is {}", delayLevelString);
        } catch (Exception ex) {
            LOGGER.error("delayLevelString = {}, parseDelayLevel Exception: {}", delayLevelString, ex.getMessage(), ex);
        }

    }


    @Override
    public long calculateRetryTime(int currentRetryLevel) {
        return this.delayLevelTable.get(currentRetryLevel);
    }

    @Override
    public int maxCount() {
        return this.maxDelayLevel;
    }
}
