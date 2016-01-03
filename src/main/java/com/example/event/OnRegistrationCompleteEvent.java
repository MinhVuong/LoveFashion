package com.example.event;

import java.util.Locale;

import org.springframework.context.ApplicationEvent;

import com.example.entity.CustomerEntity;

public class OnRegistrationCompleteEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String appUrl;
	private final Locale locale;
	private final CustomerEntity customer;

	public OnRegistrationCompleteEvent(Object source, CustomerEntity customer, Locale locale,
			String appUrl) {
		super(source);
		this.appUrl = appUrl;
		this.locale = locale;
		this.customer = customer;
	}

	public String getAppUrl() {
		return appUrl;
	}

	public Locale getLocale() {
		return locale;
	}

	public CustomerEntity getCustomer() {
		return customer;
	}

}
