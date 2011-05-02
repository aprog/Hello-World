package edu.j4f.aprog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.j4f.aprog.dao.DaoRepository;
import edu.j4f.aprog.dao.PersonalMessageDao;
import edu.j4f.aprog.dao.PersonalMessageRecipientDao;
import edu.j4f.aprog.entity.PersonalMessage;
import edu.j4f.aprog.entity.PersonalMessageRecipient;
import edu.j4f.aprog.entity.User;

public class MessageService {

	private PersonalMessageDao PMDao;
	private PersonalMessageRecipientDao PMRDao;

	public MessageService() {
		this.PMDao = DaoRepository.getInstance().getPersonalMessageDao();
		this.PMRDao = DaoRepository.getInstance().getPersonalMessageRecipientDao();
	}

	public PersonalMessageRecipient getUserAsMessageRecipient(User user) {
		PersonalMessageRecipient pmr = new PersonalMessageRecipient();
		pmr.setDeleted(false);
		pmr.setIsRead(false);
		pmr.setUser(user);
		return pmr;
	}

	public void sendMessage(User userFrom, User userTo, PersonalMessage personalMessage) {
		List<PersonalMessageRecipient> pmrs = new ArrayList<PersonalMessageRecipient>();
		pmrs.add(getUserAsMessageRecipient(userTo));
		personalMessage.setRecipients(pmrs);
		userFrom.getPersonalMessages().add(personalMessage);
	}

	public void sendMessageToUsers(User userFrom, Set<User> userTo, PersonalMessage personalMessage) {
		List<PersonalMessageRecipient> pmrs = new ArrayList<PersonalMessageRecipient>();
		for (User user : userTo) {
			pmrs.add(getUserAsMessageRecipient(user));
		}
		personalMessage.setRecipients(pmrs);
		userFrom.getPersonalMessages().add(personalMessage);
	}
	
	public void sendMessageToAdmins(User userFrom, PersonalMessage personalMessage) {
		UserService userService = new UserService();
		Set<User> admins = userService.getAllAdmins();
		sendMessageToUsers(userFrom, admins, personalMessage);
	}

	public void setRecipientMessageRead(User user, Long messageId) {
		if (user == null) {
			throw new NullPointerException("User was not correctly set");
		}

		PersonalMessageRecipient pmr = PMRDao.getUserMessage(user, messageId);
		if (pmr == null) {
			throw new IllegalArgumentException("Message with id: " + messageId + " was not found for username: " + user.getName());
		}
		pmr.setIsRead(true);
	}

	private boolean canDeletePM(PersonalMessage personalMessage) {
		boolean canDelete = false;
		if (personalMessage.isDeleted()) {
			canDelete = PMRDao.canDeletePM(personalMessage);
		}
		return canDelete;
	}

	public void deleteRecipientMessage(User user, Long messageId) {
		if (user == null) {
			throw new NullPointerException("User was not correctly set");
		}

		PersonalMessageRecipient pmr = PMRDao.getUserMessage(user, messageId);
		if (pmr == null) {
			throw new IllegalArgumentException("Message with id: " + messageId + " was not found for username: " + user.getName());
		}

		pmr.setDeleted(true);

		PersonalMessage personalMessage = pmr.getPersonalMessage();
		if (canDeletePM(personalMessage)) {
			User messageOwner = personalMessage.getUser();
			messageOwner.getPersonalMessages().remove(personalMessage);
			//PMDao.delete(personalMessage.getId());
		}
	}

	public void deletePersonalMessage(User user, Long pmId) {
		if (user == null) {
			throw new NullPointerException("User was not correctly set");
		}

		PersonalMessage pm = PMDao.getUserMessage(user, pmId);
		if (pm == null) {
			throw new IllegalArgumentException("Message with id: " + pmId + " was not found for username: " + user.getName());
		}

		pm.setDeleted(true);
		if (canDeletePM(pm)) {
			user.getPersonalMessages().remove(pm);
			//PMDao.delete(pm.getId());
		}
	}

	public int getNewMessagesCount(User user) {
		if (user == null) {
			throw new NullPointerException("User was not correctly set");
		}

		return PMRDao.getNewMessagesCount(user);
	}

	public PersonalMessageRecipient getPersonalMessageRecipient(User user, Long id) {
		if (user == null) {
			throw new NullPointerException("User was not correctly set");
		}

		return PMRDao.getById(user, id);
	}

	public List<PersonalMessageRecipient> getPersonalMessagesRecipient(User user) {
		if (user == null) {
			throw new NullPointerException("User was not correctly set");
		}

		return PMRDao.getUserMessages(user);
	}

	
	public PersonalMessage getPersonalMessage(User user, Long id) {
		if (user == null) {
			throw new NullPointerException("User was not correctly set");
		}

		return PMDao.getById(user, id);
	}

	public List<PersonalMessage> getPersonalMessages(User user) {
		return PMDao.getPersonalMessages(user);
	}

}
