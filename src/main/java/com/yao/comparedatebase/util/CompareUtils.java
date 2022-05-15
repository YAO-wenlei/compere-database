package com.yao.comparedatebase.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.yao.comparedatebase.entity.AbstractDatabase;
import oracle.jdbc.OracleDatabaseMetaData;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yaowenlei
 * @description
 * @date 2022年04月21日 3:01 下午
 */
@Component
public class CompareUtils {

    private static final String ORACLE = "ORACLE";


    public ExcelWriter compare(AbstractDatabase databaseEntity1, AbstractDatabase databaseEntity2) throws SQLException {

        Connection connection1 = databaseEntity1.getConnection();
        DatabaseMetaData metaData1 = connection1.getMetaData();

        Connection connection2 = databaseEntity2.getConnection();
        DatabaseMetaData metaData2 = connection2.getMetaData();

        ExcelWriter excelWriter = null;
        try {
            //获取给定数据库的所有表
            List<String> tableList1 = getAllTableForDatabase(metaData1, databaseEntity1, null, "%", new String[]{"TABLE"});
            List<String> tableList2 = getAllTableForDatabase(metaData2, databaseEntity2, null, "%", new String[]{"TABLE"});

            //获取A库中B库没有的表
            System.out.println(databaseEntity1.getDataBase() + "库比" + databaseEntity2.getDataBase() + "库多的表：");
            List<String> diffList = tableList1.stream().filter(item -> !tableList2.contains(item)).collect(Collectors.toList());
            diffList.forEach(System.out::println);
            List<String> row1 = new ArrayList<>();
            row1.add(databaseEntity1.getDataBase() + "库比" + databaseEntity2.getDataBase() + "库多的表：");
            row1.addAll(diffList);


            System.out.println(databaseEntity1.getDataBase() + "库比" + databaseEntity2.getDataBase() + "库少的表：");
            //获取B库中A库没有的表
            List<String> diffListA = tableList2.stream().filter(item -> !tableList1.contains(item)).collect(Collectors.toList());
            diffListA.forEach(System.out::println);
            List<String> row2 = new ArrayList<>();
            row2.add(databaseEntity1.getDataBase() + "库比" + databaseEntity2.getDataBase() + "库少的表：");
            row2.addAll(diffListA);

            System.out.println(databaseEntity1.getDataBase() + "库与" + databaseEntity2.getDataBase() + "库相同的表：");
            //获取A库与B库相同的表
            List<String> sameList = tableList1.stream().filter(item -> tableList2.contains(item)).collect(Collectors.toList());
            sameList.forEach(System.out::println);
            ArrayList<String> row3 = new ArrayList<>();
            row3.add(databaseEntity1.getDataBase() + "库与" + databaseEntity2.getDataBase() + "库相同的表：");
            row3.addAll(sameList);

            //获取A库与B库中相同表的表信息
            List<List<Map<String, String>>> allSameTableInfoList = new ArrayList<>();

            //判断表名是否相等
            boolean dateBaseNameIsEquals = databaseEntity1.getDataBase().equals(databaseEntity2.getDataBase());
            String database01Name = dateBaseNameIsEquals ? databaseEntity1.getDataBase() + "(1)" : databaseEntity1.getDataBase();
            String database02Name = dateBaseNameIsEquals ? databaseEntity2.getDataBase() + "(2)" : databaseEntity2.getDataBase();
            sameList.stream().forEach(item -> {
                Map<String, String> tableInfo01 = getTableInfo(metaData1, databaseEntity1.getDataBase(), item);
                Map<String, String> tableInfo02 = getTableInfo(metaData2, databaseEntity2.getDataBase(), item);

                ArrayList<Map<String, String>> maps = new ArrayList<>(tableInfo01.size() + tableInfo02.size());

                tableInfo01.forEach((k, v) -> {
                    HashMap<String, String> tableRows01 = new HashMap<>();
                    tableRows01.put(database01Name, v);
                    if (tableInfo02.containsKey(k)) {
                        tableRows01.put(database02Name, tableInfo02.get(k));
                    } else {
                        tableRows01.put(database02Name, "");
                    }
                    maps.add(tableRows01);
                });
                //从tableInfo01的末尾追加tableInfo02的值
                int[] count = {tableInfo01.size()};
                tableInfo02.forEach((k, v) -> {
                    HashMap<String, String> tableRows = new HashMap<>();
                    if (tableInfo01.containsKey(k)) {
                        return;
                    }
                    tableRows.put(database01Name, "");
                    tableRows.put(database02Name, v);
                    int size = tableInfo01.size();
                    size += size++;
                    maps.add(count[0]++, tableRows);
                });

                allSameTableInfoList.add(maps);
            });


            List<Object> content = new ArrayList<>();
            content.add(row1);
            content.add(row2);
            content.add(row3);
            content.addAll(allSameTableInfoList);

            excelWriter = writeExcel(content, sameList);
        } catch (IOException e) {
            System.out.println("写入表格出错");
        } finally {
            databaseEntity1.closeConnection(connection1);
            databaseEntity2.closeConnection(connection2);
        }
        return excelWriter;
    }

    /**
     * 获取数据库中的所有表
     **/
    public List<String> getAllTableForDatabase(DatabaseMetaData metaData, AbstractDatabase catalog, String schemaPattern, String tableName, String[] type) {
        List<String> tableList = new ArrayList<>();
        ResultSet tables;
        try {
            if (ORACLE.equals(catalog.getDatabaseType())) {
                tables = metaData.getTables(null, catalog.getDataBase(), null, type);
            } else {
                tables = metaData.getTables(catalog.getDataBase(), schemaPattern, tableName, type);
            }
            while (tables.next()) {
                String table_name = tables.getString("TABLE_NAME");
                tableList.add(table_name);
            }
        } catch (SQLException throwables) {
            return new ArrayList<>();
        }
        return tableList;
    }

    /**
     * 获取表中的列信息
     **/
    public Map<String, String> getTableInfo(DatabaseMetaData metaData, String database, String tableName) {
        HashMap<String, String> columnMap = new HashMap<>();
        try {
            ResultSet columns = metaData.getColumns(database, null, tableName, null);
            String column = "";
            while (columns.next()) {
                column = columns.getString("COLUMN_NAME") + ":" + columns.getString("TYPE_NAME") + ":" + columns.getString("COLUMN_SIZE") + ":" + columns.getString("IS_NULLABLE");
                columnMap.put(columns.getString("COLUMN_NAME"), column);
            }
        } catch (SQLException throwables) {
            return new HashMap<>();
        }
        return columnMap;
    }

    public ExcelWriter writeExcel(List<Object> content, List<String> mergeName) throws IOException {
        ExcelWriter writer = ExcelUtil.getWriter();

        int i = 0;
        for (Object o : content) {
            Object o1 = ((ArrayList) o).get(0);
            if (o1 instanceof Map) {
                writer.merge(1, mergeName.get(i++));
                writer.write(((ArrayList) o), true);
            } else {
                ArrayList<Object> arrayList = CollUtil.newArrayList(o);
                writer.write(arrayList, true);
            }
        }
        return writer;
    }

}
