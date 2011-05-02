package edu.j4f.aprog.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.j4f.aprog.dao.DaoRepository;
import edu.j4f.aprog.dao.RoleDao;
import edu.j4f.aprog.entity.Role;

public class RoleService {
	
	private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

	RoleDao roleDao;
	
	public RoleService() {
		this.roleDao = DaoRepository.getInstance().getRoleDao();
	}
	
	public void add(Role role) {
		logger.debug("Add role with name: " + role.getName());
		roleDao.add(role);
	}
	
	public Role getById(Long id) {
		logger.debug("Get role with id: " + id);
		return roleDao.get(id);
	}
	
	public Role getByName(String name) {
		return roleDao.getByName(name);
	}

}
