package com.example.repository;

import java.util.List;

import com.example.entity.CatalogCategoryEntity;


public interface ICategoryDAO extends DAO<CatalogCategoryEntity>{
	
	public List<CatalogCategoryEntity> getAllCategoryByOrder();
	
	public List<CatalogCategoryEntity> getCategoryByParent(Integer parent);
	
	public CatalogCategoryEntity getRootCategory();
	
	public CatalogCategoryEntity findCategoryByUrl(String urlPath);
}
