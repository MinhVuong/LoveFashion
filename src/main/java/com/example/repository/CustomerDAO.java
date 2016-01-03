package com.example.repository;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.example.entity.CustomerEntity;

@Repository
public class CustomerDAO extends AbstractDAO<CustomerEntity> implements
		ICustomerDAO {

	public CustomerEntity getCustomer(String email, String password) {
		// TODO Auto-generated method stub
		Query query = getSession()
				.createQuery("from CustomerEntity where email=:email and password=:password and isActive = 1");
		query.setString("email", email);
		query.setString("password", password);
		return (CustomerEntity) query.uniqueResult();
	}

	public CustomerEntity findByEmail(String email) {
		// TODO Auto-generated method stub
		Query query = getSession()
				.createQuery("from CustomerEntity where email=:email");
		query.setString("email", email);
		return (CustomerEntity) query.uniqueResult();
	}
}
