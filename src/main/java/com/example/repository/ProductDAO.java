package com.example.repository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import com.example.entity.CatalogProductEntity;
import com.example.entity.CataloginventoryStockStatus;
import com.example.entity.TaxCalculationRate;

@Repository
public class ProductDAO extends AbstractDAO<CatalogProductEntity> implements
		IProductDAO {

	public CatalogProductEntity getProduct(String urlKey, String type) {
		// TODO Auto-generated method stub
		Query query = getSession().createQuery(
				"from CatalogProductEntity where urlPath=:urlPath "
				+ "and type_id=:type");
		query.setString("urlPath", urlKey);
		query.setString("type", type);
		return (CatalogProductEntity) query.uniqueResult();
	}

	@SuppressWarnings("rawtypes")
	public Map<Integer, String> getListColor(String sku, String type) {
		// TODO Auto-generated method stub
		SQLQuery query = getSession().createSQLQuery("select distinct color, color_value from catalog_product_entity where sku like :sku"
				+ " and type_id =:type");
		query.setString("sku", sku + "%");
		query.setString("type", type);
		List results = query.list();
		Map<Integer, String> colors = new HashMap<Integer, String>();
		for (Iterator it = results.iterator(); it.hasNext(); ) {
		    Object[] item = (Object[]) it.next();
		    colors.put((Integer) item[0], (String) item[1]);
		}
		return colors;
	}

	@SuppressWarnings("rawtypes")
	public Map<Integer, String> getListSize(String sku, String type) {
		// TODO Auto-generated method stub
		SQLQuery query = getSession().createSQLQuery("select distinct size, size_value from catalog_product_entity where sku like :sku"
				+ " and type_id =:type");
		query.setString("sku", sku + "%");
		query.setString("type", type);
		List results = query.list();
		Map<Integer, String> sizes = new HashMap<Integer, String>();
		for (Iterator it = results.iterator(); it.hasNext(); ) {
		    Object[] item = (Object[]) it.next();
		    sizes.put((Integer) item[0], (String) item[1]);
		}
		return sizes;
	}

	public boolean exitsInventory(Integer id) {
		// TODO Auto-generated method stub
		Query query = getSession().createQuery("from CataloginventoryStockStatus where catalogProductEntity.entityId =:id");
		query.setInteger("id", id);
		CataloginventoryStockStatus status = (CataloginventoryStockStatus)	query.uniqueResult();
		if(status == null){
			return false;
		}
		return true;
	}

	public CatalogProductEntity getSimpleProduct(String sku, String color, String size) {
		// TODO Auto-generated method stub
		Query query = getSession().createQuery("from CatalogProductEntity where sku like:sku"
				+ " and typeId=:type and colorValue=:color and sizeValue=:size");
		query.setString("sku", sku + "%");
		query.setString("type", "Simple Product");
		query.setString("color", color);
		query.setString("size", size);
		return (CatalogProductEntity) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public CatalogProductEntity getSimpleProduct(String sku) {
		// TODO Auto-generated method stub
		Query query = getSession().createQuery("from CatalogProductEntity where sku like:sku"
				+ " and typeId=:type order by sku");
		query.setString("sku", sku + "%");
		query.setString("type", "Simple Product");
		List<CatalogProductEntity> list = (List<CatalogProductEntity>) query.list();
		if(list.size() == 0)
			return null;
		return list.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<CatalogProductEntity> getListProductByCategory(Integer id,  String color, String prices, String size) {
		// TODO Auto-generated method stub
		StringBuilder queryStr = new StringBuilder("select p1.* from catalog_product_entity p1, catalog_category_product cp where"
				+ " cp.product_id = p1.entity_id and cp.category_id=:id");
		StringBuilder conditions = new StringBuilder(" and p1.url_key in (select distinct p2.url_key from catalog_product_entity p2 where ");
		String[] splits;
		StringBuilder str = null;
		int lenght = conditions.length();
		boolean isFlat = false;
		if(color != null || "".equals(color)){
			splits = color.split(",");
			str = new StringBuilder(" (");
			for(int i = 0; i < splits.length; i++){
				str.append("p2.color_value = " + "'" + splits[i] +  "'");
				if(i != (splits.length - 1))
					str.append(" or ");
			}
			isFlat = true;
			str.append(")");
			conditions.append(str);
		}
		if(size != null || "".equals(size)){
			splits = size.split(",");
			if(lenght != conditions.length())
				str = new StringBuilder(" and (");
			else
				str = new StringBuilder(" (");
			for(int i = 0; i < splits.length; i++){
				str.append("p2.size_value = " + "'" + splits[i] +  "'");
				if(i != (splits.length - 1))
					str.append(" or ");
			}
			isFlat = true;
			str.append(")");
			conditions.append(str);
		}
		if(prices != null || "".equals(prices)){
			splits = prices.split("-");
			if(lenght != conditions.length())
				str = new StringBuilder(" and (");
			else
				str = new StringBuilder(" (");
			if(splits.length == 2){
				str.append("p2.price >= " + splits[0] + " and p2.price <= " + splits[1]);
			}
			isFlat = true;
			str.append(")");
			conditions.append(str);
		}
		conditions.append(")");
		if(isFlat)
			queryStr.append(conditions.toString());
		SQLQuery query = getSession().createSQLQuery(queryStr.toString());
		query.setInteger("id", id);
		query.addEntity(CatalogProductEntity.class);
		return query.list();
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return (Long) getSession().createQuery(
				"select count(*) from CatalogProductEntity where typeId = 'Configurable Product'").uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<CatalogProductEntity> search(String key, String color, String size, String price) {
		// TODO Auto-generated method stub
		StringBuilder queryStr = new StringBuilder("select* from catalog_product_entity p1 where p1.url_key in "
				+ "(select distinct p2.url_key from catalog_product_entity p2 where (p2.name like:key1 "
				+ "or p2.sku like:key2)");
		String[] splits;
		StringBuilder str = null;
		if(color != null || "".equals(color)){
			splits = color.split(",");
			str = new StringBuilder(" and (");
			for(int i = 0; i < splits.length; i++){
				str.append("p2.color_value = " + "'" + splits[i] +  "'");
				if(i != (splits.length - 1))
					str.append(" or ");
			}
			str.append(")");
			queryStr.append(str);
		}
		if(size != null || "".equals(size)){
			splits = size.split(",");
			str = new StringBuilder(" and (");
			for(int i = 0; i < splits.length; i++){
				str.append("p2.size_value = " + "'" + splits[i] +  "'");
				if(i != (splits.length - 1))
					str.append(" or ");
			}
			str.append(")");
			queryStr.append(str);
		}
		if(price != null || "".equals(price)){
			splits = price.split("-");
			str = new StringBuilder(" and (");
			if(splits.length == 2){
				str.append("p2.price >= " + splits[0] + " and p2.price <= " + splits[1]);
			}
			str.append(")");
			queryStr.append(str);
		}
		queryStr.append(") and p1.type_id = 'Configurable Product' order by entity_id");
		SQLQuery query = getSession().createSQLQuery(queryStr.toString());
		query.setString("key1", "%" + key + "%");
		query.setString("key2", "%" + key + "%");
		query.addEntity(CatalogProductEntity.class);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public double getValueTax(Integer tax) {
		// TODO Auto-generated method stub
		SQLQuery query = getSession().createSQLQuery("SELECT r.* from tax_calculation c, tax_calculation_rate r "
				+ "where tax_class_id=:tax and c.tax_calculation_rate_id = r.tax_calculation_rate_id");
		query.setInteger("tax", tax);
		query.addEntity(TaxCalculationRate.class);
		List<TaxCalculationRate> list = query.list();
		if(list.size() == 0)
			return 0f;
		return list.get(0).getRate();
	}
	
}