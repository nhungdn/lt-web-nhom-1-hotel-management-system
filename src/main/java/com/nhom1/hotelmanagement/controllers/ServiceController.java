package com.nhom1.hotelmanagement.controllers;

import com.nhom1.hotelmanagement.dto.ServiceRequest;
import com.nhom1.hotelmanagement.entities.HotelService;
import com.nhom1.hotelmanagement.services.HotelServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/services")
public class ServiceController {

    @Autowired
    private HotelServiceService serviceService;

    @GetMapping
    public String listServices(Model model) {
        model.addAttribute("services", serviceService.listAllDto());
        return "services";
    }

    @GetMapping("/create")
    public String createServiceForm(Model model) {
        model.addAttribute("service", new ServiceRequest());
        return "service-form";
    }

    @PostMapping("/create")
    public String createService(@ModelAttribute ServiceRequest dto) {
        serviceService.create(dto);
        return "redirect:/services";
    }

    @GetMapping("/edit/{id}")
    public String editServiceForm(@PathVariable Long id, Model model) {
        HotelService existing = serviceService.getById(id);
        if (existing == null) {
            return "redirect:/services";
        }
        model.addAttribute("service", serviceService.toDto(existing));
        return "service-form";
    }

    @PostMapping("/update/{id}")
    public String updateService(@PathVariable Long id, @ModelAttribute ServiceRequest dto) {
        serviceService.update(id, dto);
        return "redirect:/services";
    }

    @PostMapping("/delete/{id}")
    public String deleteService(@PathVariable Long id) {
        serviceService.delete(id);
        return "redirect:/services";
    }
}
