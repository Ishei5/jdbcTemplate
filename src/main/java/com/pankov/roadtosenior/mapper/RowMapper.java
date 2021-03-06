package com.pankov.roadtosenior.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper<T> {

    T mapRow(ResultSet resultSet, int rowNum) throws SQLException;
}
