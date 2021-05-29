package chap2.connection;

import chap1.connection.ConnectionMaker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DConnectionMaker implements ConnectionMaker {
    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        //D사의 독자적인 방법으로 Connection을 생성하는 코드
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:13306/tobi_spring?useSSL=false&serverTimezone=UTC",
                "root", "root");
    }
}
