package edu.j4f.aprog.web;

import java.io.IOException;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.j4f.aprog.service.UserService;
import edu.j4f.aprog.utils.HibernateUtil;
import edu.j4f.aprog.utils.UserManager;


@ManagedBean(name = "user")
@RequestScoped
public class UserBean {

	private static final Logger logger = LoggerFactory.getLogger(UserBean.class);

	@Length(min = 3, max = 255)
	private String name;
	private String password;
	@Email
	private String email;
	private boolean isProcessed;
	private String refererUrl;

	public boolean getIsProcessed() {
		return isProcessed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean getIsLoggedIn() {
		return UserManager.getInstance().getCurrentUser() == null ? false
				: true;
	}

	public void addUser() {
		FacesContext context = FacesContext.getCurrentInstance();
		UserService userService = new UserService();
		try {
			logger.debug("Add new user with name: " + getName());
			userService.addUser(getName(), getPassword(), getEmail());
			HibernateUtil.getSessionFactory().getCurrentSession().flush();
			isProcessed = true;
		} catch (Exception e) {
			logger.error("Failed to add new user with name: " + getName(), e);
			FacesMessage message = new FacesMessage(context.getApplication().getResourceBundle(context, "i18n").getString("registration.form.fail"));
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage("", message);
			isProcessed = false;
		}
	}

	public void loginUser() {
		FacesContext context = FacesContext.getCurrentInstance();
		UserManager.getInstance().setUser(getName(), getPassword());
		logger.debug("Set user with name: " + getName());
		if (UserManager.getInstance().getCurrentUser() == null) {
			logger.error("Failed to set user with name: " + getName());
			// i18n - variable, that was defined in faces-config.xml
			FacesMessage message = new FacesMessage(context.getApplication().getResourceBundle(context, "i18n").getString("login.form.fail"));
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage("", message);
		} else {
			//String redirect = context.getExternalContext().getRequestContextPath();
			//String redirect = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("requestURI").toString();
			String redirect = getRefererUrl();
			try {
				logger.debug("Redirect to: " + redirect);
				FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("refererUrl");
				context.getExternalContext().redirect(redirect);
			} catch (IOException e) {
				logger.error("Failed to redirect to: " + redirect, e);
			}
		}
	}

	public void logoutUser() {
		logger.debug("Logout user");
		UserManager.getInstance().resetUser();
	}

	public String getRefererUrl() {
		Map<String, String> headerMap = FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap();
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		if (headerMap.containsKey("referer") && !sessionMap.containsKey("refererUrl")) {
			refererUrl = headerMap.get("referer");
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("refererUrl", refererUrl);
		} else if (sessionMap.containsKey("refererUrl")) {
			refererUrl = sessionMap.get("refererUrl").toString();
		}
		return refererUrl;
	}

}
