package service;

import dao.LoginLogDao;
import dao.UserDao;
import domain.LoginLog;
import domain.User;
import exception.UserExitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserService {
    private UserDao userDao;
    private LoginLogDao loginLogDao;


    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setLoginLogDao(LoginLogDao loginLogDao) {
        this.loginLogDao = loginLogDao;
    }

    public void register(User user) throws UserExitException {
        User u = userDao.getUserByUserName(user.getUserName());
        if (u != null) {
            throw new UserExitException("user exist");
        } else {
            user.setCredits(100);
            user.setUserType(1);
            userDao.save(user);
        }
    }

    public void update(User user) {
        userDao.update(user);
    }

    public User getUserByUserName(String userName) {
        return userDao.getUserByUserName(userName);
    }

    public void lockUser(String userName) {
        User user = userDao.getUserByUserName(userName);
        user.setLocked(User.USER_LOCK);
        userDao.update(user);
    }

    public void unlockUser(String userName) {
        User user = userDao.getUserByUserName(userName);
        user.setLocked(User.USER_UNLOCK);
        userDao.update(user);
    }

    public User getUserById(int userId) {
        return userDao.get(userId);
    }

    //    search user by user name
    public List<User> queryUserByUserName(String userName) {
        return userDao.queryUserByUserName(userName);
    }

    public List<User> getAllUser() {
        return userDao.loadAll();
    }

    // update information of user and create a new login information
    public void loginSuccess(User user) {
        user.setCredits(5 + user.getCredits());
        userDao.update(user);

        LoginLog loginLog = new LoginLog();
        loginLog.setIp(user.getLastIp());
        loginLog.setUser(user);
        loginLog.setLoginDate(new Date());
        loginLogDao.update(loginLog);
    }


}
