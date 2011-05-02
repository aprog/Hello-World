package edu.j4f.aprog.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {

	private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);

	private static final SessionFactory sessionFactory = builSessionFactory();

	private static SessionFactory builSessionFactory() {
		try {
			logger.debug("Get session factory");
			return new Configuration().configure().buildSessionFactory();
		} catch (Throwable e) {
			logger.error("Failed to get session factory", e);
			throw new ExceptionInInitializerError(e);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}
