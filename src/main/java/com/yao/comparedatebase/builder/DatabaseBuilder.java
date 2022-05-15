package com.yao.comparedatebase.builder;

import com.yao.comparedatebase.entity.AbstractDatabase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * @author yaowenlei
 * @description
 * @date 2022年05月15日 10:15 上午
 */
@Component
@Scope("prototype")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DatabaseBuilder extends DatabaseFactory {

    private String host;
    private String userName;
    private String passWord;
    private String dataBase;
    private String port;

    private String databaseType;

    public DatabaseBuilder host(String host){
        this.host = host;
        return this;
    }

    public DatabaseBuilder port(String port){
        this.port = port;
        return this;
    }

    public DatabaseBuilder dataBase(String dataBase) {
        this.dataBase = dataBase;
        return this;
    }

    public DatabaseBuilder userName(String userName){
        this.userName = userName;
        return this;
    }

    public DatabaseBuilder passWord(String passWord) {
        this.passWord = passWord;
        return this;
    }

    public DatabaseBuilder databaseType(String databaseType) {
        this.databaseType = databaseType;
        return this;
    }

    public AbstractDatabase build(){
        AbstractDatabase databaseInstance = this.getDatabaseType(databaseType);

        if (ObjectUtils.isEmpty(databaseInstance)){
            throw new RuntimeException("为发现["+databaseType+"]数据库实例");
        }
        return databaseInstance.getInstance(this);
    }

}
