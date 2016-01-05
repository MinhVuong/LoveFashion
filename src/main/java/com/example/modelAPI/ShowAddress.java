package com.example.modelAPI;

import java.util.List;

import com.example.entity.CustomerAddressEntity;

public class ShowAddress {
	private CustomerAddressEntity defaultBilling;
	private CustomerAddressEntity defaultShipping;
	private List<CustomerAddressEntity> listAddress;
	
	public ShowAddress() {
		super();
	}
	
	public ShowAddress(CustomerAddressEntity defaultBilling,
			CustomerAddressEntity defaultShipping,
			List<CustomerAddressEntity> listAddress) {
		super();
		this.defaultBilling = defaultBilling;
		this.defaultShipping = defaultShipping;
		this.listAddress = listAddress;
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

	public List<CustomerAddressEntity> getListAddress() {
		return listAddress;
	}

	public void setListAddress(List<CustomerAddressEntity> listAddress) {
		this.listAddress = listAddress;
	}
	
	
	
	

}
