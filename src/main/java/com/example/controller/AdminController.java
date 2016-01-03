package com.example.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.WebRequest;

import com.example.dto.CartViewModel;
import com.example.entity.AdminUser;
import com.example.entity.CustomerEntity;
import com.example.entity.SalesOrder;
import com.example.service.IAdminService;
import com.example.service.ICustomerService;

@Controller
@SessionAttributes("admin")
public class AdminController {

	@Autowired
	private IAdminService adminService;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private ICustomerService customerService;
	
	@RequestMapping(value = "/admin/index", method = RequestMethod.GET)
	public String index(){
		return "admin/index";
	}
	
	@RequestMapping(value = "/admin/login", method = RequestMethod.GET)
	public String showLogin(HttpSession session) {
		AdminUser admin = (AdminUser) session.getAttribute("admin");
		if(admin != null)
			return "redirect:/admin/index";
		return "admin/login";
	}
	
	@RequestMapping(value = "/admin/login", method = RequestMethod.POST)
	public String login(@RequestParam("username") String username,
			@RequestParam("password") String password, Model model) {
		if (username == null || "".equals(username)) {
			model.addAttribute("error","test");
			return "admin/login";
		} else if (password == null || "".equals(password)) {
			model.addAttribute("error","test");
			return "admin/login";
		} else {
			AdminUser admin = adminService.getAdmin(username, password);
			if(admin != null){
				model.addAttribute("admin", admin);
				return "redirect:/admin/index";
			} else {
				model.addAttribute("error","Invalid username or password.");
				return "admin/login";
			}
		}
	}
	
	@RequestMapping(value = "/admin/logout", method = RequestMethod.GET)
	public String logout(WebRequest request, SessionStatus status) {
		status.setComplete();
		request.removeAttribute("admin", WebRequest.SCOPE_SESSION);
		return "redirect:/admin/login";
	}
	
	@RequestMapping(value = "/admin/manager/orders", method = RequestMethod.GET)
	public String managerOrder(Model model, HttpSession session){
		AdminUser admin = (AdminUser) session.getAttribute("admin");
		if(admin != null){
			model.addAttribute("list_orders", adminService.getAllOrders());
			return "admin/manager-order";
		}
		return "redirect:/admin/login";
		
	}
	
	@RequestMapping(value = "/admin/manager/orders/edit/{id}", method = RequestMethod.POST)
	public String changeStatus(@RequestParam("status") String status,
			@PathVariable("id") int id, HttpSession session){
		AdminUser admin = (AdminUser) session.getAttribute("admin");
		if(admin != null){
			SalesOrder order = adminService.getSalesOrder(id);
			order.setStatus(status);
			adminService.changeStatus(order);
			return "redirect:/admin/manager/orders";
		}
		return "redirect:/admin/login";
	}
	
	@RequestMapping(value = "/admin/report/customer", method = RequestMethod.GET)
	public String reportCustomer(Model model, HttpSession session){
		AdminUser admin = (AdminUser) session.getAttribute("admin");
		if(admin != null){
			model.addAttribute("reportCustomer", adminService.reportCustomer());
			return "admin/report-customer";
		}
		return "redirect:/admin/login";
	}
	
	@RequestMapping(value = "/admin/report/revenue", method = RequestMethod.GET)
	public String reportRevenue(Model model, HttpSession session,
			@RequestParam(value = "type", defaultValue="week", required = false) String type){
		AdminUser admin = (AdminUser) session.getAttribute("admin");
		if(admin != null){
			model.addAttribute("report", adminService.reportRevenue(type));
			model.addAttribute("type", type);
			return "admin/report-revenue";
		}
		return "redirect:/admin/login";
	}
	
}
