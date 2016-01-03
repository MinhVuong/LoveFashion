package com.example.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.OrderModel;
import com.example.dto.ReportCustomer;
import com.example.dto.RevenueReport;
import com.example.entity.AdminUser;
import com.example.entity.SalesOrder;
import com.example.repository.IAdminDAO;
import com.example.repository.ISalesOrderDAO;

@Service
public class AdminService implements IAdminService{

	@Autowired
	private IAdminDAO adminRepository;
	
	@Autowired
	private ISalesOrderDAO sales;
	
	@Transactional
	public AdminUser getAdmin(String username, String password) {
		// TODO Auto-generated method stub
		return adminRepository.getAdmin(username, password);
	}
	
	@Transactional
	public List<OrderModel> getAllOrders() {
		// TODO Auto-generated method stub
		List<SalesOrder> orders = sales.getAll();
		List<OrderModel> myOrders = new ArrayList<OrderModel>();
		if (orders != null) {
			OrderModel model = null;
			for (int i = 0; i < orders.size(); i++) {
				model = new OrderModel();
				model.setCreate(orders.get(i).getCreatedAt());
				model.setId(orders.get(i).getEntityId());
				model.setStatus(orders.get(i).getStatus());
				model.setTotal(orders.get(i).getGrandTotal());
				model.setShipingName(orders.get(i).getCustomerFirstname() + " "
						+ orders.get(i).getCustomerLastname());
				myOrders.add(model);
			}
		}
		return myOrders;
	}

	@Transactional
	public void changeStatus(SalesOrder order) {
		// TODO Auto-generated method stub
		sales.update(order);
	}

	@Transactional
	public SalesOrder getSalesOrder(int id) {
		// TODO Auto-generated method stub
		return sales.get(id);
	}

	@Transactional
	public List<ReportCustomer> reportCustomer() {
		// TODO Auto-generated method stub
		return sales.reportCustomer();
	}

	@Transactional
	public List<RevenueReport> reportRevenue(String type) {
		// TODO Auto-generated method stub
		return sales.reportRevenue(type);
	}

}
