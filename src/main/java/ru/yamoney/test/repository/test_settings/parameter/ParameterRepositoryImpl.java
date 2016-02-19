package ru.yamoney.test.repository.test_settings.parameter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import ru.yamoney.test.entity.Parameter;
import ru.yamoney.test.repository.test_settings.FetchByGroupRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by def on 14.02.16.
 */
@org.springframework.stereotype.Repository("parameterRepository")
public class ParameterRepositoryImpl implements FetchByGroupRepository<Parameter> {

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Override
    public List<Parameter> fetchByGroupId(long groupId) {
        return jdbcOperations.query("SELECT id, group_id, name, description\n" +
                "  FROM parameter where group_id=?;", new Object[]{groupId}, new ParameterRowMapper());
    }

    @Override
    public void insert(Parameter data) {
        jdbcOperations.update("INSERT INTO parameter(\n" +
                "            group_id, name, description)\n" +
                "    VALUES (?, ?, ?);", data.getGroupId(), data.getName(), data.getDescription());
    }

    @Override
    public void update(Parameter data) {
        jdbcOperations.update("UPDATE parameter\n" +
                "   SET group_id=?, name=?, description=?\n" +
                " WHERE id=?;", data.getGroupId(), data.getName(), data.getDescription(), data.getId());
    }

    @Override
    public void delete(Parameter data) {
        jdbcOperations.update("DELETE FROM parameter\n" +
                " WHERE id=?;", data.getId());
    }

    @Override
    public List<Parameter> fetch() {
        return jdbcOperations.query("SELECT id, group_id, name, description\n" +
                "  FROM parameter;", new ParameterRowMapper());
    }

    @Override
    public Parameter fetchById(long id) {
        return jdbcOperations.queryForObject("SELECT id, group_id, name, description\n" +
                "  FROM parameter where id=?;", new Object[]{id}, new ParameterRowMapper());
    }

    private class ParameterRowMapper implements RowMapper<Parameter> {

        @Override
        public Parameter mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Parameter(rs.getLong("id"),
                    rs.getLong("group_id"),
                    rs.getString("name"),
                    rs.getString("description"));
        }
    }
}
