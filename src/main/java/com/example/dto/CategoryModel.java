package com.example.dto;

import java.util.ArrayList;
import java.util.List;


public class CategoryModel {
	private Integer entityId;
	private String name;
	private String nameEn;
	private String urlPath;
	private Integer level;
	
	private List<CategoryModel> subCategories;

	public CategoryModel(){
		subCategories  = new ArrayList<CategoryModel>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public List<CategoryModel> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<CategoryModel> subCategories) {
		this.subCategories = subCategories;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
}
