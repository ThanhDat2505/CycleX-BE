package com.example.cyclexbe.repository;

import com.example.cyclexbe.dto.BikeListingHomeDTO;
import com.example.cyclexbe.entity.BikeListings;

import java.util.List;

public interface BikeListingRepository {
    public List<BikeListingHomeDTO> findAll();
    public List<BikeListingHomeDTO> filterPage(int page,int size);
    public List<BikeListingHomeDTO> search(String keyword,int page,int size);
}
