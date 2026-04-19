
package com.nhom1.hotelmanagement.repositories;

import com.nhom1.hotelmanagement.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomerRepository extends JpaRepository<Customer, Long>{
    Customer findByPhone(String phone);

    public Customer findByIdCard(String idCard);

    public Customer findByEmail(String email);
    
    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByIdCard(String idCard);
    
}
