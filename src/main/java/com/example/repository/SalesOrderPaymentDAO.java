package com.example.repository;

import org.springframework.stereotype.Repository;

import com.example.entity.SalesOrderPayment;

@Repository
public class SalesOrderPaymentDAO extends AbstractDAO<SalesOrderPayment> implements
		ISalesOrderPaymentDAO {

}
