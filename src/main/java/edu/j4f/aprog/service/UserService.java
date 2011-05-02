package edu.j4f.aprog.service;

import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.j4f.aprog.dao.DaoRepository;
import edu.j4f.aprog.dao.UserDao;
import edu.j4f.aprog.entity.User;
import edu.j4f.aprog.utils.PropertyManager;

public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	UserDao userDao;

	public UserService() {
		this.userDao = DaoRepository.getInstance().getUserDao();
	}

	public void addUser(String name, String rawPassword, String email) {
		logger.debug("Add user with name: " + name + "; password: " + encryptPassword(rawPassword) + "; email: " + email);
		User user = new User();
		user.setName(name);
		user.setPassword(encryptPassword(rawPassword));
		user.setEmail(email);
		user.setRole(new RoleService().getByName("member"));
		userDao.add(user);
	}

	public String encryptPassword(String string) {
		String salt = PropertyManager.getInstance().getProperty("security.password.salt");
		String sugar = PropertyManager.getInstance().getProperty("security.password.sugar");
		String pepper = PropertyManager.getInstance().getProperty("security.password.pepper");
		return DigestUtils.sha256Hex(salt.concat(sugar.concat(pepper.concat(string))));
	}

	public boolean userExists(String userName) {
		if (userDao.getByName(userName) != null) {
			return true;
		}
		return false;
	}

	public User getUser(String userName, String rawPassword) {
		logger.debug("Get user with name: " + userName + "; password: " + encryptPassword(rawPassword));
		return userDao.get(userName, encryptPassword(rawPassword));
	}

	public List<User> getAllUsers() {
		return userDao.getAll();
	}

	public Set<User> getAllAdmins() {
		// TODO get users with role
		return userDao.getAllAdmins();
	}

}
