package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.service.IProductSevice;

@Controller
public class HomeController {

	@Autowired
	private IProductSevice service;
	

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String home(Model model) {
		//CategoryModel category = this.buildTreeCategory(0);
		//model.addAttribute("categories", category);
		return "bad-user";
	}

	
}
