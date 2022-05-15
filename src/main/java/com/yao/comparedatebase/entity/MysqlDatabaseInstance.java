package com.yao.comparedatebase.entity;

import com.yao.comparedatebase.builder.DatabaseBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author yaowenlei
 * @description
 * @date 2022年05月15日 10:55 上午
 */
@Component(value = "MYSQL")
public class MysqlDatabaseInstance extends AbstractDatabase {

    private static final String PORT = "3306";
    private static final String DRIVE_CLASS = "com.mysql.cj.jdbc.Driver";

    @Override
    public Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(this.getDriveClass());
            connection = DriverManager.getConnection(this.getHost(), this.getUserName(), this.getPassWord());
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("数据库链接异常！");
        }
        return connection;
    }


    @Override
    public AbstractDatabase getInstance(DatabaseBuilder databaseBuilder) {

        String port = (!StringUtils.isEmpty(databaseBuilder.getPort()))? databaseBuilder.getPort() : PORT;
        String host = "jdbc:mysql://"+databaseBuilder.getHost()+":"+port+"/"+databaseBuilder.getDataBase()+"?useUnicode=true&useSSL=false&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false&serverTimezone=UTC";

        MysqlDatabaseInstance mysqlDatabaseInstance = new MysqlDatabaseInstance(); //返回新的对象
        mysqlDatabaseInstance.setDriveClass(DRIVE_CLASS);
        mysqlDatabaseInstance.setPort(port);
        mysqlDatabaseInstance.setDataBase(databaseBuilder.getDataBase());
        mysqlDatabaseInstance.setUserName(databaseBuilder.getUserName());
        mysqlDatabaseInstance.setPassWord(databaseBuilder.getPassWord());
        mysqlDatabaseInstance.setHost(host);
        mysqlDatabaseInstance.setDatabaseType(databaseBuilder.getDatabaseType());

        return mysqlDatabaseInstance;
    }

}
