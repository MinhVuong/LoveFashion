package com.example.listener;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.entity.CustomerEntity;
import com.example.entity.EmailTemplate;
import com.example.event.OnRegistrationCompleteEvent;
import com.example.service.ICustomerService;
import com.example.service.IEmailTemplateService;

@Component
public class RegistrationListener implements
		ApplicationListener<OnRegistrationCompleteEvent> {

	@Autowired
	private ICustomerService service;

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private IEmailTemplateService emailService;

	public void onApplicationEvent(OnRegistrationCompleteEvent event) {
		confirmRegistration(event);
	}

	private void confirmRegistration(OnRegistrationCompleteEvent event) {
		CustomerEntity customer = event.getCustomer();
		EmailTemplate template = emailService.getEmailTemplate("register");
		String token = UUID.randomUUID().toString();
		service.createVerificationTokenForUser(customer, token, "verity-email");
		String recipientAddress = customer.getEmail();
		String confirmationUrl = event.getAppUrl() + "/customer/account/regitrationConfirm?token=" + token;
		String resendEmail = event.getAppUrl() + "/customer/account/resendEmail?token=" + token;
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(recipientAddress);
		email.setSubject(template.getTemplateSubject());
		String text = String.format(template.getTemplateText(), customer.getLastname() + " " + customer.getFirstname(), 
				confirmationUrl, resendEmail);
		email.setText(text);
		mailSender.send(email);
	}
}
