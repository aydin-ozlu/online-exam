package com.aydin.exam.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class Database {
    private static HikariDataSource ds;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/examdb");
        config.setUsername("examuser");
        config.setPassword("examPass!2025_123");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setPoolName("HikariPool");
        ds = new HikariDataSource(config);
    }

    private Database() {}

    public static DataSource getDataSource() {
        return ds;
    }
}
