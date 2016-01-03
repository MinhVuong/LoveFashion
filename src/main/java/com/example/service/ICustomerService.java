package com.example.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.example.dto.AddressAccount;
import com.example.dto.OrderModel;
import com.example.dto.RegisterModel;
import com.example.entity.CustomerAddressEntity;
import com.example.entity.CustomerEntity;
import com.example.entity.SalesOrder;
import com.example.entity.VerificationToken;

public interface ICustomerService {

	public CustomerEntity getCustomer(String email, String password)
			throws NoSuchAlgorithmException;

	public CustomerEntity getCustomerId(Integer id);
	
	public void update(CustomerEntity customer);

	public CustomerEntity register(RegisterModel register)
			throws NoSuchAlgorithmException;

	public CustomerEntity findByEmail(String email);
	
	public void createVerificationTokenForUser(CustomerEntity customer, String type,
			String token);
	
	public VerificationToken generateNewVerificationToken(String existingToken);

	public VerificationToken getVerificationToken(String verificationToken);
	
	public String hashPassword(String password) throws NoSuchAlgorithmException;
	
	public void saveAdress(AddressAccount address, CustomerEntity customer);
	
	public void updateAdress(AddressAccount address, CustomerEntity customer);
	
	public CustomerAddressEntity getCustomerAddress(Integer id);
	
	public void deleteCustomerAddress(Integer id);
	
	public List<CustomerAddressEntity>  findAdditionalAddress(int customerId,
			Integer defaultBilling, Integer defaultShipping);

	public List<OrderModel> myOrders(CustomerEntity customer);
	
	public void cancelOrder(SalesOrder order);
	
	public SalesOrder getOrder(int id);
}
