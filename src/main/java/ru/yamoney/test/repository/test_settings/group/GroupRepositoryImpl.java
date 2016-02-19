package ru.yamoney.test.repository.test_settings.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import ru.yamoney.test.entity.Group;
import ru.yamoney.test.repository.CommonRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * Created by def on 14.02.16.
 */
@org.springframework.stereotype.Repository("groupRepository")
public class GroupRepositoryImpl implements CommonRepository<Group> {
    @Autowired
    protected JdbcOperations jdbcOperations;

    @Override
    public void insert(Group data) {
        Object[] params = new Object[] { data.getName(), data.getDescription() };
        int[] types = new int[] { Types.VARCHAR, Types.VARCHAR };

        jdbcOperations.update("INSERT INTO \"group\"(\n" +
                "            name, description)\n" +
                "    VALUES (?, ?);", params, types);
    }

    @Override
    public void update(Group data) {
        Object[] params = new Object[] { data.getName(), data.getDescription(), data.getId() };

        jdbcOperations.update("UPDATE \"group\"\n" +
                "   SET name=?, description=?\n" +
                " WHERE id=?;", params);
    }

    @Override
    public void delete(Group data) {
        jdbcOperations.update("DELETE FROM \"group\"\n" +
                " WHERE id=?;", new Object[]{data.getId()});
    }

    @Override
    public List<Group> fetch() {
        List<Group> groups = jdbcOperations.query("SELECT id, name, description\n" +
                "  FROM \"group\";", new GroupRowMapper());
        return groups;
    }

    @Override
    public Group fetchById(long id) {
        jdbcOperations.queryForObject("SELECT id, name, description\n" +
                "  FROM \"group\" where id=?;", new Object[]{id}, new GroupRowMapper());
        return null;
    }

    private class GroupRowMapper implements RowMapper<Group>{

        @Override
        public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Group(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("description"));
        }
    }
}
