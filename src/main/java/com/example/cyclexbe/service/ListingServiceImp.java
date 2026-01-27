package com.example.cyclexbe.service;

import com.example.cyclexbe.dto.BikeListingHomeDTO;
import com.example.cyclexbe.repository.BikeListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListingServiceImp implements ListingService {

    private BikeListingRepository repository;

    @Autowired
    public ListingServiceImp(BikeListingRepository repository) {
        this.repository = repository;
    }



    @Override
    public List<BikeListingHomeDTO> getAllList() {
        return repository.findAll();
    }

    @Override
    public List<BikeListingHomeDTO> getFilterPage(int page) {
    int size=10;
    return repository.filterPage(page,size);
    }

    @Override
    public List<BikeListingHomeDTO> getSearch(String keyword, int page) {
       int size=10;
       return repository.search(keyword,page,size);
    }
}
