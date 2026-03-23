
package com.nhom1.hotelmanagement.repositories;

import com.nhom1.hotelmanagement.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomerRepository extends JpaRepository<Customer, Long>{
    
}
