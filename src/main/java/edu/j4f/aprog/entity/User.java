package edu.j4f.aprog.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.ParamDef;

@Entity
@Table(name = "users",
		uniqueConstraints = @UniqueConstraint(columnNames = {"name"})
)

@FilterDef(name = "deletedMessages", 
		parameters = @ParamDef(name="showDeletedMessages", type="boolean"), 
		defaultCondition = "deleted = :showDeletedMessages")
public class User extends ObjectEntity implements Serializable {

	private static final long serialVersionUID = 970870639633089395L;

	@Column(length = 100)
	@NotNull
	private String name;

	@Column(length = 100)
	@NotNull
	private String password;

	private String email;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "roleId")
	private Role roleId;

	@OneToMany(orphanRemoval = true)
	@Cascade(CascadeType.ALL)
	@JoinTable(name = "pm2users",
		joinColumns = {@JoinColumn(name = "userid")},
		inverseJoinColumns = {@JoinColumn(name = "pmid")})
	//@Filter(name = "deletedMessages")
	@OrderBy(clause = "pmid DESC")
	private List<PersonalMessage> personalMessages;

	@OneToMany(orphanRemoval = true)
	@JoinColumn(name = "userid", referencedColumnName = "id")
	//@Filter(name = "deletedMessages")
	@OrderBy(clause = "id DESC")
	private List<PersonalMessageRecipient> personalMessagesIncoming;

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

	public void setRole(Role role) {
		this.roleId = role;
	}

	public Role getRole() {
		return roleId;
	}

	public boolean isValid() {
		try {
			checkEmptyString(getName());
			checkEmptyString(getPassword());
		} catch (IllegalArgumentException ex) {
			return false;
		}
		return true;
	}

	public void setPersonalMessages(List<PersonalMessage> personalMessages) {
		this.personalMessages = personalMessages;
	}

	public List<PersonalMessage> getPersonalMessages() {
		return personalMessages;
	}

	public void setPersonalMessagesIncoming(List<PersonalMessageRecipient> personalMessagesIncoming) {
		this.personalMessagesIncoming = personalMessagesIncoming;
	}

	public List<PersonalMessageRecipient> getPersonalMessagesIncoming() {
		return personalMessagesIncoming;
	}

}
