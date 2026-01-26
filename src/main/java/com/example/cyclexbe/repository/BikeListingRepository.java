package com.example.cyclexbe.repository;

import com.example.cyclexbe.dto.BikeListingHomeDTO;
import com.example.cyclexbe.entity.BikeListings;

import java.util.List;

public interface BikeListingRepository {
    public List<BikeListingHomeDTO> findAll();
}
