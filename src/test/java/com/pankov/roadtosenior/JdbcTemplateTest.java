package com.pankov.roadtosenior;

import com.pankov.roadtosenior.holder.KeyHolder;
import com.pankov.roadtosenior.holder.GeneratedKeyHolder;
import com.pankov.roadtosenior.mapper.RowMapper;
import org.apache.commons.dbcp2.BasicDataSource;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JdbcTemplateTest {

    private final static String URL = "jdbc:h2:mem:test";

    private static BasicDataSource ds = new BasicDataSource();
    private JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

    static {
        ds.setUrl(URL);
    }

    @BeforeAll
    public static void before() throws SQLException {
        RunScript.execute(ds.getConnection(),
                new BufferedReader(new InputStreamReader(
                        JdbcTemplateTest.class.getClassLoader().
                                getResourceAsStream("test.sql"), StandardCharsets.UTF_8)));
    }

    @Test
    public void testQueryMethod_shouldReturnAList() {
        List<Framework> frameworkList = jdbcTemplate.query("SELECT * from test.framework",
                new FrameworkRowMapper());
        assertNotNull(frameworkList);
        assertEquals(5, frameworkList.size());
        assertEquals(List.of("Spring Framework", "Angular", "Laravel", "Hibernate", "Veujs"),
                frameworkList.stream().map(f -> f.getName()).toList());

    }

    @Test
    public void testQueryWithParapetersMethod_shouldReturnAList_searchByInt() {
        List<Framework> frameworkList = jdbcTemplate.query("SELECT * from test.framework WHERE language = ?",
                new FrameworkRowMapper(), "Java");
        assertNotNull(frameworkList);
        assertEquals(2, frameworkList.size());
        assertEquals(List.of("Spring Framework", "Hibernate"),
                frameworkList.stream().map(f -> f.getName()).toList());

    }

    @Test
    public void testQueryWithParapetersMethod_shouldReturnAList_searchByString() {
        List<Framework> frameworkList = jdbcTemplate.query("SELECT * from test.framework WHERE language like ?",
                new FrameworkRowMapper(), "Java%");
        assertNotNull(frameworkList);
        assertEquals(3, frameworkList.size());
        frameworkList.stream().forEach(System.out::println);
        assertEquals(List.of("Spring Framework", "Hibernate", "Veujs"),
                frameworkList.stream().map(f -> f.getName()).toList());

    }

    @Test
    public void testQueryForObject_ShouldReturnFrameWorkJava_searchByInt() {
        Framework javaFramework = jdbcTemplate.queryForObject("select * from test.framework where id = ?",
                new FrameworkRowMapper(),
                1);
        assertEquals(new Framework(1, "Spring Framework", "Java",
                "https://spring.io", LocalDateTime.parse("2017-10-01T21:22:23")), javaFramework);
    }


    @Test
    public void testQueryForObject_ShouldReturnFrameWorkJava_searchByString() {
        Framework javaFramework = jdbcTemplate.queryForObject("select * from test.framework where name = ?",
                new FrameworkRowMapper(),
                "Laravel");
        assertEquals(new Framework(3, "Laravel", "PHP",
                "https://laravel.com", LocalDateTime.parse("2017-10-03T03:22:23")), javaFramework);
    }

    @Test
    public void testQueryForObject_ShouldThrowException() {
        assertThrows(RuntimeException.class, () ->
                jdbcTemplate.queryForObject("select * from test.framework where id = ?",
                        new FrameworkRowMapper()));
    }

    @Test
    public void testUpdate_shouldDeleteFramework() {
        jdbcTemplate.update("DELETE FROM test.framework WHERE id = ?", 2);
        assertEquals(1, jdbcTemplate.query("select * from test.framework where language = ?",
                new FrameworkRowMapper(),
                "JavaScript").size());
    }

    @Test
    public void testUpdateWithReturning_ShouldReturnId() throws SQLException {
        KeyHolder keyHolder = new GeneratedKeyHolder();
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

    private class FrameworkRowMapper implements RowMapper<Framework> {
        @Override
        public Framework mapRow(ResultSet resultSet, int rowNum) throws SQLException {
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