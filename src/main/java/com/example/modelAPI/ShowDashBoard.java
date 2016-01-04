package com.example.modelAPI;

import com.example.entity.CustomerAddressEntity;

public class ShowDashBoard extends PackageAPI {
	private CustomerAddressEntity defaultBilling;
	private CustomerAddressEntity defaultShipping;
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
	
	
}
