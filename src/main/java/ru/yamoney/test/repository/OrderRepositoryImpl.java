package ru.yamoney.test.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import ru.yamoney.test.services.card_pay.Order;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by def on 20.03.2016.
 */

@org.springframework.stereotype.Repository("orderRepository")
public class OrderRepositoryImpl implements OrderRepository<Order> {

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Override
    public void insert(Order data) {
        data.setChangedDate(new Date());
        final Object[] orderData = new Object[]{
            data.getOrderN(),
            data.getChangedDate(),
            data.getCreatedDate(),
            data.getStatus().getCode(),
            data.getStatusMessage()
        };
        final String sql = "INSERT INTO \"order\"(\n" +
                "            order_n, changed_date, created_date, status, status_message)\n" +
                "    VALUES (?, ?, ?, ?, ?);";
        jdbcOperations.update(sql, orderData);
    }

    @Override
    public void update(Order data) {
        final String sql = "UPDATE \"order\"\n" +
                "   SET changed_date=?, created_date=?, status=?, status_message=?, \n" +
                "       order_n=?\n" +
                " WHERE id=?;";

        final Object[] orderData = new Object[]{
                data.getChangedDate(),
                data.getCreatedDate(),
                data.getStatus().getCode(),
                data.getStatusMessage(),
                data.getOrderN(),
                data.getId()
        };
        jdbcOperations.update(sql, orderData);
    }

    @Override
    public void delete(Order data) {

    }

    @Override
    public List<Order> fetch() {
        return null;
    }

    @Override
    public Order fetchById(long id) {
        return null;
    }

    @Override
    public Order fetchByOrderN(String orderN) {
        final String sql = "SELECT id, changed_date, created_date, status, status_message, order_n\n" +
                "  FROM \"order\" where order_n=?;";
        return jdbcOperations.queryForObject(sql, new Object[]{orderN}, new OrderMapper());
    }

    private static class OrderMapper implements RowMapper<Order> {

        @Override
        public Order mapRow(ResultSet resultSet, int i) throws SQLException {
            final Order order = new Order();
            order.setId(resultSet.getInt("id"));
            order.setOrderN(resultSet.getString("order_n"));
            order.setStatus(Order.Status.getStatus(resultSet.getInt("status")));
            order.setStatusMessage(resultSet.getString("status_message"));
            order.setCreatedDate(resultSet.getTimestamp("created_date"));
            order.setChangedDate(resultSet.getTimestamp("changed_date"));
            return order;
        }
    }
}
