package chap2.service;

import chap2.dao.UserDao;
import chap2.dao.UserDaoJdbc;
import chap2.domain.Level;
import chap2.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static chap2.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static chap2.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserService testUserService;

    @Autowired
    UserDaoJdbc userDao;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    MailSender mailSender;

    @Autowired
    ApplicationContext context;

    List<User> users;

    static class TestUserServiceImpl extends UserServiceImpl {
        private final String id = "4";

        @Override
        protected void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) {
                throw new TestUserServiceException();
            }
            super.upgradeLevel(user);
        }

        @Override
        public List<User> getAll() {
            for(User user : super.getAll()) {
                super.update(user);
            }
            return null;
        }
    }

    static class TestUserServiceException extends RuntimeException {

    }

    @BeforeEach
    void setUp() {
        String email = "abc@abc.com";
        users = Arrays.asList(
                new User("1", "bumjin", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0, email),
                new User("2", "joytouch", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, email),
                new User("3", "erwins", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1, email),
                new User("4", "madnite1", "p4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD, email),
                new User("5", "green", "p5", Level.GOLD, 100, Integer.MAX_VALUE, email)
        );
    }

    @Test
    @DirtiesContext
    public void upgradeLevels() throws SQLException {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        UserDao mockUserDao = mock(UserDao.class);
        when(mockUserDao.getAll()).thenReturn(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MailSender mockMailSender = mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao).update(users.get(1));
        assertThat(users.get(1).getLevel()).isEqualTo(Level.SILVER);
        verify(mockUserDao).update(users.get(3));
        assertThat(users.get(3).getLevel()).isEqualTo(Level.GOLD);

        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0]).isEqualTo(users.get(1).getEmail());
        assertThat(mailMessages.get(1).getTo()[0]).isEqualTo(users.get(3).getEmail());
    }


    private void checkLevel(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if (upgraded) {
            assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel().nextLevel());
        } else {
            assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel());
        }
    }

    @Test
    void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel()).isEqualTo(userWithLevel.getLevel());
        assertThat(userWithoutLevelRead.getLevel()).isEqualTo(Level.BASIC);
    }

    @Test
    public void upgradeAllOrNothing() throws Exception {
        userDao.deleteAll();
        for (User user : users) {
            userDao.add(user);
        }

        try {
            this.testUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch (TestUserServiceException | SQLException e) {
            System.out.println("?????? ??????");
        }

        checkLevel(users.get(1), false);
    }

    @Test
    public void advisorAutoProxyCreator() {
        assertThat(testUserService).isInstanceOf(java.lang.reflect.Proxy.class);
    }

    @Test
    public void readOnlyTransactionAttribute() {
        assertThatThrownBy(() -> {
            testUserService.getAll();
        }).isInstanceOf(TransientDataAccessResourceException.class);

    }

    @Test
    public void transactionSync() {
        userDao.deleteAll();
        assertThat(userDao.getCount()).isEqualTo(0);

        DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);

        userService.deleteAll();

        userService.add(users.get(0));
        userService.add(users.get(1));

        assertThat(userDao.getCount()).isEqualTo(2);

        transactionManager.rollback(txStatus);

        assertThat(userDao.getCount()).isEqualTo(0);
    }
}