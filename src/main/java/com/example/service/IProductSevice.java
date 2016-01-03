package com.example.service;

import java.util.List;
import java.util.Map;

import com.example.dto.CartProductModel;
import com.example.dto.CartViewModel;
import com.example.dto.ListProductModel;
import com.example.dto.ProductModel;
import com.example.entity.CatalogCategoryEntity;
import com.example.entity.CatalogProductEntity;
import com.example.entity.CustomerEntity;

public interface IProductSevice {

	public List<CatalogCategoryEntity> getAllCategoryByOrder();
	
	public CatalogCategoryEntity getRootCategory();
	
	public List<CatalogCategoryEntity> getCategoryByParent(int parent);
	
	public List<ListProductModel> getListProductByCategoy(String urlPath);
	
	public List<ListProductModel> getListProductByCategoy(Integer id,  String color, String prices, String size);
	
	public List<ListProductModel> search(String key, String color, String size, String price);
	
	public ProductModel getProduct(Integer id);
	
	public CatalogProductEntity getSimpleProduct(String sku);
	
	public CatalogProductEntity getSimpleProduct(String sku, String color, String size);
	
	public Map<Integer, String> getColorSimpleProduct(String sku);
	
	public Map<Integer, String> getSizeSimpleProduct(String sku);
	
	public long count();
	
	public double clalTax(List<CartProductModel> list);
	
	public void complate(CartViewModel cart, CustomerEntity customer);
}
