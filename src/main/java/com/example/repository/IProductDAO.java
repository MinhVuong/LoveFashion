package com.example.repository;

import java.util.List;
import java.util.Map;

import com.example.entity.CatalogProductEntity;

public interface IProductDAO extends DAO<CatalogProductEntity>{

	public CatalogProductEntity getProduct(String urlKey, String type);
	
	public Map<Integer, String> getListColor(String sku, String type);
	
	public Map<Integer, String> getListSize(String sku, String type);
	
	public boolean exitsInventory(Integer id);
	
	public CatalogProductEntity getSimpleProduct(String sku, String color, String size);
	
	public CatalogProductEntity getSimpleProduct(String sku);
	
	public List<CatalogProductEntity> getListProductByCategory(Integer id,  String color, String prices, String size);
	
	public List<CatalogProductEntity> search(String key, String color, String size, String price);
	
	public double getValueTax(Integer tax);
	
}
