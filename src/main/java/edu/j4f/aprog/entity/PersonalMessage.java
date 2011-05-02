package edu.j4f.aprog.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.OrderBy;

@Entity
@Table(name = "personalmessages")
public class PersonalMessage extends ObjectEntity implements Serializable {

	private static final long serialVersionUID = 2454433877199781452L;
	private String subject;
	private String body;
	private Timestamp creationTime;
	private boolean deleted;

	@OneToMany(orphanRemoval = true)
	@Cascade(CascadeType.ALL)
	@JoinTable(name = "pm2recipients",
			joinColumns = {@JoinColumn(name = "pmid")},
			inverseJoinColumns = {@JoinColumn(name = "pmrecipientid")})
	@OrderBy(clause = "id DESC")
	private List<PersonalMessageRecipient> recipients;

	@ManyToOne
	@JoinTable(name = "pm2users",
			joinColumns = {@JoinColumn(name = "pmid")},
			inverseJoinColumns = {@JoinColumn(name = "userid")})
	private User user;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Timestamp getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Timestamp creationTime) {
		this.creationTime = creationTime;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public void setRecipients(List<PersonalMessageRecipient> recipients) {
		this.recipients = recipients;
	}
	
	public List<PersonalMessageRecipient> getRecipients() {
		return recipients;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	@Override
	public boolean isValid() {
		try {
			checkEmptyString(subject);
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}

}
