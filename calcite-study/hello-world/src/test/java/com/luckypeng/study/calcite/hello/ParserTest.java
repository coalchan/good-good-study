package com.luckypeng.study.calcite.hello;

import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.ddl.SqlDdlParserImpl;
import org.apache.calcite.sql.parser.impl.SqlParserImpl;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.tools.Frameworks;
import org.junit.Before;
import org.junit.Test;

/**
 * @see <a href="https://github.com/apache/calcite/blob/master/core/src/test/java/org/apache/calcite/sql/parser/SqlParserTest.java">SqlParserTest.java</a>
 */
public class ParserTest {
    private static SqlParser.Config config;
    private static SqlParser.Config ddlConfig;

    @Before
    public void setUp() throws Exception {
        config = Frameworks.newConfigBuilder()
                .parserConfig(
                        SqlParser.configBuilder()
                                .setParserFactory(SqlParserImpl.FACTORY)
                                .setCaseSensitive(false)
                                .setQuotedCasing(Casing.TO_LOWER)
                                .setUnquotedCasing(Casing.TO_LOWER)
                                .setConformance(SqlConformanceEnum.DEFAULT)
                                .build()
                ).build().getParserConfig();

        /**
         * Calcite’s core module (calcite-core) supports SQL queries (SELECT)
         * and DML operations (INSERT, UPDATE, DELETE, MERGE)
         * but does not support DDL operations such as CREATE SCHEMA or CREATE TABLE.
         *
         * The server module (<a href="https://calcite.apache.org/docs/adapter.html#server">calcite-server</a>)
         * adds DDL support to Calcite.
         */
        ddlConfig = Frameworks.newConfigBuilder()
                .parserConfig(
                        SqlParser.configBuilder()
                                .setParserFactory(SqlDdlParserImpl.FACTORY) // SqlDdlParserImpl from calcite-server
                                .setCaseSensitive(false)
                                .setQuotedCasing(Casing.TO_LOWER)
                                .setUnquotedCasing(Casing.TO_LOWER)
                                .setConformance(SqlConformanceEnum.DEFAULT)
                                .build()
                ).build().getParserConfig();
    }

    private void parse(String sql, SqlParser.Config config) throws SqlParseException {
        SqlParser parser = SqlParser.create(sql, config);
        SqlNode sqlNode = parser.parseStmt();
        System.out.println(sql + ":" + sqlNode.getKind());
    }

    @Test
    public void test() throws SqlParseException {
        String sql = "select 1+1";
        parse(sql, ddlConfig);
    }

    @Test
    public void testDDL() throws SqlParseException {
        for (String sql : Sql.DDL_ARRAY) {
            parse(sql, ddlConfig);
        }
    }

    @Test
    public void testDML() throws SqlParseException {
        for (String sql : Sql.DML_ARRAY) {
            parse(sql, config);
//            parse(sql, ddlConfig);  // SqlDdlParserImpl 也可以解析 DML 语句
        }
    }

    @Test
    public void testOTHERS() throws SqlParseException {
        for (String sql : Sql.OTHER_SQL_ARRAY) {
            parse(sql, config);
        }
    }


}
