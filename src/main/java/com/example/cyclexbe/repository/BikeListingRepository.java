package com.example.cyclexbe.repository;

import com.example.cyclexbe.dto.BikeListingDetail;
import com.example.cyclexbe.dto.BikeListingHomeDTO;

import java.util.List;

public interface BikeListingRepository {
    public List<BikeListingHomeDTO> findAll();
    public List<BikeListingHomeDTO> pagination(int page, int size);
    public List<BikeListingHomeDTO> search(String keyword,int page,int size);

    public void increaseview(int listingId);
    public BikeListingDetail listingdetail(int listingId);
}
