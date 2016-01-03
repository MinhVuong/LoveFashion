package com.example.dto;

import java.util.Date;

public class OrderModel {
	private int id;
	private Date create;
	private String shipingName;
	private double total;
	private String status;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getCreate() {
		return create;
	}
	public void setCreate(Date create) {
		this.create = create;
	}
	public String getShipingName() {
		return shipingName;
	}
	public void setShipingName(String shipingName) {
		this.shipingName = shipingName;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
