
package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.CustomerDTO;
import com.nhom1.hotelmanagement.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
public class CustomerRestController {
    @Autowired
    private BookingService bookingService;
    
    @GetMapping("check")
    public ResponseEntity<?> checkCustomer(@RequestParam String phone){
        CustomerDTO.InfoForBooking info = bookingService.checkCustomer(phone);
        if(info != null){
            return ResponseEntity.ok(info);
        }
        return ResponseEntity.notFound().build();
    }
}
