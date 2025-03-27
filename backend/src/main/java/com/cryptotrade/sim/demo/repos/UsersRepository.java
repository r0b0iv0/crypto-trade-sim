package com.cryptotrade.sim.demo.repos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cryptotrade.sim.demo.models.User;

@Repository
public class UsersRepository {
    private final JdbcTemplate jdbcTemplate;

    public UsersRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> getAllUsersList() {
        String sql = "SELECT id, name, balance FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }
}


class UserRowMapper implements RowMapper<User> {
    
    public User mapRow(ResultSet rs, int row) throws SQLException {
        return new User(rs.getInt("id"), rs.getString("name"), rs.getBigDecimal("balance"));
    }
}

