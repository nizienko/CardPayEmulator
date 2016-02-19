package ru.yamoney.test.repository.test_settings.group_instance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import ru.yamoney.test.entity.GroupInstance;
import ru.yamoney.test.entity.Parameter;
import ru.yamoney.test.repository.test_settings.FetchByGroupRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by def on 15.02.16.
 */
@org.springframework.stereotype.Repository("groupInstanceRepository")
public class GroupInstanceRepositoryImpl implements FetchByGroupRepository<GroupInstance> {
    @Autowired
    protected JdbcOperations jdbcOperations;

    @Override
    public List<GroupInstance> fetchByGroupId(long groupId) {
        return jdbcOperations.query("SELECT id, name, group_id, description\n" +
                "  FROM group_instance where group_id=?;", new Object[]{groupId}, new GroupInstanceRowMapper());
    }

    @Override
    public void insert(GroupInstance data) {
        jdbcOperations.update("INSERT INTO group_instance(\n" +
                "            name, group_id, description)\n" +
                "    VALUES (?, ?, ?);", data.getName(), data.getGroupId(), data.getDescription());
    }

    @Override
    public void update(GroupInstance data) {
        jdbcOperations.update("UPDATE group_instance\n" +
                "   SET name=?, group_id=?, description=?\n" +
                " WHERE id=?;", data.getName(), data.getGroupId(), data.getDescription());
    }

    @Override
    public void delete(GroupInstance data) {
        jdbcOperations.update("DELETE FROM group_instance\n" +
                " WHERE id=?;", data.getId());
    }

    @Override
    public List<GroupInstance> fetch() {
        return jdbcOperations.query("SELECT id, name, group_id, description\n" +
                "  FROM group_instance;", new GroupInstanceRowMapper());
    }

    @Override
    public GroupInstance fetchById(long id) {
        return jdbcOperations.queryForObject("SELECT id, name, group_id, description\n" +
                "  FROM group_instance where id=?;", new Object[]{id}, new GroupInstanceRowMapper());
    }

    private class GroupInstanceRowMapper implements RowMapper<GroupInstance> {

        @Override
        public GroupInstance mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new GroupInstance(rs.getLong("id"),
                    rs.getLong("group_id"),
                    rs.getString("name"),
                    rs.getString("description"));
        }
    }
}
