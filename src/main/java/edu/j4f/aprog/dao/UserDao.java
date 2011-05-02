package edu.j4f.aprog.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Restrictions;

import edu.j4f.aprog.entity.Role;
import edu.j4f.aprog.entity.User;

public class UserDao extends BaseDao<User> {

	public User getByName(String name) {
		List<User> users = findByCriteria(Restrictions.eq("name", name));
		return users.isEmpty() ? null : users.get(0);
	}
	
	public User get(String name, String password) {
		List<User> users = findByCriteria(Restrictions.eq("name", name), Restrictions.eq("password", password));
		return users.isEmpty() ? null : users.get(0);
	}

	public Set<User> getAllAdmins() {
		RoleDao roleDao = DaoRepository.getInstance().getRoleDao();
		Role adminRole = roleDao.getByName("admin");
		Set<User> admins = new HashSet<User>(findByCriteria(Restrictions.eq("roleId", adminRole)));
		return admins;
	}

}
