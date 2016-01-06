package com.example.modelAPI;

import java.util.List;

import com.example.entity.CustomerAddressEntity;

public class ShowAddress {
	private CustomerAddressAPI defaultBilling;
	private CustomerAddressAPI defaultShipping;
	private List<CustomerAddressAPI> listAddress;
	
	public ShowAddress() {
		super();
	}

	public ShowAddress(CustomerAddressAPI defaultBilling,
			CustomerAddressAPI defaultShipping,
			List<CustomerAddressAPI> listAddress) {
		super();
		this.defaultBilling = defaultBilling;
		this.defaultShipping = defaultShipping;
		this.listAddress = listAddress;
	}

	public CustomerAddressAPI getDefaultBilling() {
		return defaultBilling;
	}

	public void setDefaultBilling(CustomerAddressAPI defaultBilling) {
		this.defaultBilling = defaultBilling;
	}

	public CustomerAddressAPI getDefaultShipping() {
		return defaultShipping;
	}

	public void setDefaultShipping(CustomerAddressAPI defaultShipping) {
		this.defaultShipping = defaultShipping;
	}

	public List<CustomerAddressAPI> getListAddress() {
		return listAddress;
	}

	public void setListAddress(List<CustomerAddressAPI> listAddress) {
		this.listAddress = listAddress;
	}
	
	
	

}
