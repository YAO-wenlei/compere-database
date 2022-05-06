package com.yao.comparedatebase.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.yao.comparedatebase.entity.DatabaseEntity;
import com.yao.comparedatebase.util.CompareUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yaowenlei
 * @description
 * @date 2022年04月30日 2:10 下午
 */
@RestController
@RequestMapping("/compare")
public class CompareController {

    @Autowired
    CompareUtils compareUtils;

    @PostMapping("/testConnection")
    public Map<String, Object> testConnection(HttpServletRequest request) {
        HashMap<String, Object> result = new HashMap<>();

        String formData = request.getParameter("formData");
        JSONObject jsonObject = JSONUtil.parseObj(formData);
        String ip = (String) jsonObject.get("ip");
        String port = (String)jsonObject.get("port");
        String database = (String)jsonObject.get("database");
        String userName = (String)jsonObject.get("userName");
        String password = (String)jsonObject.get("password");

        DatabaseEntity databaseEntity = new DatabaseEntity.Builder()
                .host(ip)
                .port(port)
                .dataBase(database)
                .userName(userName)
                .passWord(password)
                .build();

        return getConnectionResult(databaseEntity);
    }

    @PostMapping("/compare")
    public void compare(HttpServletRequest request, HttpServletResponse response) {
        HashMap<String, Object> result = new HashMap<>();
        String formDate01 = request.getParameter("formDate01");
        String formDate02 = request.getParameter("formDate02");

        JSONObject connectionOneParams = JSONUtil.parseObj(formDate01);
        String ipOne = (String) connectionOneParams.get("ip");
        String portOne = (String) connectionOneParams.get("port");
        String databaseOne = (String) connectionOneParams.get("database");
        String userNameOne = (String) connectionOneParams.get("userName");
        String passwordOne = (String) connectionOneParams.get("password");
        DatabaseEntity databaseEntityOne = new DatabaseEntity.Builder()
                .host(ipOne)
                .port(portOne)
                .dataBase(databaseOne)
                .userName(userNameOne)
                .passWord(passwordOne)
                .build();

        JSONObject connectionTwoParams = JSONUtil.parseObj(formDate02);
        String ipTwo = (String) connectionTwoParams.get("ip");
        String portTwo = (String) connectionTwoParams.get("port");
        String databaseTwo = (String) connectionTwoParams.get("database");
        String userNameTwo = (String) connectionTwoParams.get("userName");
        String passwordTwo = (String) connectionTwoParams.get("password");
        DatabaseEntity databaseEntityTwo = new DatabaseEntity.Builder()
                .host(ipTwo)
                .port(portTwo)
                .dataBase(databaseTwo)
                .userName(userNameTwo)
                .passWord(passwordTwo)
                .build();


        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=compare.xls");
        ServletOutputStream out = null;
        ExcelWriter writer = null;
        try {
            out = response.getOutputStream();
            writer = compareUtils.compare(databaseEntityOne, databaseEntityTwo);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //关闭输出Servlet流
        IoUtil.close(out);
    }


    private HashMap<String, Object> getConnectionResult(DatabaseEntity databaseEntity) {
        HashMap<String, Object> result = new HashMap<>();
        Connection connection = null;
        try {
            connection = databaseEntity.getConnection();
            result.put("status", 0);
            result.put("msg", "数据库链接成功");
        } catch (Exception e) {
            result.put("status", 1);
            result.put("msg", "数据库连接失败！");
        } finally {
            databaseEntity.closeConnection(connection);
        }
        return result;
    }

}
