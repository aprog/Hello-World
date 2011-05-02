package edu.j4f.aprog.dao;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import edu.j4f.aprog.entity.Role;

public class RoleDao extends BaseDao<Role> {

	public Role getByName(String name) {
		List<Role> roles = findByCriteria(Restrictions.eq("name", name));
		return roles.isEmpty() ? null : roles.get(0);
	}

}
