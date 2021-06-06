package chap2.strategy;

import java.sql.Connection;
import java.sql.PreparedStatement;

public interface StatementStrategy {
    PreparedStatement makePreparedStatement(Connection c) throws Exception;
}
