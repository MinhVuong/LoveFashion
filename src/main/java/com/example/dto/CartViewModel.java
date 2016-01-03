package com.example.dto;

import java.util.ArrayList;
import java.util.List;

import com.example.entity.SalesOrderAddress;

public class CartViewModel {

	private  double subtotal;
	private double grandtotal;
	private int item;
	private double priceTax;
	private double priceShipping;
	private String methodShipping;
	private String methodPayement;
	private SalesOrderAddress billing;
	private SalesOrderAddress shipping;
	private List<CartProductModel> list = new ArrayList<CartProductModel>();
	
	public List<CartProductModel> getList() {
		return list;
	}

	public void setList(List<CartProductModel> list) {
		this.list = list;
	}

	public int getItem() {
		return item;
	}

	public void setItem(int item) {
		this.item = item;
	}

	public double getPriceTax() {
		return priceTax;
	}

	public void setPriceTax(double priceTax) {
		this.priceTax = priceTax;
	}

	public double getPriceShipping() {
		return priceShipping;
	}

	public void setPriceShipping(double priceShipping) {
		this.priceShipping = priceShipping;
	}

	public String getMethodShipping() {
		return methodShipping;
	}

	public void setMethodShipping(String methodShipping) {
		this.methodShipping = methodShipping;
	}

	public String getMethodPayement() {
		return methodPayement;
	}

	public void setMethodPayement(String methodPayement) {
		this.methodPayement = methodPayement;
	}

	public SalesOrderAddress getBilling() {
		return billing;
	}

	public void setBilling(SalesOrderAddress billing) {
		this.billing = billing;
	}

	public SalesOrderAddress getShipping() {
		return shipping;
	}

	public void setShipping(SalesOrderAddress shipping) {
		this.shipping = shipping;
	}

	public double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}

	public double getGrandtotal() {
		return grandtotal;
	}

	public void setGrandtotal(double grandtotal) {
		this.grandtotal = grandtotal;
	}
	
	
}
