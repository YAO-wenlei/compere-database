package com.yao.comparedatebase.entity;

import com.yao.comparedatebase.builder.DatabaseBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author yaowenlei
 * @description
 * @date 2022年05月15日 2:46 下午
 */
@Component("ORACLE")
public class OracleDatabaseInstance extends AbstractDatabase{

    private static final String PORT = "1521";
    private static final String DRIVE_CLASS = "oracle.jdbc.driver.OracleDriver";

    @Override
    public Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(this.getDriveClass());
            Properties props = new Properties() ;
            props.put( "user" , this.getUserName()) ;
            props.put( "password" , this.getPassWord()) ;
            props.put( "oracle.net.CONNECT_TIMEOUT" , "10000") ;
            props.put( "oracle.jdbc.ReadTimeout" , "60000" ) ;
            connection = DriverManager.getConnection(this.getHost(), props);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("数据库链接异常！");
        }
        return connection;
    }
//    jdbc:oracle:thin:@localhost :1521:orcl
    @Override
    public AbstractDatabase getInstance(DatabaseBuilder databaseBuilder) {
        String port = (!StringUtils.isEmpty(databaseBuilder.getPort()))? databaseBuilder.getPort() : PORT;
        String host = "jdbc:oracle:thin:@"+databaseBuilder.getHost()+":"+port+":"+"xe";

        OracleDatabaseInstance oracleDatabaseInstance = new OracleDatabaseInstance();
        oracleDatabaseInstance.setDriveClass(DRIVE_CLASS);
        oracleDatabaseInstance.setPort(port);
        oracleDatabaseInstance.setDataBase(databaseBuilder.getDataBase());
        oracleDatabaseInstance.setUserName(databaseBuilder.getUserName());
        oracleDatabaseInstance.setPassWord(databaseBuilder.getPassWord());
        oracleDatabaseInstance.setHost(host);
        oracleDatabaseInstance.setDatabaseType(databaseBuilder.getDatabaseType());

        return oracleDatabaseInstance;
    }
}
