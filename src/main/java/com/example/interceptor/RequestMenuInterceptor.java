package com.example.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.example.dto.CategoryModel;
import com.example.entity.CatalogCategoryEntity;
import com.example.service.IProductSevice;

public class RequestMenuInterceptor extends HandlerInterceptorAdapter{

	@Autowired
	private IProductSevice service;
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		CategoryModel categories = (CategoryModel) session.getAttribute("categories");
		if(categories == null){
			categories = this.buildTreeCategory(0);
			session.setAttribute("categories", categories);
		}
		return true;
	}
	
	public CategoryModel buildTreeCategory(int parent) {
		// TODO Auto-generated method stub
		CatalogCategoryEntity root = service.getRootCategory();
		CategoryModel model = this.convertEntity(root);
		List<CatalogCategoryEntity> level1 = service.getCategoryByParent(model
				.getEntityId());
		int lv2Inx = 0, lv3Inx = 0;
		for (CatalogCategoryEntity lv1 : level1) {
			List<CatalogCategoryEntity> level2 = service
					.getCategoryByParent(lv1.getEntityId());
			model.getSubCategories().add(this.convertEntity(lv1));
			lv3Inx = 0;
			for (CatalogCategoryEntity lv2 : level2) {
				List<CatalogCategoryEntity> level3 = service
						.getCategoryByParent(lv2.getEntityId());
				model.getSubCategories().get(lv2Inx).getSubCategories()
						.add(this.convertEntity(lv2));
				for (CatalogCategoryEntity lv3 : level3) {
					model.getSubCategories().get(lv2Inx).getSubCategories()
							.get(lv3Inx).getSubCategories()
							.add(this.convertEntity(lv3));
				}
				lv3Inx++;
			}
			lv2Inx++;
		}
		return model;
	}

	public CategoryModel convertEntity(CatalogCategoryEntity entity) {
		CategoryModel model = new CategoryModel();
		model.setEntityId(entity.getEntityId());
		model.setLevel(entity.getLevel());
		model.setName(entity.getName());
		model.setNameEn(entity.getNameEn());
		model.setUrlPath(entity.getUrlPath());
		return model;
	}
	
}
