package com.luckypeng.study.calcite.hello;

public class Sql {
    public static final String[] DDL_ARRAY = {
            "create table xxx(id int, name string)",
//            "create table xxx(id int, name string) partitioned by (day string) stored as parquet",  // 不支持分区
            "create table xxx as select a,b from yyy",
//            "create table xxx like yyy",
            "drop table abc",
//            "create database xxx",  // 用 schema 替代
//            "drop database xxx",  // 用 schema 替代
            "create schema xxx",
            "drop schema xxx",
//            "truncate table xxx.yyy", // 不支持 truncate
//            "alter table xxx.yyy add range partition '2019-01-01' <= values < '2020-01-01'", // 不支持分区操作
//            "alter table  xxx.yyy drop partition(dt='2019-12-20')", // 不支持分区操作
    };

    public static final String[] DML_ARRAY = {
            "select a,b from xxx",
            "insert into xxx select a,b from yyy",
            "insert into xxx values (1,2)",
            "upsert into xxx select a,b from yyy",
//            "insert overwrite table test select 1", // 不支持 overwrite
//            "insert into xxx partition(day) select a,b from ttt", // 不支持分区
//            "insert into xxx partition(day='2019-12-12') select a,b from sss", // 不支持分区
            "update xxx set a=1 where b=2",
            "delete from abc.def where dt = '2019-12-20'",
    };

    public static final String[] OTHER_SQL_ARRAY = {
            "describe xxx",
            "describe formatted xxx",
//            "use xx",
//            "desc xxx",  // 不支持，用 describe 替代
//            "desc formatted xxx",  // 不支持，用 describe 替代
//            "show create table xxx",
//            "show databases",
//            "show tables",
//            "load data inpath '/path/to/data/' into table xxx.yyy",
            "set sync_ddl=true", // 属于 SET_OPTION
//            "invalidate metadata xxx.yyy",
//            "show partitions a.b",
//            "refresh xxx.yy",
//            "compute stats xx.yy",
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
