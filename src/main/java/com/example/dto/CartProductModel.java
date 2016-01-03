package com.example.dto;

import java.util.Map;

public class CartProductModel {
	private int id;
	private String name;
	private String color;
	private String sku;
	private String size;
	private String image;
	private int qty;
	private double price;
	private int index;
	private int item;
	private double total;
	private int tax;
	private Map<Integer, String> listColors;
	private Map<Integer, String> listSizes;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Map<Integer, String> getListColors() {
		return listColors;
	}

	public void setListColors(Map<Integer, String> listColors) {
		this.listColors = listColors;
	}

	public Map<Integer, String> getListSizes() {
		return listSizes;
	}

	public void setListSizes(Map<Integer, String> listSizes) {
		this.listSizes = listSizes;
	}

	public int getItem() {
		return item;
	}

	public void setItem(int item) {
		this.item = item;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public int getTax() {
		return tax;
	}

	public void setTax(int tax) {
		this.tax = tax;
	}
	
	
}