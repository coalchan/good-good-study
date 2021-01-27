package com.luckypeng.study.alidruid.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TableParserTest {

    @Test
    public void testParse() {
        List<String> tables = TableParser.parse(Sql.SELECT_SQL);
        assertEquals(Arrays.asList("dwd.customer", "dws.orders", "nation"), tables);
    }

    @Test
    public void parseSQL() {
        String sql = "select a from b";
        List<String> tables = TableParser.parse(sql);
        System.out.println(tables);
    }

    @Test
    public void parseDML() {
        for (String sql : Sql.DML_ARRAY) {
            System.out.println("parse sql: " + sql);
            System.out.println(String.join(", ", TableParser.parse(sql)));
        }
    }

    @Test
    public void testDDL() {
        for (String sql : Sql.DDL_ARRAY) {
            System.out.println("parse sql: " + sql);
            System.out.println(String.join(", ", TableParser.parse(sql)));
        }
    }

    @Test
    public void testOTHERS() {
        for (String sql : Sql.OTHER_SQL_ARRAY) {
            parse(sql);
        }
    }

    private void parse(String sql) {
        System.out.println("sql: " + sql);
        SQLStatementParser parser = new HiveStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
    }
}