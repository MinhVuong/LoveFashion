package com.example.repository;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.example.entity.EmailTemplate;

@Repository
public class EmailTemplateDAO extends AbstractDAO<EmailTemplate> implements
		IEmailTemplate {

	public EmailTemplate getEmailTemplate(String code) {
		// TODO Auto-generated method stub
		Query query = getSession().createQuery("from EmailTemplate where templateCode=:code");
		query.setString("code", code);
		return (EmailTemplate) query.uniqueResult();
	}

}
