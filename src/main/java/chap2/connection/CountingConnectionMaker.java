package chap2.connection;

import chap1.connection.ConnectionMaker;

import java.sql.Connection;
import java.sql.SQLException;

public class CountingConnectionMaker implements chap1.connection.ConnectionMaker {
    int counter = 0;
    private chap1.connection.ConnectionMaker realConnectionMaker;

    public CountingConnectionMaker(ConnectionMaker realConnectionMaker) {
        this.realConnectionMaker = realConnectionMaker;
    }

    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        this.counter++;
        return realConnectionMaker.makeConnection();
    }

    public int getCounter() {
        return counter;
    }
}
