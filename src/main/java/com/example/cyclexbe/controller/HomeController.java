package com.example.cyclexbe.controller;

import com.example.cyclexbe.dto.BikeListingHomeDTO;
import com.example.cyclexbe.entity.BikeListings;
import com.example.cyclexbe.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/home")
public class HomeController {
    private HomeService service;

    @Autowired
    public HomeController(HomeService service) {
        this.service = service;
    }



    @GetMapping
    public List<BikeListingHomeDTO> list() {
    return service.getAllList();
    }
}
