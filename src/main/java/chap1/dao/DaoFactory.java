package chap1.dao;

import chap1.connection.ConnectionMaker;
import chap1.connection.CountingConnectionMaker;
import chap1.connection.DConnectionMaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration //애플리케이션 컨텍스트 또는 빈 팩토리가 사용할 설정정보라는 표시
public class DaoFactory {

    @Bean // 오브젝트 생성을 담당하는 IoC용 메소드라는 표시
    public UserDao userDao() {
        UserDao userDao = new UserDao();
        userDao.setDataSource(dataSource());
        return userDao;
    }

//    public AccountDao accountDao() {
//        return new AccountDao(realConnectionMaker());
//    }
//
//    public MessageDao messageDao() {
//        return new MessageDao(realConnectionMaker());
//    }

//    @Bean
//    public ConnectionMaker connectionMaker() {
//        return new CountingConnectionMaker(realConnectionMaker());
//    }
    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost:13306/tobi_spring?useSSL=false&serverTimezone=UTC");
        dataSource.setUsername("root");
        dataSource.setPassword("root");

        return dataSource;
    }
//
//    @Bean
//    public ConnectionMaker realConnectionMaker() {
//        ConnectionMaker connectionMaker = new DConnectionMaker();
//        return connectionMaker;
//    }


}
