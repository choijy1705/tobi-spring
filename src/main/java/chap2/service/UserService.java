package chap2.service;

import chap2.domain.User;

import java.sql.SQLException;

public interface UserService {
    void add(User user);
    void upgradeLevels() throws SQLException;
}
