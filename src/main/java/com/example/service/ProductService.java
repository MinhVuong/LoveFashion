package com.example.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.CartProductModel;
import com.example.dto.CartViewModel;
import com.example.dto.ListProductModel;
import com.example.dto.ProductModel;
import com.example.entity.CatalogCategoryEntity;
import com.example.entity.CatalogProductEntity;
import com.example.entity.CatalogProductEntityGallery;
import com.example.entity.CustomerEntity;
import com.example.entity.SalesOrder;
import com.example.entity.SalesOrderAddress;
import com.example.entity.SalesOrderItem;
import com.example.entity.SalesOrderPayment;
import com.example.repository.ICategoryDAO;
import com.example.repository.IProductDAO;
import com.example.repository.ISalesOrderAddressDAO;
import com.example.repository.ISalesOrderDAO;
import com.example.repository.ISalesOrderItemDAO;
import com.example.repository.ISalesOrderPaymentDAO;

@Service
public class ProductService implements IProductSevice {

	@Autowired
	private ICategoryDAO categoryRepository;

	@Autowired
	private IProductDAO productRepository;
	@Autowired
	private ISalesOrderDAO saleOrder;
	@Autowired
	private ISalesOrderAddressDAO saleAdrress;
	@Autowired
	private ISalesOrderPaymentDAO salePayment;
	@Autowired
	private ISalesOrderItemDAO saleItem;
	@Transactional
	public List<CatalogCategoryEntity> getAllCategoryByOrder() {
		// TODO Auto-generated method stub
		return categoryRepository.getAllCategoryByOrder();
	}

	@Transactional
	public CatalogCategoryEntity getRootCategory() {
		// TODO Auto-generated method stub
		return categoryRepository.getRootCategory();
	}

	@Transactional
	public List<CatalogCategoryEntity> getCategoryByParent(int parent) {
		// TODO Auto-generated method stub
		return categoryRepository.getCategoryByParent(parent);
	}

	@Transactional
	public List<ListProductModel> getListProductByCategoy(String urlPath) {
		// TODO Auto-generated method stub
		Set<CatalogProductEntity> products = categoryRepository
				.findCategoryByUrl(urlPath).getCatalogProductEntitys();
		List<ListProductModel> list = new ArrayList<ListProductModel>();
		ListProductModel model;
		Set<CatalogProductEntityGallery> images;
		for (CatalogProductEntity item : products) {
			if (item.getStatus() == 1) {
				model = new ListProductModel();
				model.setName(item.getName());
				model.setPrice(item.getPrice());
				model.setUrlPath(item.getUrlPath());
				model.setSale(item.getSale() == 1 ? true : false);
				model.setNews(item.getIsNew() == 1 ? true : false);
				images = item.getCatalogProductEntityGalleries();
				for (CatalogProductEntityGallery image : images) {
					model.getListImage().add(image.getValue());
				}
				if (model.getListImage().size() < 2
						&& model.getListImage().size() > 0) {
					model.getListImage().add(model.getListImage().get(0));
				}
				list.add(model);
			}
		}
		return list;
	}

	@Transactional
	public ProductModel getProduct(Integer id) {
		// TODO Auto-generated method stub
		CatalogProductEntity catProduct = productRepository.get(id);
		Set<CatalogProductEntityGallery> galleries;
		Set<CatalogProductEntity> relatives;
		ProductModel product = new ProductModel();
		product.setId(catProduct.getEntityId());
		product.setActive(catProduct.getActive());
		product.setClimate(catProduct.getClimate());
		product.setDescription(catProduct.getDescription());
		product.setImage(catProduct.getImage());
		product.setMaterial(catProduct.getMaterial());
		product.setName(catProduct.getName());
		product.setPattern(catProduct.getPattern());
		product.setPrice(catProduct.getPrice());
		product.setSale(catProduct.getSale());
		product.setSpecialPrice(catProduct.getSpecialPrice());
		product.setSku(catProduct.getSku());
		product.setSleeve(catProduct.getSleeve());
		product.setUrlPath(catProduct.getUrlPath());
		product.setCollar(catProduct.getCollar());
		product.setColor(productRepository.getListColor(catProduct.getSku(),
				"Simple Product"));
		product.setSize(productRepository.getListSize(catProduct.getSku(),
				"Simple Product"));
		product.setStatus(productRepository.exitsInventory(catProduct
				.getEntityId()));
		galleries = catProduct.getCatalogProductEntityGalleries();
		for (CatalogProductEntityGallery gallery : galleries) {
			product.getListImages().add(gallery.getValue());
		}
		ProductModel entity;
		relatives = catProduct.getCatalogProductEntitiesForChildId();
		for (CatalogProductEntity relative : relatives) {
			entity = new ProductModel();
			entity.setId(relative.getEntityId());
			entity.setName(relative.getName());
			entity.setPrice(relative.getPrice());
			entity.setSale(relative.getSale());
			entity.setUrlPath(relative.getUrlPath());
			entity.setImage(relative.getImage());
			product.getRelatives().add(entity);
		}
		int num = product.getRelatives().size() / 3;
		if (product.getRelatives().size() % 3 != 0
				&& product.getRelatives().size() > 0) {
			num = num + 1;
		}
		product.setNumPageRelative(num);
		return product;
	}

	@Transactional
	public CatalogProductEntity getSimpleProduct(String sku) {
		// TODO Auto-generated method stub
		return productRepository.getSimpleProduct(sku);
	}

	@Transactional
	public CatalogProductEntity getSimpleProduct(String sku, String color,
			String size) {
		// TODO Auto-generated method stub
		return productRepository.getSimpleProduct(sku, color, size);
	}

