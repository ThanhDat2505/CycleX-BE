package com.example.cyclexbe.service;

import com.example.cyclexbe.dto.BikeListingHomeDTO;
import com.example.cyclexbe.entity.BikeListings;
import com.example.cyclexbe.repository.BikeListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeServiceImp implements HomeService{

    private BikeListingRepository repository;

    @Autowired
    public HomeServiceImp(BikeListingRepository repository) {
        this.repository = repository;
    }



    @Override
    public List<BikeListingHomeDTO> getAllList() {
        return repository.findAll();
    }
}
