package com.pankov.roadtosenior.mapper;

import com.pankov.roadtosenior.mapper.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ColumnRowMapper implements RowMapper<Map<String, Object>> {

    @Override
    public Map<String, Object> mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        Map<String, Object> map = new HashMap<>(columnCount);

        for (int i = 1; i <= columnCount; ++i) {
            String column = determineColumnName(metaData, i);
            map.putIfAbsent(column, getColumnValue(resultSet, i));
        }

        return map;
    }

    private Object getColumnValue(ResultSet resultSet, int i) throws SQLException {
        return resultSet.getObject(i);
    }

    private String determineColumnName(ResultSetMetaData metaData, int columnIndex) throws SQLException {
        String name = metaData.getColumnLabel(columnIndex);
        if (name == null && name.isEmpty()) {
            name = metaData.getColumnName(columnIndex);
        }

        return name;
    }
}
