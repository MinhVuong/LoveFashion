package com.example.modelAPI;

import com.example.entity.CustomerAddressEntity;
import com.example.entity.CustomerEntity;

public class ShowDashBoard extends PackageAPI {
	
	private CustomerAddressEntity defaultBilling;
	private CustomerAddressEntity defaultShipping;
	private CustomerAPI customer;	
	public ShowDashBoard() {
		super();	
	}

	public ShowDashBoard(CustomerAddressEntity defaultBilling,
			CustomerAddressEntity defaultShipping, CustomerAPI customer) {
		super();
		this.defaultBilling = defaultBilling;
		this.defaultShipping = defaultShipping;
		this.customer = customer;
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
	
	public CustomerAPI getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerAPI customer) {
		this.customer = customer;
	}

}
