package edu.j4f.aprog.dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import edu.j4f.aprog.entity.IObjectEntity;

public abstract class BaseDao<EntityType extends IObjectEntity> {

	private Session session;
	private Class<EntityType> entityClass;

	@SuppressWarnings("unchecked")
	public BaseDao() {
		this.entityClass = (Class<EntityType>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0]; // magic number, any ideas to fix it?
	}

	public Session getSession() {
		if (session == null) {
			throw new IllegalStateException(
					"Session has not been set on DAO before usage");
		}
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Class<EntityType> getEntityClass() {
		return entityClass;
	}

	@SuppressWarnings("unchecked")
	public EntityType get(Long id) {
		EntityType entity;
		entity = (EntityType) getSession().load(getEntityClass(), id);
		return entity;
	}

	public List<EntityType> getAll() {
		return findByCriteria();
	}

	public EntityType add(EntityType entity) {
		if (entity == null) {
			throw new IllegalArgumentException("Entity is null");
		}
		if (!entity.isValid()) {
			throw new IllegalArgumentException("Entity is invalid");
		}
		getSession().saveOrUpdate(entity);
		return entity;
	}

	public void delete(Long id) {
		EntityType entity = get(id);
		if (entity == null) {
			throw new EntityNotFoundException("Entity with id: " + id
					+ " not found");
		}
		getSession().delete(entity);
	}

	@SuppressWarnings("unchecked")
	protected List<EntityType> findByCriteria(Criterion... criterion) {
		Criteria criteria = getSession().createCriteria(entityClass).addOrder(Order.desc("id"));
		for (Criterion c : criterion) {
			criteria.add(c);
		}
		return criteria.list();
	}

}
