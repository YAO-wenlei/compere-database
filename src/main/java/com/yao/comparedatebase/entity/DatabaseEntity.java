package com.yao.comparedatebase.entity;

import lombok.*;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author yaowenlei
 * @description
 * @date 2022年04月21日 2:47 下午
 */
@Getter
@Setter
public class DatabaseEntity {

    private String host;
    private String driveClass;
    private String userName;
    private String passWord;
    private String dataBase;

    public DatabaseEntity(Builder builder) {
        this.driveClass = builder.driveClass;
        this.host = builder.host;
        this.userName = builder.userName;
        this.passWord = builder.passWord;
        this.dataBase = builder.dataBase;
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(driveClass);
            connection = DriverManager.getConnection(host, userName, passWord);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("数据库链接异常！");
        }
        return connection;
    }


    public void closeConnection(Connection connection) {
        if (!ObjectUtils.isEmpty(connection)) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throw new RuntimeException("数据库关闭失败！");
            }
        }
    }

    public static final class Builder{
        private String driveClass = "com.mysql.cj.jdbc.Driver";
        private String port = "3306";
        private String host;
        private String userName;
        private String passWord;
        private String dataBase;

        public Builder() {}

        public DatabaseEntity build() {
            this.host = "jdbc:mysql://"+host+":"+port+"/"+dataBase+"?useUnicode=true&useSSL=false&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false&serverTimezone=UTC";
            return new DatabaseEntity(this);
        }

        public Builder driveClass(String driveClass){
            this.driveClass = driveClass;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(String port) {
            if (!StringUtils.isEmpty(port)){
                this.port = port;
            }
            return this;
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder passWord(String passWord) {
            this.passWord = passWord;
            return this;
        }

        public Builder dataBase(String dataBase) {
            this.dataBase = dataBase;
            return this;
        }

    }
}
