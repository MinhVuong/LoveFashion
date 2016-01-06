package com.example.modelAPI;



public class ShowDashBoard extends PackageAPI {
	
	private CustomerAddressAPI defaultBilling;
	private CustomerAddressAPI defaultShipping;
	private CustomerAPI customer;	
	public ShowDashBoard() {
		super();	
	}
	public ShowDashBoard(CustomerAddressAPI defaultBilling,
			CustomerAddressAPI defaultShipping, CustomerAPI customer) {
		super();
		this.defaultBilling = defaultBilling;
		this.defaultShipping = defaultShipping;
		this.customer = customer;
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
	public CustomerAPI getCustomer() {
		return customer;
	}
	public void setCustomer(CustomerAPI customer) {
		this.customer = customer;
	}

	
}
