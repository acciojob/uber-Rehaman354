package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		Customer customer1=new Customer();
		customer1.setMobile(customer.getMobile());
		customer1.setPassword(customer.getPassword());
		customerRepository2.save(customer1);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
         customerRepository2.deleteById(customerId);
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		int drivercount=driverRepository2.findAll().size();
		int minId=Integer.MAX_VALUE;
		for(Driver driver:driverRepository2.findAll())
		{
			if(driver.getCab().getAvailable())
			minId=Math.min(minId,driver.getDriverId());
		}
		if(minId==Integer.MAX_VALUE)
		{
			throw new Exception("No cab available!");
		}
		//else
		   TripBooking trip = new TripBooking();
		   Customer customer = customerRepository2.findById(customerId).get();
		   trip.setCustomer(customer);
		   trip.setStatus(TripStatus.CONFIRMED);
		   trip.setDistanceInKm(distanceInKm);
		   Driver driver=driverRepository2.findById(minId).get();
		   trip.setDriver(driver);
		   trip.setFromLocation(fromLocation);
		   trip.setToLocation(toLocation);
		   //before returning update customer,driver attributes
		   driver.getTripBookingList().add(trip);
		   customer.getTripBookingList().add(trip);
		   driver.getCab().setAvailable(false);//making cab not available
		   tripBookingRepository2.save(trip);
		   //return trip
		   return trip;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
        TripBooking trip=tripBookingRepository2.findById(tripId).get();
		trip.getDriver().getCab().setAvailable(true);
		trip.setStatus(TripStatus.CANCELED);
		trip.setBill(0);
		tripBookingRepository2.save(trip);

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking trip=tripBookingRepository2.findById(tripId).get();
		trip.getDriver().getCab().setAvailable(true);
		trip.setStatus(TripStatus.COMPLETED);
		tripBookingRepository2.save(trip);
	}
}
