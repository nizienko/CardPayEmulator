package ru.yamoney.test.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import ru.yamoney.test.services.card_pay.Payment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by def on 20.03.2016.
 */

@org.springframework.stereotype.Repository("paymentRepository")
public class PaymentRepositoryImpl implements PaymentRepository<Payment> {

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Override
    public void insert(Payment data) {
        data.setChangedDate(new Date());
        final Object[] orderData = new Object[]{
            data.getPaymentN(),
            data.getChangedDate(),
            data.getCreatedDate(),
            data.getStatus().getCode(),
            data.getStatusMessage()
        };
        final String sql = "INSERT INTO payment (\n" +
                "            payment_n, changed_date, created_date, status, status_message)\n" +
                "    VALUES (?, ?, ?, ?, ?);";
        jdbcOperations.update(sql, orderData);
    }

    @Override
    public void update(Payment data) {
        data.setChangedDate(new Date());
        final String sql = "UPDATE payment\n" +
                "   SET changed_date=?, created_date=?, status=?, status_message=?, \n" +
                "       payment_n=?\n" +
                " WHERE id=?;";

        final Object[] orderData = new Object[]{
                data.getChangedDate(),
                data.getCreatedDate(),
                data.getStatus().getCode(),
                data.getStatusMessage(),
                data.getPaymentN(),
                data.getId()
        };
        jdbcOperations.update(sql, orderData);
    }

    @Override
    public void delete(Payment data) {

    }

    @Override
    public List<Payment> fetch() {
        return null;
    }

    @Override
    public Payment fetchById(long id) {
        return null;
    }

    @Override
    public Payment fetchByOrderN(String orderN) {
        final String sql = "SELECT id, changed_date, created_date, status, status_message, payment_n\n" +
                "  FROM payment where payment_n=?;";
        return jdbcOperations.queryForObject(sql, new Object[]{orderN}, new OrderMapper());
    }

    @Override
    public List<Payment> fetchForClearing() {
        final String sql = "SELECT id, changed_date, created_date, status, status_message, payment_n\n" +
                "  FROM payment where status=2 and created_date < current_timestamp - time '0:01';";
        return jdbcOperations.query(sql, new OrderMapper());
    }

    private static class OrderMapper implements RowMapper<Payment> {

        @Override
        public Payment mapRow(ResultSet resultSet, int i) throws SQLException {
            final Payment payment = new Payment();
            payment.setId(resultSet.getInt("id"));
            payment.setPaymentN(resultSet.getString("payment_n"));
            payment.setStatus(Payment.Status.getStatus(resultSet.getInt("status")));
            payment.setStatusMessage(resultSet.getString("status_message"));
            payment.setCreatedDate(resultSet.getTimestamp("created_date"));
            payment.setChangedDate(resultSet.getTimestamp("changed_date"));
            return payment;
        }
    }
}
