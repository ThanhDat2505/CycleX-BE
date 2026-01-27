package com.example.cyclexbe.controller;


import com.example.cyclexbe.dto.BikeListingHomeDTO;
import com.example.cyclexbe.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/listings/")
public class ListingController {
    private ListingService service;

    @Autowired
    public ListingController(ListingService service) {
        this.service = service;
    }
    @GetMapping("/pagination")
    //Phân trang
    public List<BikeListingHomeDTO> Pagination(@RequestParam(defaultValue = "0") int page){
        return service.getFilterPage(page);
    }

    @GetMapping("/search")
    public List<BikeListingHomeDTO> search(@RequestParam String keyword,@RequestParam(defaultValue = "0") int page){
        return service.getSearch(keyword,page);
    }
}
