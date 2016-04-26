package ru.yamoney.test.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import ru.yamoney.test.services.card_pay.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by def on 27.03.2016.
 */

@org.springframework.stereotype.Repository("operationRepository")
public class OperationRepositoryImpl implements OperationRepository<Operation> {

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Override
    public List<Operation> getByPaymentId(Integer id) {
        final Object[] orderData = new Object[]{id};
        final String sql = "SELECT id, payment_id, sum, operation_type, status, bank_acquire_id, request_params, \n" +
                "       response_params, changed_date, created_date\n" +
                "  FROM operation where payment_id=?;\n";
        return jdbcOperations.query(sql, orderData, new OperationMapper());
    }

    @Override
    public void insert(Operation data) {
        data.setChangedDate(new Date());
        final Object[] orderData = new Object[]{
                data.getPaymentId(),
                data.getSum(),
                data.getOperationType().getCode(),
                data.getStatus().getCode(),
                data.getBankAcquireId(),
                data.getRequestParams(),
                data.getResponseParams(),
                data.getChangedDate(),
                data.getCreatedDate()
        };
        final String sql = "INSERT INTO operation(\n" +
                "            payment_id, sum, operation_type, status, bank_acquire_id, request_params, \n" +
                "            response_params, changed_date, created_date)\n" +
                "    VALUES (?, ?, ?, ?, ?, ?, \n" +
                "            ?, ?, ?);\n";
        jdbcOperations.update(sql, orderData);
    }

    @Override
    public void update(Operation data) {
        data.setChangedDate(new Date());
        final Object[] orderData = new Object[]{
                data.getPaymentId(),
                data.getSum(),
                data.getOperationType().getCode(),
                data.getStatus().getCode(),
                data.getBankAcquireId(),
                data.getRequestParams(),
                data.getResponseParams(),
                data.getChangedDate(),
                data.getCreatedDate(),
                data.getId()
        };
        final String sql = "UPDATE operation\n" +
                "   SET payment_id=?, sum=?, operation_type=?, status=?, bank_acquire_id=?, \n" +
                "       request_params=?, response_params=?, changed_date=?, created_date=?\n" +
                " WHERE id=?;\n";
        jdbcOperations.update(sql, orderData);
    }

    @Override
    public void delete(Operation data) {

    }

    @Override
    public List<Operation> fetch() {
        return null;
    }

    @Override
    public Operation fetchById(long id) {
        final Object[] orderData = new Object[]{id};
        final String sql = "SELECT id, payment_id, sum, operation_type, status, bank_acquire_id, request_params, \n" +
                "       response_params, changed_date, created_date\n" +
                "  FROM operation where id=?;\n";
        return jdbcOperations.queryForObject(sql, orderData, new OperationMapper());
    }

    private static class OperationMapper implements RowMapper<Operation> {

        @Override
        public Operation mapRow(ResultSet resultSet, int i) throws SQLException {
            final Operation operation = new Operation();
            operation.setId(resultSet.getInt("id"));
            operation.setPaymentId(resultSet.getInt("payment_id"));
            operation.setSum(resultSet.getBigDecimal("sum"));
            operation.setOperationType(Operation.OperationType.getOperationType(resultSet.getInt("operation_type")));
            operation.setStatus(Operation.Status.getStatus(resultSet.getInt("status")));
            operation.setBankAcquireId(resultSet.getInt("bank_acquire_id"));
            operation.setRequestParams(resultSet.getString("request_params"));
            operation.setResponseParams(resultSet.getString("response_params"));
            operation.setCreatedDate(resultSet.getTimestamp("created_date"));
            operation.setChangedDate(resultSet.getTimestamp("changed_date"));
            return operation;
        }
    }
}
