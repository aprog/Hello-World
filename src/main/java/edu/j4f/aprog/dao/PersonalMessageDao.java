package edu.j4f.aprog.dao;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import edu.j4f.aprog.entity.PersonalMessage;
import edu.j4f.aprog.entity.User;

public class PersonalMessageDao extends BaseDao<PersonalMessage> {

	public PersonalMessage getUserMessage(User user, Long pmId) {
		List<PersonalMessage> personalMessages = findByCriteria(Restrictions.eq("user", user), Restrictions.eq("id", pmId));
		return personalMessages.size() > 0 ? personalMessages.get(0) : null;
		/*
		PersonalMessage personalMessage = get(pmId);
		if (getPersonalMessages(user).contains(personalMessage)) {
			return personalMessage;
		}
		return null;
		*/
	}

	public PersonalMessage getById(User user, Long id) {
		List<PersonalMessage> personalMessages = findByCriteria(Restrictions.eq("user", user), Restrictions.eq("id", id));
		return personalMessages.size() > 0 ? personalMessages.get(0) : null;
	}

	public List<PersonalMessage> getPersonalMessages(User user) {
		List<PersonalMessage> personalMessages = findByCriteria(Restrictions.eq("user", user), Restrictions.eq("deleted", false));
		return personalMessages;
	}

}
