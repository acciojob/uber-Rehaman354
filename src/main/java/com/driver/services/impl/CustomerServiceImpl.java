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
		Customer customer = customerRepository2.findById(customerId).get();
		List<TripBooking> bookedTrips = customer.getTripBookingList();

		for(TripBooking trip : bookedTrips){
			if(trip.getStatus()==TripStatus.CONFIRMED) {
				Driver driver = trip.getDriver();
				Cab cab = driver.getCab();
				cab.setAvailable(true);
				trip.setStatus(TripStatus.CANCELED);
			}
		}
		customerRepository2.delete(customer);
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

		TripBooking newTripBooked = new TripBooking();
		newTripBooked.setCustomer(customerRepository2.findById(customerId).get());
		newTripBooked.setFromLocation(fromLocation);
		newTripBooked.setToLocation(toLocation);
		newTripBooked.setDistanceInKm(distanceInKm);
		newTripBooked.setStatus(TripStatus.CONFIRMED);
		newTripBooked.setDriver(driver);
		int rate = driver.getCab().getPerKmRate();
		newTripBooked.setBill(distanceInKm*rate);

		driver.getCab().setAvailable(false);
		driverRepository2.save(driver);

		Customer customer = customerRepository2.findById(customerId).get();
		customer.getTripBookingList().add(newTripBooked);
		customerRepository2.save(customer);


		tripBookingRepository2.save(newTripBooked);
		return newTripBooked;
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
