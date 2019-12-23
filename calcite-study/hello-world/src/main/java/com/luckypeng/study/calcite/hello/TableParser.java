package com.luckypeng.study.calcite.hello;

import com.google.common.collect.Sets;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表解析
 * @see  {@link org.apache.calcite.sql2rel.SqlToRelConverter#convertQuery(SqlNode, boolean, boolean)}
 * @see {@link org.apache.calcite.sql2rel.SqlToRelConverter#convertQueryRecursive(SqlNode, boolean, RelDataType)}
 */
public class TableParser {
    /**
     * 解析 select 语句中的所有表名
     * @param sqlNode
     * @return
     */
    public static Set<String> extractTableInSelect(SqlNode sqlNode) {
        return extractTableInSelectRecursive(sqlNode, false);
    }

    public static Set<String> extractTableInSelectRecursive(SqlNode sqlNode, boolean fromOrJoin) {
        if (sqlNode == null) {
            return new HashSet<>(0);
        }
        switch (sqlNode.getKind()) {
            case SELECT: {
                SqlSelect selectNode = (SqlSelect) sqlNode;
                // from clause
                Set<String> tableInFromClause = extractTableInSelectRecursive(selectNode.getFrom(), true);
                // select list
                Set<String> tableInSelectListCause = selectNode.getSelectList().getList().stream()
                        .filter(node -> !(node instanceof SqlCall))
                        .map(node -> extractTableInSelectRecursive(node, false))
                        .flatMap(Set::stream)
                        .collect(Collectors.toSet());
                // where
                Set<String> tableInWhereCause = extractTableInSelectRecursive(selectNode.getWhere(), false);
                // having
                Set<String> tableInHavingCause = extractTableInSelectRecursive(selectNode.getHaving(), false);
                return combine(tableInFromClause, tableInSelectListCause, tableInWhereCause, tableInHavingCause);
            }
            case JOIN: {
                SqlJoin sqlJoin = (SqlJoin) sqlNode;
                Set<String> left = extractTableInSelectRecursive(sqlJoin.getLeft(), true);
                Set<String> right = extractTableInSelectRecursive(sqlJoin.getRight(), true);
                return combine(left, right);
            }
            case AS: {
                SqlCall sqlCall = (SqlCall) sqlNode;
                if (sqlCall.operandCount() < 2) {
                    throw new RuntimeException("AS node should at least 2 operand");
                }
                return extractTableInSelectRecursive(sqlCall.operand(0), fromOrJoin);
            }
            case IDENTIFIER: {
                if (fromOrJoin) {
                    SqlIdentifier sqlIdentifier = (SqlIdentifier) sqlNode;
                    return Sets.newHashSet(sqlIdentifier.toString());
                } else {
                    return new HashSet<>(0);
                }
            }
            default: {
                if (sqlNode instanceof SqlCall) {
                    SqlCall sqlCall = (SqlCall) sqlNode;
                    Set<String> tableInChildren = sqlCall.getOperandList().stream()
                            .map(node -> extractTableInSelectRecursive(node, false))
                            .flatMap(Set::stream)
                            .collect(Collectors.toSet());
                    return tableInChildren;
                } else {
                    return new HashSet<>(0);
                }
            }
        }
    }

    public static<T> Set<T> combine(Set<T>... sets) {
        Set<T> collection = new HashSet<>();
        Stream.of(sets).forEach(collection::addAll);

        return collection;
    }
}
