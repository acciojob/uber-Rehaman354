package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.List;
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
		customerRepository2.save(customer);
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
		List<Driver> driverList = driverRepository2.findAll();
		Driver driver = null;
		for(Driver currDriver : driverList){
			if(currDriver.getCab().getAvailable()){
				if((driver == null) || (currDriver.getDriverId() < driver.getDriverId())){
					driver = currDriver;
				}
			}
		}

		if(driver==null) {
			throw new Exception("No cab available!");
		}

		Customer customer = customerRepository2.findById(customerId).get();
		   TripBooking trip = new TripBooking();
		   trip.setCustomer(customer);
		   trip.setStatus(TripStatus.CONFIRMED);
		   trip.setDistanceInKm(distanceInKm);
		   trip.setDriver(driver);
		   trip.setFromLocation(fromLocation);
		   trip.setToLocation(toLocation);
		   int rate = driver.getCab().getPerKmRate();
		   trip.setBill(distanceInKm*rate);
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
