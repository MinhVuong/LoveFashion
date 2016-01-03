package com.example.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import com.example.dto.ReportCustomer;
import com.example.dto.RevenueReport;
import com.example.entity.SalesOrder;

@Repository
public class SalesOrderDAO extends AbstractDAO<SalesOrder> implements
		ISalesOrderDAO {

	@SuppressWarnings("unchecked")
	public List<SalesOrder> getOrderOfCustomer(int id) {
		// TODO Auto-generated method stub
		Query query = getSession().createQuery(
				"from SalesOrder where customerEntity.entityId=:id");
		query.setInteger("id", id);
		return query.list();
	}

	public List<ReportCustomer> reportCustomer() {
		String sql = "SELECT s.customer_firstname, s.customer_lastname, sum(s.grand_total), count(*) "
				+ "FROM lovefashion.sales_order s group by customer_id order by sum(s.grand_total) desc";
		SQLQuery query = getSession().createSQLQuery(sql);
		List list = query.list();
		List<ReportCustomer> reports = new ArrayList<ReportCustomer>();
		ReportCustomer report;
		int order = 1;
		for (Iterator it = list.iterator(); it.hasNext();) {
			report = new ReportCustomer();
			Object[] item = (Object[]) it.next();
			report.setOrder(order);
			report.setFirstname((String) item[0]);
			report.setLastname((String) item[1]);
			report.setPaid(((BigDecimal) item[2]).doubleValue());
			report.setNumber(((BigInteger) item[3]).intValue());
			reports.add(report);
			order++;
		}
		return reports;
	}

	public List<RevenueReport> reportRevenue(String type) {
		String sql = "";
		if ("week".equals(type)) {
			sql = "SELECT CONCAT(WEEK(s.created_at),'-',YEAR(s.created_at)), sum(s.grand_total), count(*) FROM sales_order s "
					+ "group by WEEK(s.created_at), YEAR(s.created_at) order by  WEEK(s.created_at) desc";
		} else if ("quarter".equals(type)) {
			sql = "SELECT CONCAT(quarter(s.created_at),'-',YEAR(s.created_at)), sum(s.grand_total), count(*) FROM sales_order s "
					+ "group by quarter(s.created_at), YEAR(s.created_at) order by  quarter(s.created_at) desc";
		} else if ("month".equals(type)) {
			sql = "SELECT CONCAT(MONTH(s.created_at),'-',YEAR(s.created_at)), sum(s.grand_total), count(*) FROM sales_order s "
					+ "group by MONTH(s.created_at), YEAR(s.created_at) order by  MONTH(s.created_at) desc";
		} else {
			sql = "SELECT CONVERT(YEAR(s.created_at), char(10)), sum(s.grand_total), count(*) FROM sales_order s "
					+ "group by YEAR(s.created_at) order by  YEAR(s.created_at) desc";
		}
		SQLQuery query = getSession().createSQLQuery(sql);
		@SuppressWarnings("rawtypes")
		List list = query.list();
		List<RevenueReport> reports = new ArrayList<RevenueReport>();
		RevenueReport report;
		for (@SuppressWarnings("rawtypes")
		Iterator it = list.iterator(); it.hasNext();) {
			report = new RevenueReport();
			Object[] item = (Object[]) it.next();
			report.setId((String)item[0]);
			report.setPaid(((BigDecimal) item[1]).doubleValue());
			report.setNumber(((BigInteger) item[2]).intValue());
			reports.add(report);
		}
		return reports;
	}

}
