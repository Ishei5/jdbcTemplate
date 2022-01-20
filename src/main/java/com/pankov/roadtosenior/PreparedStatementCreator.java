package com.pankov.roadtosenior;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementCreator {
    PreparedStatement create(Connection connection) throws SQLException;
}
