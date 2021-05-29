package chap2;

import chap2.dao.UserDao;
import chap2.domain.User;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.sql.SQLException;

public class UserDaoTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        //AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        GenericXmlApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
        UserDao dao = context.getBean("userDao2", UserDao.class);

        User user = new User();
        user.setId("chap2");
        user.setName("백기선");
        user.setPassword("married");

        dao.add(user);

        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());

        if (!user.getName().equals(user2.getName())) {
            System.out.println("테스트 실패 (name)");
        } else if (!user.getPassword().equals(user2.getPassword())) {
            System.out.println("테스트 실패 (password");
        } else {
            System.out.println("조회 테스트 성공");
        }
    }
}
