package ru.yamoney.test.repository.test_settings.value;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import ru.yamoney.test.entity.Parameter;
import ru.yamoney.test.entity.ParameterValue;
import ru.yamoney.test.repository.test_settings.FetchByInstanceRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by def on 15.02.16.
 */

@org.springframework.stereotype.Repository("valueRepository")
public class ValueRepositoryImpl implements ValueRepository<ParameterValue> {

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Override
    public List<ParameterValue> fetchByInstanceId(long instanceId) {
        return jdbcOperations.query("SELECT parameter_id, instance_id, value\n" +
                "  FROM value where instance_id=?;", new Object[]{instanceId}, new ValueMapper());    }

    @Override
    public void insert(ParameterValue data) {
        jdbcOperations.update("INSERT INTO value(\n" +
                "            parameter_id, instance_id, value)\n" +
                "    VALUES (?, ?, ?);", data.getParameterId(), data.getInstanceId(), data.getValue());
    }

    @Override
    public void update(ParameterValue data) {
        jdbcOperations.update("UPDATE value\n" +
                "   SET value=?\n" +
                " WHERE parameter_id=? and instance_id=?;", data.getValue(), data.getParameterId(), data.getInstanceId());
    }

    @Override
    public void delete(ParameterValue data) {
        jdbcOperations.update("DELETE FROM value\n" +
                " WHERE parameter_id=? and instance_id=?;",
                data.getParameterId(), data.getInstanceId());
    }

    @Override
    public List<ParameterValue> fetch() {
        return jdbcOperations.query("SELECT parameter_id, instance_id, value\n" +
                "  FROM value;", new ValueMapper());
    }

    @Override
    public ParameterValue fetchById(long id) {
        return null;
    }

    @Override
    public ParameterValue fetchById(long instanceId, long parameterId) {
        return jdbcOperations.queryForObject("SELECT parameter_id, instance_id, value\n" +
                "  FROM value where parameter_id=? and instance_id=?;",
                new Object[]{parameterId, instanceId}, new ValueMapper());
    }

    private class ValueMapper implements RowMapper<ParameterValue> {
        @Override
        public ParameterValue mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ParameterValue(
                    rs.getLong("parameter_id"),
                    rs.getLong("instance_id"),
                    rs.getString("value"));
        }
    }
}
