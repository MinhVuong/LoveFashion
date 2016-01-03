package com.example.repository;

import com.example.entity.VerificationToken;

public interface IVerificationTokenDAO extends DAO<VerificationToken> {
	
	VerificationToken findByToken(String token);
}
