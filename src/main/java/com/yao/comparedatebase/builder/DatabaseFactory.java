package com.yao.comparedatebase.builder;

import com.yao.comparedatebase.entity.AbstractDatabase;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yaowenlei
 * @description
 * @date 2022年05月15日 10:33 上午
 */
@Component
public class DatabaseFactory implements InitializingBean, ApplicationContextAware {
    //存放数据库实例
    private ApplicationContext applicationContext;

    public final Map<String, AbstractDatabase> databaseInstanceMap = new HashMap();

    public void setDatabaseInstance(String databaseName, AbstractDatabase database) {
        this.databaseInstanceMap.put(databaseName, database);
    }

    public AbstractDatabase getDatabaseType(String databaseName){
        return this.databaseInstanceMap.get(databaseName);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, AbstractDatabase> beansOfType = applicationContext.getBeansOfType(AbstractDatabase.class);
        this.databaseInstanceMap.putAll(beansOfType);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public DatabaseBuilder getDataBaseBuilder(){
        return applicationContext.getBean(DatabaseBuilder.class);
    }
}
