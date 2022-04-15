package com.cracker.api.mc.common.strategy;

/**
 *
 * 策略工厂：StrategyFactory
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-23
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public interface StrategyFactory {

    /**
     * 根据业务code存储策略实体
     * @param buzzCode 业务code
     * @param choice 在特定业务Objects对象池中根据choice构建策略实体
     * @param mapping 存储实体对应的类全名
     */
    void pushStrategy(String buzzCode, Object choice, String mapping);

    /**
     * 构建策略实体
     * @param buzzCode 业务code
     * @param choice 在特定业务Objects对象池中根据choice构建策略实体
     * @return Strategy
     */
    @SuppressWarnings("rawtypes")
    Strategy buildStrategy(String buzzCode, Object choice);

    /**
     * 销毁策略对应的业务对象
     * @param buzzCode 业务code
     */
    void destroyStrategy(String buzzCode);

}