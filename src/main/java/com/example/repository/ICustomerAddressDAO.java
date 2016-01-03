package com.example.repository;

import java.util.List;

import com.example.entity.CustomerAddressEntity;

public interface ICustomerAddressDAO extends DAO<CustomerAddressEntity> {
	
	public List<CustomerAddressEntity> findAdditionalAddress(int customerId,
			Integer defaultBilling, Integer defaultShipping);

}
