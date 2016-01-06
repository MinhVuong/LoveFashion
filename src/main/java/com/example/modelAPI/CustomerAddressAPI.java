package com.example.modelAPI;



public class CustomerAddressAPI {
	private Integer entityId;
	private String street;
	private String region;
	private String country;
	private String firstname;
	private String lastname;
	private String phone;
	public CustomerAddressAPI() {
		super();
	}
	public CustomerAddressAPI(Integer entityId, String street, String region, String country,
			String firstname, String lastname, String phone) {
		super();
		this.entityId = entityId;
		this.street = street;
		this.region = region;
		this.country = country;
		this.firstname = firstname;
		this.lastname = lastname;
		this.phone = phone;
	}
	public Integer getEntityId() {
		return entityId;
	}
	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	

}
