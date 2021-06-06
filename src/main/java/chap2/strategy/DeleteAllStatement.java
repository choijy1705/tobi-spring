package chap2.strategy;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DeleteAllStatement implements StatementStrategy{
    @Override
    public PreparedStatement makePreparedStatement(Connection c) throws Exception {
        PreparedStatement ps = c.prepareStatement("delete from users");
        return ps;
    }
}
