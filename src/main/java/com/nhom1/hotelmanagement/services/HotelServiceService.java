package com.nhom1.hotelmanagement.services;

import com.nhom1.hotelmanagement.dto.ServiceRequest;
import com.nhom1.hotelmanagement.dto.ServiceResponse;
import com.nhom1.hotelmanagement.entities.HotelService;
import com.nhom1.hotelmanagement.repositories.HotelServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HotelServiceService {

    @Autowired
    private HotelServiceRepository serviceRepository;

    public List<HotelService> listAll() {
        return serviceRepository.findAll();
    }

    public HotelService getById(Long serviceId) {
        return serviceRepository.findById(serviceId).orElse(null);
    }

    public HotelService create(ServiceRequest dto) {
        HotelService service = new HotelService();
        service.setName(dto.getName());
        service.setPrice(dto.getPrice());
        return serviceRepository.save(service);
    }

    public HotelService update(Long serviceId, ServiceRequest dto) {
        Optional<HotelService> optionalService = serviceRepository.findById(serviceId);
        if (!optionalService.isPresent()) {
            return null;
        }

        HotelService service = optionalService.get();
        if (dto.getName() != null) {
            service.setName(dto.getName());
        }
        if (dto.getPrice() != null) {
            service.setPrice(dto.getPrice());
        }
        return serviceRepository.save(service);
    }

    public void delete(Long serviceId) {
        serviceRepository.deleteById(serviceId);
    }

    public List<ServiceResponse> listAllDto() {
        return listAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public ServiceResponse toDto(HotelService service) {
        if (service == null) return null;
        ServiceResponse dto = new ServiceResponse();
        dto.setServiceId(service.getServiceId());
        dto.setName(service.getName());
        dto.setPrice(service.getPrice());
        return dto;
    }
}
