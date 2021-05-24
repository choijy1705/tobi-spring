package chap1.dao;

import chap1.connection.ConnectionMaker;
import chap1.connection.DConnectionMaker;

public class DaoFactory {

    public UserDao userDao() {
        ConnectionMaker connectionMaker = new DConnectionMaker();
        return new UserDao(connectionMaker);
    }
}
