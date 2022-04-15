package com.cracker.api.mc.common.strategy;

import com.google.common.collect.Maps;
import com.cracker.api.mc.common.validate.Assert;

import java.util.*;

/**
 *
 * 策略抽象类子类：DefaultStrategyFactory
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-23
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class DefaultStrategyFactory implements StrategyFactory {

    /**
     * 保存Strategy info的容器，线程安全
     */
    private final List<Map<String, Map<Object, String>>> setting =
            Collections.synchronizedList(new ArrayList<>());


    private static final StrategyFactory STRATEGY_FACTORY = new DefaultStrategyFactory();

    private DefaultStrategyFactory() {}

    public static StrategyFactory getStrategyFactory() {
        return STRATEGY_FACTORY;
    }

    /**
     * 根据业务code存储策略实体
     * @param buzzCode 业务code
     * @param choice 在特定业务Objects对象池中根据choice构建策略实体
     * @param mapping 存储实体对应的类全名
     */
    @Override
    public void pushStrategy(String buzzCode, Object choice, String mapping) {


        // 断言setting属于RandomAccess
        assert setting instanceof RandomAccess;

        Map<Object, String> objectStringMap = Maps.newHashMap();
        Map<String, Map<Object, String>> settingMap = Maps.newHashMap();
        boolean flag = false;

        if (Assert.isNotNull(this.setting)) {
            Map<String, Map<Object, String>> retMap;
            // 凡是实现了RandomAccess接口的List，使用普通for循环性能最高
            for (int i = 0, len = setting.size(); i < len; ++i) {
                if ((retMap = setting.get(i)) != null
                        && retMap.containsKey(buzzCode)) {
                    retMap.get(buzzCode).put(choice, mapping);
                    flag = true;
                    break;
                }
            }
        }
        if (!flag) {
            objectStringMap.put(choice, mapping);
            settingMap.put(buzzCode, objectStringMap);
            this.setting.add(settingMap);
        }
    }


    /**
     * 构建策略实体
     * @param buzzCode 业务code
     * @param choice 在特定业务Objects对象池中根据choice构建策略实体
     * @return Strategy
     */
    @Override
    public Strategy<?> buildStrategy(String buzzCode, Object choice) {

        if (Assert.isNotNull(setting)) {

            assert setting instanceof RandomAccess;

            String mapping = null;
            Map<String, Map<Object, String>> retMap;
            // 凡是实现了RandomAccess接口的List，使用普通for循环性能最高
            for (int i = 0,len = setting.size(); i < len; ++i) {
                if ((retMap = setting.get(i)) != null
                        && retMap.containsKey(buzzCode)) {
                    mapping = retMap.get(buzzCode).get(choice);
                    break;
                }
            }

            try {
                return mapping != null ? (Strategy<?>) Class.forName(mapping).newInstance() : null;
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 销毁策略对应的业务对象，不考虑多线程
     * @param buzzCode 业务code
     */
    @Override
    public void destroyStrategy(String buzzCode) {

        if (Assert.isNotNull(this.setting)) {
            Iterator<Map<String, Map<Object, String>>> iterator = this.setting.iterator();
            Map<String, Map<Object, String>> stringMap;
            while (iterator.hasNext()) {
                stringMap = iterator.next();
                if (stringMap.containsKey(buzzCode)) {
                    // 在循环中删除集合元素，谨记使用迭代器Iterator
                    iterator.remove();
                }
            }
        }
    }
}