	@Transactional
	public Map<Integer, String> getColorSimpleProduct(String sku) {
		// TODO Auto-generated method stub
		return productRepository.getListColor(sku, "Simple Product");
	}

	@Transactional
	public Map<Integer, String> getSizeSimpleProduct(String sku) {
		// TODO Auto-generated method stub
		return productRepository.getListSize(sku, "Simple Product");
	}

	@Transactional
	public List<ListProductModel> getListProductByCategoy(Integer id,  String color, String prices, String size) {
		// TODO Auto-generated method stub
		List<CatalogProductEntity> products = productRepository.getListProductByCategory(id, color, prices, size);
		List<ListProductModel> list = new ArrayList<ListProductModel>();
		ListProductModel model;
		Set<CatalogProductEntityGallery> images;
		for (CatalogProductEntity item : products) {
			if (item.getStatus() == 1) {
				model = new ListProductModel();
				model.setId(item.getEntityId());
				model.setSku(item.getSku());
				model.setName(item.getName());
				model.setPrice(item.getPrice());
				model.setUrlPath(item.getUrlPath());
				model.setSpecialPrice(item.getSpecialPrice());
				model.setSale(item.getSale() == 1 ? true : false);
				model.setNews(item.getIsNew() == 1 ? true : false);
				images = item.getCatalogProductEntityGalleries();
				for (CatalogProductEntityGallery image : images) {
					model.getListImage().add(image.getValue());
				}
				if (model.getListImage().size() < 2
						&& model.getListImage().size() > 0) {
					model.getListImage().add(model.getListImage().get(0));
				}
				list.add(model);
			}
		}
		return list;
	}

	@Transactional
	public long count() {
		// TODO Auto-generated method stub
		return productRepository.count();
	}

	@Transactional
	public List<ListProductModel> search(String key, String color, String size, String price) {
		// TODO Auto-generated method stub
		List<CatalogProductEntity> products = productRepository.search(key, color, size, price);
		List<ListProductModel> list = new ArrayList<ListProductModel>();
		ListProductModel model;
		Set<CatalogProductEntityGallery> images;
		for (CatalogProductEntity item : products) {
			if (item.getStatus() == 1) {
				model = new ListProductModel();
				model.setId(item.getEntityId());
				model.setName(item.getName());
				model.setSku(item.getSku());
				model.setPrice(item.getPrice());
				model.setUrlPath(item.getUrlPath());
				model.setSpecialPrice(item.getSpecialPrice());
				model.setSale(item.getSale() == 1 ? true : false);
				model.setNews(item.getIsNew() == 1 ? true : false);
				images = item.getCatalogProductEntityGalleries();
				for (CatalogProductEntityGallery image : images) {
					model.getListImage().add(image.getValue());
				}
				if (model.getListImage().size() < 2
						&& model.getListImage().size() > 0) {
					model.getListImage().add(model.getListImage().get(0));
				}
				list.add(model);
			}
		}
		return list;
	}

	@Transactional
	public double clalTax(List<CartProductModel> list) {
		// TODO Auto-generated method stub
		double sum = 0f;
		double tax = 0f;
		for(int i = 0; i < list.size(); i++){
			tax = productRepository.getValueTax(list.get(0).getTax());
			sum = sum + list.get(i).getPrice() * (tax / 100);
		}
		return sum;
	}

	@Transactional
	public void complate(CartViewModel cart, CustomerEntity customer) {
		// TODO Auto-generated method stub
		SalesOrder order = new SalesOrder();
		order.setShippingMethod(cart.getMethodShipping());
		order.setCustomerFirstname(customer.getFirstname());
		order.setCustomerLastname(customer.getLastname());
		order.setStatus("pending");
		order.setSubtotal(cart.getSubtotal());
		order.setGrandTotal(cart.getSubtotal());
		order.setTaxAmount(cart.getPriceTax());
		order.setCreatedAt(new Date());
		order.setUpdatedAt(new Date());
		order.setCustomerEntity(customer);
		order.setTotalItemCount((short) cart.getItem());
		int id = saleOrder.create(order);
		SalesOrderItem item = null;
		for(int i = 0; i < cart.getList().size(); i++){
			item = new SalesOrderItem();
			item.setSalesOrder(order);
			item.setCreatedAt(new Date());
			item.setUpdatedAt(new Date());
			item.setName(cart.getList().get(i).getName());
			item.setProductId(cart.getList().get(i).getId());
			item.setOriginalPrice(cart.getList().get(i).getPrice());
			item.setQtyOrdered(cart.getList().get(i).getQty());
			item.setSku(cart.getList().get(i).getSku());
			item.setProductOptions(cart.getList().get(i).getColor() + ","
					+ cart.getList().get(i).getSize());
			item.setRowTotal(item.getQtyOrdered() * item.getOriginalPrice());
			saleItem.create(item);
		}
		SalesOrderAddress address = cart.getBilling();
		address.setSalesOrder(new SalesOrder(id));
		int biiling = saleAdrress.create(address);
		address = cart.getShipping();
		address.setSalesOrder(new SalesOrder(id));
		int ship = saleAdrress.create(address);
		SalesOrderPayment payment = new SalesOrderPayment();
		payment.setAmountOrdered(cart.getGrandtotal());
		payment.setMethod(cart.getMethodPayement());
		payment.setSalesOrder(new SalesOrder(id));
		salePayment.create(payment);
		order.setBillingAddressId(biiling);
		order.setShippingAddressId(ship);
		saleOrder.update(order);
	}
}
