package com.yao.comparedatebase.entity;

import com.yao.comparedatebase.builder.DatabaseBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.ObjectUtils;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author yaowenlei
 * @description
 * @date 2022年05月15日 10:19 上午
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractDatabase {
    private String host;
    private String driveClass;
    private String userName;
    private String passWord;
    private String dataBase;
    private String port;

    private String databaseType;


    public abstract Connection getConnection();

    public void closeConnection(Connection connection){
        if (!ObjectUtils.isEmpty(connection)) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throw new RuntimeException("数据库关闭失败！");
            }
        }
    };

    public abstract AbstractDatabase getInstance(DatabaseBuilder databaseBuilder);

}
