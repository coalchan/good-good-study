package com.luckypeng.study.avatica.hello;

import com.google.common.collect.ImmutableList;
import org.apache.calcite.avatica.*;
import org.apache.calcite.avatica.remote.TypedValue;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * @author coalchan
 */
public class MyMeta extends MetaImpl {
    public MyMeta() {
        super(null);
    }

    @Override
    public StatementHandle createStatement(ConnectionHandle ch) {
        return new StatementHandle(ch.id, 0, null);
    }

    @Override
    public StatementHandle prepare(ConnectionHandle ch, String sql, long maxRowCount) {
        return null;
    }

    @Deprecated
    @Override
    public ExecuteResult prepareAndExecute(StatementHandle h, String sql, long maxRowCount, PrepareCallback callback) throws NoSuchStatementException {
        // Avatica doesn't call this.
        throw new UnsupportedOperationException("Deprecated");
    }

    @Override
    public ExecuteResult prepareAndExecute(StatementHandle h, String sql, long maxRowCount, int maxRowsInFirstFrame, PrepareCallback callback) throws NoSuchStatementException {
        Frame firstFrame = Frame.create(0, true, ImmutableList.of(new Object[] {"a"},
                new Object[] {"b"}, new Object[] {"c"}));

        final List<ColumnMetaData> columns = new ArrayList<>();
        final ColumnMetaData.ScalarType columnType = ColumnMetaData.scalar(
                1,
                "VARCHAR",
                ColumnMetaData.Rep.STRING
        );
        columns.add(new ColumnMetaData(
                0, // ordinal
                false, // auto increment
                true, // case sensitive
                false, // searchable
                false, // currency
                DatabaseMetaData.columnNullable, // nullable
                true, // signed
                128, // display size
                "id", // label
                "id", // column name
                "dual", // schema name
                1, // precision
                1, // scale
                "dual", // table name
                "test", // catalog name
                columnType, // avatica type
                true, // read only
                false, // writable
                false, // definitely writable
                columnType.columnClassName() // column class name
        ));

        Signature signature = Signature.create(
                columns,
                sql,
                new ArrayList<>(),
                Meta.CursorFactory.ARRAY,
                Meta.StatementType.SELECT);

        ExecuteResult result = new ExecuteResult(
                ImmutableList.of(
                        MetaResultSet.create(
                                h.connectionId,
                                h.id,
                                false,
                                signature,
                                firstFrame
                        )
                )
        );
        return result;
    }

    @Override
    public ExecuteBatchResult prepareAndExecuteBatch(StatementHandle h, List<String> sqlCommands) throws NoSuchStatementException {
        return null;
    }

    @Override
    public ExecuteBatchResult executeBatch(StatementHandle h, List<List<TypedValue>> parameterValues) throws NoSuchStatementException {
        return null;
    }

    @Override
    public Frame fetch(StatementHandle h, long offset, int fetchMaxRowCount) throws NoSuchStatementException, MissingResultsException {
        return null;
    }

    @Override
    public ExecuteResult execute(StatementHandle h, List<TypedValue> parameterValues, long maxRowCount) throws NoSuchStatementException {
        return null;
    }

    @Override
    public ExecuteResult execute(StatementHandle h, List<TypedValue> parameterValues, int maxRowsInFirstFrame) throws NoSuchStatementException {
        return null;
    }

    @Override
    public void closeStatement(StatementHandle h) {

    }

    @Override
    public boolean syncResults(StatementHandle sh, QueryState state, long offset) throws NoSuchStatementException {
        return false;
    }

    @Override
    public void commit(ConnectionHandle ch) {

    }

    @Override
    public void rollback(ConnectionHandle ch) {

    }
}
