package com.example.cyclexbe.service;

import com.example.cyclexbe.dto.BikeListingHomeDTO;

import java.util.List;

public interface ListingService {
    public List<BikeListingHomeDTO> getAllList();
    public List<BikeListingHomeDTO> getFilterPage(int page);
    public List<BikeListingHomeDTO> getSearch(String keyword,int page);
}
