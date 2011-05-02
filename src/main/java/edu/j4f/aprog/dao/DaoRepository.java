package edu.j4f.aprog.dao;

import org.hibernate.Session;

import edu.j4f.aprog.utils.HibernateUtil;


public class DaoRepository {

	private static DaoRepository instance;

	private DaoRepository() {

	}

	public static DaoRepository getInstance() {
		if (instance == null) {
			instance = new DaoRepository();
		}
		return instance;
	}

	protected Session getCurrentSession() {
		return HibernateUtil.getSessionFactory().getCurrentSession();
	}

	private BaseDao<?> instantiateDao(Class<?> daoClass) {
		try {
			BaseDao<?> dao = (BaseDao<?>) daoClass.newInstance();
			dao.setSession(getCurrentSession());
			return dao;
		} catch (Exception ex) {
			throw new RuntimeException("Can not instantiate DAO: " + daoClass,
					ex);
		}
	}

	public UserDao getUserDao() {
		return (UserDao) instantiateDao(UserDao.class);
	}

	public RoleDao getRoleDao() {
		return (RoleDao) instantiateDao(RoleDao.class);
	}

	public PersonalMessageDao getPersonalMessageDao() {
		return (PersonalMessageDao) instantiateDao(PersonalMessageDao.class);
	}
	
	public PersonalMessageRecipientDao getPersonalMessageRecipientDao() {
		return (PersonalMessageRecipientDao) instantiateDao(PersonalMessageRecipientDao.class);
	}

}
