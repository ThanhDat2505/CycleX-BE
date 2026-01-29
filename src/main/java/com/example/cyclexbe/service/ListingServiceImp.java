package com.example.cyclexbe.service;

import com.example.cyclexbe.dto.BikeListingDetail;
import com.example.cyclexbe.dto.BikeListingHomeDTO;
import com.example.cyclexbe.repository.BikeListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    return repository.pagination(page,size);
    }

    @Override
    public List<BikeListingHomeDTO> getSearch(String keyword, int page) {
       int size=10;
       return repository.search(keyword,page,size);
    }

    @Override
    @Transactional
    public void getIncreaseview(int listingId) {
        repository.increaseview(listingId);
    }

    @Override
    public BikeListingDetail listingdetail(int listingId) {
        BikeListingDetail dto=repository.listingdetail(listingId);
        return dto;
    }
}
