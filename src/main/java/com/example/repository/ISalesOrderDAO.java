package com.example.repository;

import java.util.List;

import com.example.dto.ReportCustomer;
import com.example.dto.RevenueReport;
import com.example.entity.SalesOrder;

public interface ISalesOrderDAO extends DAO<SalesOrder>{

	public List<SalesOrder> getOrderOfCustomer(int id);
	
	public List<ReportCustomer> reportCustomer();
	
	public List<RevenueReport> reportRevenue(String type);
}
