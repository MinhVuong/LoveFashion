package com.example.repository;

import com.example.entity.CustomerEntity;

public interface ICustomerDAO extends DAO<CustomerEntity> {
	public CustomerEntity getCustomer(String email, String password);
	
	public CustomerEntity findByEmail(String email);
}
