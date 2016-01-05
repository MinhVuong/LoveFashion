package com.example.modelAPI;

import com.example.entity.CustomerAddressEntity;
import com.example.entity.CustomerEntity;

public class ShowDashBoard extends PackageAPI {
	private CustomerAddressEntity defaultBilling;
	private CustomerAddressEntity defaultShipping;
	private CustomerEntity customer;
	
	

	
	public ShowDashBoard() {
		super();	
	}
	
	public ShowDashBoard(CustomerAddressEntity defaultBilling,
			CustomerAddressEntity defaultShipping) {
		super();
		this.defaultBilling = defaultBilling;
		this.defaultShipping = defaultShipping;
	}

	public CustomerAddressEntity getDefaultBilling() {
		return defaultBilling;
	}

	public void setDefaultBilling(CustomerAddressEntity defaultBilling) {
		this.defaultBilling = defaultBilling;
	}

	public CustomerAddressEntity getDefaultShipping() {
		return defaultShipping;
	}

	public void setDefaultShipping(CustomerAddressEntity defaultShipping) {
		this.defaultShipping = defaultShipping;
	}
	
	public CustomerEntity getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerEntity customer) {
		this.customer = customer;
	}

}
