package com.example.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
    public String handleSQLException(HttpServletRequest request, 
    		Exception ex, Model model){
		model.addAttribute("error", ex.getMessage());
        return "exception";
    }
}
