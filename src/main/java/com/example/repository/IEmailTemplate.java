package com.example.repository;

import com.example.entity.EmailTemplate;

public interface IEmailTemplate extends DAO<EmailTemplate>{

	public EmailTemplate getEmailTemplate(String code);
}
