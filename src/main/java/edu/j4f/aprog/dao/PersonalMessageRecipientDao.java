package edu.j4f.aprog.dao;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import edu.j4f.aprog.entity.PersonalMessage;
import edu.j4f.aprog.entity.PersonalMessageRecipient;
import edu.j4f.aprog.entity.User;

public class PersonalMessageRecipientDao extends BaseDao<PersonalMessageRecipient> {

	public PersonalMessageRecipient getUserMessage(User user, Long pmrId) {
		List<PersonalMessageRecipient> pmrs = findByCriteria(Restrictions.eq("user", user), Restrictions.eq("id", pmrId));
		return pmrs.size() > 0 ? pmrs.get(0) : null;
	}

	public boolean canDeletePM(PersonalMessage personalMessage) {
		List<PersonalMessageRecipient> pmrs = findByCriteria(Restrictions.eq("personalMessage", personalMessage), Restrictions.eq("deleted", false));
		return pmrs.size() == 0;
	}

	public int getNewMessagesCount(User user) {
		List<PersonalMessageRecipient> pmrs = findByCriteria(Restrictions.eq("user", user), Restrictions.eq("isRead", false));
		return pmrs.size();
	}

	public PersonalMessageRecipient getById(User user, Long id) {
		List<PersonalMessageRecipient> pmrs = findByCriteria(Restrictions.eq("user", user), Restrictions.eq("id", id));
		return pmrs.size() > 0 ? pmrs.get(0) : null;
	}

	public List<PersonalMessageRecipient> getUserMessages(User user) {
		List<PersonalMessageRecipient> pmrs = findByCriteria(Restrictions.eq("user", user), Restrictions.eq("deleted", false));
		return pmrs;
	}

}
