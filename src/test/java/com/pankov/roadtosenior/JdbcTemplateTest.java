package com.pankov.roadtosenior;

import org.apache.commons.dbcp2.BasicDataSource;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcTemplateTest {

    private final static String URL = "jdbc:h2:mem:test";

    private static BasicDataSource ds = new BasicDataSource();
//    private JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
    private com.pankov.roadtosenior.JdbcTemplate jdbcTemplate = new com.pankov.roadtosenior.JdbcTemplate(ds);

    static {
        ds.setUrl(URL);
    }

    @BeforeAll
    public static void before() throws SQLException, FileNotFoundException {
        RunScript.execute(ds.getConnection(), new FileReader("src/test/resources/test.sql"));
    }

    @Test
    public void testQueryMethod_shouldReturnAList() {
        List<Framework> frameworkList = jdbcTemplate.query("SELECT * from test.framework",
                new FrameworkRowMapper());
//                new BeanPropertyRowMapper<>(Framework.class));
        assertNotNull(frameworkList);
        assertEquals(5, frameworkList.size());
        assertEquals(List.of("Spring Framework", "Angular", "Laravel", "Hibernate", "Veujs"),
                frameworkList.stream().map(f -> f.getName()).toList());

    }

    @Test
    public void testQueryWithParapetersMethod_shouldReturnAList() {
        List<Framework> frameworkList = jdbcTemplate.query("SELECT * from test.framework WHERE language = ?",
                new FrameworkRowMapper(), "Java");
//                new BeanPropertyRowMapper<>(Framework.class), "Java");
        assertNotNull(frameworkList);
        assertEquals(2, frameworkList.size());
        assertEquals(List.of("Spring Framework", "Hibernate"),
                frameworkList.stream().map(f -> f.getName()).toList());

    }

    @Test
    public void testQueryForObject_ShouldReturnFrameWorkJava() {
        Framework javaFramework = jdbcTemplate.queryForObject("select * from test.framework where id = ?",
                new FrameworkRowMapper(),
//                new BeanPropertyRowMapper<>(Framework.class),
                1);
    }

    @Test
    public void testUpdate_shouldDeleteFramework() {
        jdbcTemplate.update("DELETE FROM test.framework WHERE id = ?", 2);
        assertEquals(1, jdbcTemplate.query("select * from test.framework where language = ?",
                new FrameworkRowMapper(),
//                new BeanPropertyRowMapper<>(Framework.class),
                "JavaScript").size());
    }

    @Test
    public void testUpdateWithReturning_ShouldReturnId() throws SQLException {
//        KeyHolder keyHolder = new GeneratedKeyHolder();
        com.pankov.roadtosenior.holder.KeyHolder keyHolder = new com.pankov.roadtosenior.holder.GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "insert into test.framework (name, language, link, creationDate) values (?, ?, ?, ?)",
                    new String[]{"id"});
            statement.setString(1, "Veujs");
            statement.setString(2, "JavaScript");
            statement.setString(3, "https://vuejs.org");
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            return statement;
        }, keyHolder);

        assertEquals(5, keyHolder.getKey().longValue());
    }

    @AfterAll
    public static void after() {
    }

    private class FrameworkRowMapper implements com.pankov.roadtosenior.mapper.RowMapper<Framework> {
        @Override
        public Framework mapRow(ResultSet resultSet) throws SQLException {
            Timestamp creationDateTimestamp = resultSet.getTimestamp("creationDate");
            LocalDateTime creationDate = creationDateTimestamp.toLocalDateTime();
            return new Framework(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("language"),
                    resultSet.getString("link"),
                    creationDate
            );
        }
    }
}