package com.example.repository;

import com.example.entity.AdminUser;

public interface IAdminDAO extends DAO<AdminUser>{

	public AdminUser getAdmin(String username, String password);
}
