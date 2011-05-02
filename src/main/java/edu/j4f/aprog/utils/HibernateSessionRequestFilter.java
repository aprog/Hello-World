package edu.j4f.aprog.utils;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateSessionRequestFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(HibernateSessionRequestFilter.class);

	public HibernateSessionRequestFilter() {

	}

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		logger.debug("Starting transaction ...");
		HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
		HibernateUtil.getSessionFactory().getCurrentSession().enableFilter("deletedMessages").setParameter("showDeletedMessages", false);

		chain.doFilter(request, response);
		logger.debug("Committing transaction ...");
		HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
	}

	public void init(FilterConfig fConfig) throws ServletException {
		logger.debug("Initializing session factory ...");
		HibernateUtil.getSessionFactory();
	}

}
