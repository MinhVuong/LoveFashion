package com.example.repository;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.example.entity.VerificationToken;

@Repository
public class VerificationTokenDAO extends AbstractDAO<VerificationToken>
		implements IVerificationTokenDAO {

	public VerificationToken findByToken(String token) {
		// TODO Auto-generated method stub
		Query query = getSession().createQuery(
				"from VerificationToken where token=:token");
		query.setString("token", token);
		return (VerificationToken) query.uniqueResult();
	}

}
