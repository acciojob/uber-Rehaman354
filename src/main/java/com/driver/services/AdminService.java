package com.driver.services;

import com.driver.model.Admin;
import com.driver.model.Customer;
import com.driver.model.Driver;

import java.util.List;

public interface AdminService {

	public void adminRegister(Admin admin);

	public Admin updatePassword(Integer adminId, String password);

	public void deleteAdmin(int adminId);

	public List<Driver> getListOfDrivers();
	
	public List<Customer> getListOfCustomers();
}
