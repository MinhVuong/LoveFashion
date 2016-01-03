package com.example.repository;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.example.entity.CustomerAddressEntity;

@Repository
public class CustomerAddressDAO extends AbstractDAO<CustomerAddressEntity>
		implements ICustomerAddressDAO {

	@SuppressWarnings("unchecked")
	public List<CustomerAddressEntity> findAdditionalAddress(int customerId,
			Integer defaultBilling, Integer defaultShipping) {
		// TODO Auto-generated method stub
		StringBuilder queryStr = new StringBuilder("from CustomerAddressEntity where customerEntity.entityId = :customerId");
		if(defaultBilling != null){
			queryStr.append(" and entityId != :defaultBilling");
		}
		if (defaultShipping != null){
			queryStr.append(" and entityId != :defaultShipping");
		}
		Query query = getSession().createQuery(queryStr.toString());
		query.setInteger("customerId", customerId);
		if(defaultBilling != null)
			query.setInteger("defaultBilling", defaultBilling);
		if (defaultShipping != null)
			query.setInteger("defaultShipping", defaultShipping);
		return query.list();
	}

}
