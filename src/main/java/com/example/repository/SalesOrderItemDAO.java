package com.example.repository;

import org.springframework.stereotype.Repository;

import com.example.entity.SalesOrderItem;

@Repository
public class SalesOrderItemDAO extends AbstractDAO<SalesOrderItem> implements ISalesOrderItemDAO{

}
