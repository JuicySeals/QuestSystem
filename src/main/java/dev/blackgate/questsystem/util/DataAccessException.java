package dev.blackgate.questsystem.util;

import java.sql.SQLException;

public class DataAccessException extends RuntimeException {
    public DataAccessException(SQLException cause) {
        super(cause);
    }
}
