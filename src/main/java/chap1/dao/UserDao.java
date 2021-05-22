package chap1.dao;

import chap1.domain.User;

import java.sql.*;

public class UserDao {

    public void add(User user) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:13306/tobi_spring?useSSL=false&serverTimezone=UTC",
                "root", "root");

        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO USERS(id, name, password) VALUES(?,?,?)");
        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());

        ps.executeUpdate();

        ps.close();
        conn.close();
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:13306/tobi_spring?useSSL=false&serverTimezone=UTC",
                "root", "root");

        PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM USERS WHERE id = ?"
        );

        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();
        rs.next();
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));

        rs.close();
        ps.close();
        conn.close();

        return user;
    }
}
