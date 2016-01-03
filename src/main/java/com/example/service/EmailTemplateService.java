package com.example.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.EmailTemplate;
import com.example.repository.IEmailTemplate;

@Service
public class EmailTemplateService implements IEmailTemplateService{

	@Autowired
	private IEmailTemplate emailRepository;
	
	@Transactional
	public EmailTemplate getEmailTemplate(String code) {
		// TODO Auto-generated method stub
		return emailRepository.getEmailTemplate(code);
	}

}
