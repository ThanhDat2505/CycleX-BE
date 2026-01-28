package com.example.cyclexbe.controller;


import com.example.cyclexbe.dto.BikeListingHomeDTO;
import com.example.cyclexbe.exception.BadRequestException;
import com.example.cyclexbe.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
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
    public ResponseEntity<List<BikeListingHomeDTO>>search(@RequestParam String keyword, @RequestParam(defaultValue = "0") int page){
        if(keyword==null|| keyword.trim().isEmpty()){
            throw new BadRequestException("keyword must not be empty");
        }
        if (!keyword.matches("[\\p{L}0-9\\s]+")) {
            throw new BadRequestException("keyword contains invalid characters");
        }
        if(keyword.length()<2){
            throw new BadRequestException("Keyword must be at least 2 characters ");
        }
        if(page<0){
            throw new BadRequestException("Page must be greater than 0");
        }
        List<BikeListingHomeDTO> list= service.getSearch(keyword,page);
        return ResponseEntity.ok(list);
    }
}
