package com.example.modelAPI;

import java.util.List;

import com.example.dto.OrderModel;

public class ListOrderAPI {
	private List<OrderModel> listOder;

	public ListOrderAPI() {
		super();
	}
	
	public ListOrderAPI(List<OrderModel> listOder) {
		super();
		this.listOder = listOder;
	}

	public List<OrderModel> getListOder() {
		return listOder;
	}

	public void setListOder(List<OrderModel> listOder) {
		this.listOder = listOder;
	}
	
	
}
