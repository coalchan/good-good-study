package com.luckypeng.study.calcite.hello;

import com.google.common.collect.Sets;
import org.apache.calcite.config.Lex;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.impl.SqlParserImpl;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TableParserTest {
    private static SqlParser.Config config;

    @Before
    public void setUp() {
        SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        final FrameworkConfig frameworkConfig =
                Frameworks.newConfigBuilder()
                        .defaultSchema(rootSchema)
                        .parserConfig(
                                SqlParser.configBuilder()
                                        .setParserFactory(SqlParserImpl.FACTORY)
                                        .setLex(Lex.MYSQL)
                                        .setConformance(SqlConformanceEnum.MYSQL_5)
                                        .build()
                        ).build();
        config = frameworkConfig.getParserConfig();
    }

    @Test
    public void extractTableInSelect() throws SqlParseException {
        SqlParser parser = SqlParser.create(Sql.SELECT_SQL, config);
        SqlNode sqlNode = parser.parseStmt();
        Set<String> tables = TableParser.extractTableInSelect(sqlNode);
        assertEquals(Sets.newHashSet("dwd.customer", "dws.orders", "nation"), tables);
    }
}