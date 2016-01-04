package com.example.controllerAPI;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.Book;



@RestController
@RequestMapping(value = "/book")
public class BookController {
	private static ArrayList<Book> books = new ArrayList<Book>();
	
	static{
		books.add(new Book(1, "Book 1"));
		books.add(new Book(2, "Book "));
	}
	@RequestMapping(value = { "/{id} " },
			method = { RequestMethod.GET },
			produces = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<Book>getBook(@PathVariable("id") int id) {
		System.out.println("api book");
		Book book = books.get(id);
		ResponseEntity<Book> entity = new
		ResponseEntity<Book>(book, HttpStatus.OK);
		return entity;
	}
	
}
