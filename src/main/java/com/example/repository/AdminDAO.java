package com.example.repository;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.example.entity.AdminUser;

@Repository
public class AdminDAO extends AbstractDAO<AdminUser> implements IAdminDAO{

	public AdminUser getAdmin(String username, String password) {
		// TODO Auto-generated method stub
		Query query = getSession()
				.createQuery("from AdminUser where username=:username and password=:password and isActive = 1");
		query.setString("username", username);
		query.setString("password", password);
		return (AdminUser) query.uniqueResult();
	}
}
