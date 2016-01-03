package com.example.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductModel {

	private int id;
	private String name;
	private String sku;
	private double price;
	private String material;
	private String climate;
	private String pattern;
	private String collar;
	private String sleeve;
	private String active;
	private String description;
	private String urlPath;
	private boolean status;
	private Short sale;
	private Map<Integer, String> color = new HashMap<Integer, String>();
	private Map<Integer, String> size = new HashMap<Integer, String>();
	private List<ProductModel> relatives = new ArrayList<ProductModel>();
	private String image;
	private List<String> listImages = new ArrayList<String>();
	private double specialPrice;
	private int numPageRelative;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getClimate() {
		return climate;
	}

	public void setClimate(String climate) {
		this.climate = climate;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getCollar() {
		return collar;
	}

	public void setCollar(String collar) {
		this.collar = collar;
	}

	public String getSleeve() {
		return sleeve;
	}

	public void setSleeve(String sleeve) {
		this.sleeve = sleeve;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public Short getSale() {
		return sale;
	}

	public void setSale(Short sale) {
		this.sale = sale;
	}

	public Map<Integer, String> getColor() {
		return color;
	}

	public void setColor(Map<Integer, String> color) {
		this.color = color;
	}

	public Map<Integer, String> getSize() {
		return size;
	}

	public void setSize(Map<Integer, String> size) {
		this.size = size;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<String> getListImages() {
		return listImages;
	}

	public void setListImages(List<String> listImages) {
		this.listImages = listImages;
	}

	public double getSpecialPrice() {
		return specialPrice;
	}

	public void setSpecialPrice(double specialPrice) {
		this.specialPrice = specialPrice;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public List<ProductModel> getRelatives() {
		return relatives;
	}

	public void setRelatives(List<ProductModel> relatives) {
		this.relatives = relatives;
	}

	public int getNumPageRelative() {
		return numPageRelative;
	}

	public void setNumPageRelative(int numPageRelative) {
		this.numPageRelative = numPageRelative;
	}
	
	
}
