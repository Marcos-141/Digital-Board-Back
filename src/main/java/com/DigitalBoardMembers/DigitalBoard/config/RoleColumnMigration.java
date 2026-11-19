package com.DigitalBoardMembers.DigitalBoard.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class RoleColumnMigration implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;

    public RoleColumnMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        // VARCHAR makes future roles extensible; allowing NULL preserves older rows, which
        // the application maps to STUDENT. Roll back with db/rollback/role-column.sql.
        String columnType = jdbcTemplate.queryForObject(
                "SELECT COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'role'",
                String.class
        );
        if (!"varchar(20)".equalsIgnoreCase(columnType)) {
            jdbcTemplate.execute("ALTER TABLE users MODIFY role VARCHAR(20) NULL");
        }
    }
}
