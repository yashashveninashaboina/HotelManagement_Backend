package com.hotelmanagement.controller;

import com.hotelmanagement.dto.BookingResponse;
import com.hotelmanagement.dto.CustomerRequest;
import com.hotelmanagement.model.Customer;
import com.hotelmanagement.service.BookingService;
import com.hotelmanagement.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerRequest request) {
        Customer customer = customerService.createCustomer(request);
        return new ResponseEntity<>(customer, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody CustomerRequest request) {
        Customer updatedCustomer = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer deleted successfully with ID: " + id);
    }

    @GetMapping("/{id}/bookings")
    public ResponseEntity<List<BookingResponse>> getCustomerBookings(@PathVariable Long id) {
        List<BookingResponse> bookings = bookingService.getBookingsByCustomerId(id);
        return ResponseEntity.ok(bookings);
    }
}
