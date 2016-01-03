package com.example.service;

import java.util.List;

import com.example.dto.OrderModel;
import com.example.dto.ReportCustomer;
import com.example.dto.RevenueReport;
import com.example.entity.AdminUser;
import com.example.entity.SalesOrder;

public interface IAdminService {

	public AdminUser getAdmin(String username, String password);
	
	public List<OrderModel> getAllOrders();
	
	public void changeStatus(SalesOrder order);
	
	public SalesOrder getSalesOrder(int id);
	
	public List<ReportCustomer> reportCustomer();
	
	public List<RevenueReport> reportRevenue(String type);
}
