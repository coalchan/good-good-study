package com.luckypeng.study.alidruid.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.stat.TableStat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TableParser {

    public static List<String> parse(String sql) {
        List<String> tables = new ArrayList<>();
        SQLStatementParser parser = new HiveStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        HiveSchemaStatVisitor visitor = new HiveSchemaStatVisitor();
        statement.accept(visitor);
        Map<TableStat.Name, TableStat> tableOpt = visitor.getTables();
        for (TableStat.Name key: tableOpt.keySet()){
            tables.add(key.toString());
        }
        return tables;
    }
}
