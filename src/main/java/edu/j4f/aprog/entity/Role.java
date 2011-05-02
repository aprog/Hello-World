package edu.j4f.aprog.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
public class Role extends ObjectEntity {

	private String name;
	private boolean canClaim;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean canClaim() {
		return canClaim;
	}

	public void setCanClaim(boolean canClaim) {
		this.canClaim = canClaim;
	}

	@Override
	public boolean isValid() {
		try {
			checkEmptyString(getName());
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}

}
