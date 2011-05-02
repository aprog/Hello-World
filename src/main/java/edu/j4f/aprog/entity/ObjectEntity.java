package edu.j4f.aprog.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

@MappedSuperclass
public abstract class ObjectEntity implements IObjectEntity {

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

	public ObjectEntity() {
		
	}
	
	public Long getId() {
		return id;
	}

	protected void checkEmptyString(String string) throws IllegalArgumentException {
		if (string == null || string.trim().equals("")) {
			throw new IllegalArgumentException("String is empty");
		}
	}
	
	protected void checkValid(IObjectEntity entity) {
		if (entity != null && !entity.isValid()) {
			throw new IllegalArgumentException("Entity is invalid. Entity: " + entity);
		}
	}
	
}
