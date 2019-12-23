package com.luckypeng.study.alidruid.parser;

public class Sql {
    public static final String[] DDL_ARRAY = {
            "create table xxx(id int, name string)",
            "create table xxx(id int, name string) partitioned by (day string) stored as parquet",
            "create table xxx as select a,b from yyy",
            "create table xxx like yyy",
            "drop table abc",
            "create database xxx",
            "drop database xxx",
//            "create schema xxx",  // 不支持
//            "drop schema xxx",  // 不支持
            "truncate table xxx.yyy",
            "alter table xxx.yyy add range partition '2019-01-01' <= values < '2020-01-01'",
            "alter table  xxx.yyy drop partition(dt='2019-12-20')",
    };

    public static final String[] DML_ARRAY = {
            "select a,b from xxx",
            "insert into table xxx select a,b from yyy",  // table 不加会报错
            "insert into table xxx values (1,2)",  // table 不加会报错
            "upsert into xxx select a,b from yyy",  // 加了 table 会报错
            "insert overwrite table test select 1", // table 不加会报错
            "insert into table xxx partition(day) select a,b from ttt",  // table 不加会报错
            "insert into table xxx partition(day='2019-12-12') select a,b from sss", // table 不加会报错
            "update xxx set a=1 where b=2",
            "delete from abc.def where dt = '2019-12-20'",
    };

    public static final String[] OTHER_SQL_ARRAY = {
//            "describe xxx",  // 不支持
//            "describe formatted xxx",  // 不支持
            "use xx",
//            "desc xxx",  // 不支持
//            "desc formatted xxx",  // 不支持
//            "show create table xxx",  // 不支持
//            "show databases",  // 不支持
//            "show tables",  // 不支持
//            "load data inpath '/path/to/data/' into table xxx.yyy",  // 不支持
            "set sync_ddl=true",
//            "invalidate metadata xxx.yyy",  // 不支持
//            "show partitions a.b",  // 不支持
//            "refresh xxx.yy",  // 不支持
//            "compute stats xx.yy",  // 不支持
//            "create function if not exists xxx(int, string) returns string location 'hdfs://cluster/path/to/jar' symbol='aa.XxUDF'"
    };

    public static final String SELECT_SQL = "select\n" +
            "  c_custkey, c_name, sum(l_extendedprice * (1 - l_discount)) as revenue,\n" +
            "  c_acctbal, n_name, c_address, c_phone, c_comment\n" +
            "from\n" +
            "  dwd.customer c join dws.orders o\n" +
            "  on\n" +
            "    c.c_custkey = o.o_custkey and o.o_orderdate >= '1993-10-01' and o.o_orderdate < '1994-01-01'\n" +
            "  join nation n\n" +
            "  on\n" +
            "    c.c_nationkey = n.n_nationkey\n" +
            "  join dwd.customer l\n" +
            "  on\n" +
            "    l.l_orderkey = o.o_orderkey and l.l_returnflag = 'R'\n" +
            "group by c_custkey, c_name, c_acctbal, c_phone, n_name, c_address, c_comment\n" +
            "order by revenue desc\n" +
            "limit 20";
}
