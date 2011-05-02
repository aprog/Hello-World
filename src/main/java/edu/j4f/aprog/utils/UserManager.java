package edu.j4f.aprog.utils;

import javax.faces.bean.SessionScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.j4f.aprog.entity.User;
import edu.j4f.aprog.service.UserService;

@SessionScoped
public class UserManager {
	
	private static final Logger logger = LoggerFactory.getLogger(UserManager.class);

	private static UserManager instance = null;
	private User user = null;

	private UserManager() {

	}

	public static UserManager getInstance() {
		if (instance == null) {
			instance = new UserManager();
		}
		return instance;
	}

	public void setUser(String userName, String password) {
		try {
			logger.debug("Set user with name: " + userName);
			UserService userService = new UserService();
			user = userService.getUser(userName, password);
		} catch (Exception e) {
			logger.error("Failed to set user with name: " + userName, e);
			user = null;
		}
	}

	public User getCurrentUser() {
		if (user != null) {
			return (User) HibernateUtil.getSessionFactory().getCurrentSession().merge(user);
		}
		return user;
	}

	public void resetUser() {
		this.user = null;
		// not using because resending of request cause error page
		// FacesContext.getCurrentInstance().getExternalContext()
		// .invalidateSession();
	}

}
