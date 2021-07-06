package chap2.service;

import chap2.domain.User;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

@Transactional
public interface UserService {
    void add(User user);

    @Transactional(readOnly = true)
    User get(String id);

    @Transactional(readOnly = true)
    List<User> getAll();
    void deleteAll();
    void update(User user);
    void upgradeLevels() throws SQLException;
}
