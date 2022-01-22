package com.pankov.roadtosenior;

import com.pankov.roadtosenior.holder.KeyHolder;
import com.pankov.roadtosenior.mapper.ColumnRowMapper;
import com.pankov.roadtosenior.mapper.RowMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class JdbcTemplate {
    private DataSource dataSource;

    public JdbcTemplate() {
    }

    public JdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... params) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            try {
                setParameters(preparedStatement, params);
            } catch (Exception exception) {
                throw new RuntimeException(String
                        .format("Error during set parameters to PreparedStatement -- %s", params), exception);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("Object matching the parameters was not found", Arrays.toString(params));
                }

                return rowMapper.mapRow(resultSet, 0);
            }
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public <T> List<T> query(String sql, RowMapper<T> rowMapper) {
        List<T> list = new ArrayList<>(1);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                T object = rowMapper.mapRow(resultSet, 0);
                list.add(object);
            }

            return list;

        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... params) {
        List<T> list = new ArrayList<>(1);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            try {
                setParameters(preparedStatement, params);
            } catch (Exception exception) {
                throw new RuntimeException("Error during set parameters to PreparedStatement", exception);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    T object = rowMapper.mapRow(resultSet, 0);
                    list.add(object);
                }
            }

            return list;

        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public int update(String sql, Object... params) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            try {
                setParameters(preparedStatement, params);
            } catch (Exception exception) {
                throw new RuntimeException("Error during set parameters to PreparedStatement", exception);
            }

            return preparedStatement.executeUpdate();

        } catch (SQLException exception) {
            throw new RuntimeException("Cannot execute update", exception);
        }
    }

    public int update(PreparedStatementCreator preparedStatementCreator, KeyHolder keyHolder) throws SQLException {

        try (PreparedStatement preparedStatement = preparedStatementCreator.create(dataSource.getConnection())) {
            int updateCount = preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                ColumnRowMapper rowMapper = new ColumnRowMapper();
                List<Map<String, Object>> list = keyHolder.getKeyList();
                list.clear();

                while (resultSet.next()) {
                    Map<String, Object> object = rowMapper.mapRow(resultSet, 0);
                    list.add(object);
                }
            }

            return updateCount;
        } catch (SQLException exception) {
            throw new RuntimeException("Cannot execute update", exception);
        }

    }

    void setParameters(PreparedStatement statement, Object... params) throws Exception {
        for (int i = 0; i < params.length; i++) {
            Class<?> clazz = params[i].getClass();
            Class<?> parameterClass;
            String setterType;

            if (isWrapperType(clazz)) {
                parameterClass = (Class<?>) clazz.getField("TYPE").get(null);
                setterType = parameterClass.toString().substring(0, 1).toUpperCase() +
                        parameterClass.toString().substring(1);
            } else {
                parameterClass = clazz;
                setterType = clazz.getSimpleName();
            }
            /*try {
                parameterClass = (Class<?>) clazz.getField("TYPE").get(null);
                System.out.println(parameterClass);
                setterType = parameterClass.toString().substring(0, 1).toUpperCase() +
                        parameterClass.toString().substring(1);
            } catch (NoSuchFieldException exception) {
                parameterClass = clazz;
                setterType = clazz.getSimpleName();
            }*/

            statement.getClass()
                    .getMethod("set" + setterType,
                            int.class, parameterClass)
                    .invoke(statement, i + 1, params[i]);
        }
    }

    boolean isWrapperType(Class<?> clazz) {
        return clazz.equals(Boolean.class) ||
                clazz.equals(Integer.class) ||
                clazz.equals(Character.class) ||
                clazz.equals(Byte.class) ||
                clazz.equals(Short.class) ||
                clazz.equals(Double.class) ||
                clazz.equals(Long.class) ||
                clazz.equals(Float.class);
    }
}
