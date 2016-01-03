package com.example.repository;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.example.entity.CatalogCategoryEntity;

@Repository
public class CategoryDAO extends AbstractDAO<CatalogCategoryEntity> implements
		ICategoryDAO {

	@SuppressWarnings("unchecked")
	public List<CatalogCategoryEntity> getAllCategoryByOrder() {
		// TODO Auto-generated method stub
		Query query = getSession()
				.createQuery("from CatalogCategoryEntity order by parent_id, position");
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<CatalogCategoryEntity> getCategoryByParent(Integer parent) {
		// TODO Auto-generated method stub
		Query query = getSession()
				.createQuery("from CatalogCategoryEntity where parent_id=:parent order by position");
		query.setInteger("parent", parent);
;		return query.list();
	}

	public CatalogCategoryEntity getRootCategory() {
		// TODO Auto-generated method stub
		Query query = getSession()
				.createQuery("from CatalogCategoryEntity where level = 0");
		return (CatalogCategoryEntity) query.uniqueResult();
	}

	public CatalogCategoryEntity findCategoryByUrl(String urlPath) {
		// TODO Auto-generated method stub
		Query query = getSession()
				.createQuery("from CatalogCategoryEntity where urlPath=:urlPath");
		query.setString("urlPath", urlPath);
		return (CatalogCategoryEntity) query.uniqueResult();
	}
}
