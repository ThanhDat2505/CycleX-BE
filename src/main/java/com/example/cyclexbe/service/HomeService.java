package com.example.cyclexbe.service;

import com.example.cyclexbe.dto.BikeListingHomeDTO;
import com.example.cyclexbe.entity.BikeListings;

import java.util.List;

public interface HomeService {
    public List<BikeListingHomeDTO> getAllList();
}
