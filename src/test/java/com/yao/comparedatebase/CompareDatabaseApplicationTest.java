package com.yao.comparedatebase;

import com.yao.comparedatebase.builder.DatabaseBuilder;
import com.yao.comparedatebase.builder.DatabaseFactory;
import com.yao.comparedatebase.entity.AbstractDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author yaowenlei
 * @description
 * @date 2022年05月15日 12:22 下午
 */
@SpringBootTest
public class CompareDatabaseApplicationTest {

    @Autowired
    DatabaseFactory databaseFactory;

    @Test
    public void getConnection() {
        AbstractDatabase build = databaseFactory.getDataBaseBuilder()
                .databaseType("ORACLE")
                .host("102.020.030")
                .dataBase("test")
                .userName("aa")
                .passWord("aaa")
                .build();
        System.out.println(build);
    }

}
