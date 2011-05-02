package edu.j4f.aprog.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "pmrecipients")
public class PersonalMessageRecipient extends ObjectEntity implements Serializable {

	private static final long serialVersionUID = -4924476921496351011L;

	@ManyToOne
	@JoinTable(name = "pm2recipients",
			joinColumns = {@JoinColumn(name = "pmrecipientid")},
			inverseJoinColumns = {@JoinColumn(name = "pmid")})
	private PersonalMessage personalMessage;
	
	@OneToOne
	@JoinColumn(name = "userid")
	private User user;
	private boolean isRead;
	private boolean deleted;

	/*
	public void setPersonalMessage(PersonalMessage personalMessage) {
		this.personalMessage = personalMessage;
	}
	*/

	public PersonalMessage getPersonalMessage() {
		return personalMessage;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public boolean getIsRead() {
		return isRead;
	}

	public void setIsRead(boolean isRead) {
		this.isRead = isRead;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public boolean isValid() {
		return true; // nothing to check
	}

}
