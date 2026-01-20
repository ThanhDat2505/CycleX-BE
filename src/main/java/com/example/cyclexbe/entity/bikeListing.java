package com.example.cyclexbe.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "BikeListings")
public class bikeListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "listing_id")
    private int listingid;

   @ManyToOne
   @JoinColumn(name = "seller_id")
    private int sellerid;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "bike_type")
    private String biketype;

    @Column(name = "brand")
    private String brand;

    @Column(name = "manufacture_year")
    private int manufactureyear;

    @Column(name = "model")
    private String model;

    @Column(name = "condition ")
    private String condition;

    @Column(name = "usage_time")
    private String usagetime;

    @Column(name = "reason_for_sale")
    private String reasonforsale;

    @Column(name = "price")
    private double price;

    @Column(name = "location_city")
    private String locationcity;

    @Column(name = "pickup_address")
    private String pickupaddress;

    @Column(name = "status")
    private String status;

    @Column(name = "views_count")
    private String viewscount;

    @Column(name = "created_at")
    private LocalDateTime createdat;

    @Column(name = "updated_at")
    private LocalDateTime updatedat;

    public bikeListing() {
    }

    public bikeListing(int listingid, int sellerid, String title, String description, String biketype, String brand, int manufactureyear, String model, String condition, String usagetime, String reasonforsale, double price, String locationcity, String pickupaddress, String status, String viewscount, LocalDateTime createdat, LocalDateTime updatedat) {
        this.listingid = listingid;
        this.sellerid = sellerid;
        this.title = title;
        this.description = description;
        this.biketype = biketype;
        this.brand = brand;
        this.manufactureyear = manufactureyear;
        this.model = model;
        this.condition = condition;
        this.usagetime = usagetime;
        this.reasonforsale = reasonforsale;
        this.price = price;
        this.locationcity = locationcity;
        this.pickupaddress = pickupaddress;
        this.status = status;
        this.viewscount = viewscount;
        this.createdat = createdat;
        this.updatedat = updatedat;
    }

    public bikeListing(int sellerid, String title, String description, String biketype, String brand, int manufactureyear, String model, String condition, String usagetime, String reasonforsale, double price, String locationcity, String pickupaddress, String status, String viewscount, LocalDateTime createdat, LocalDateTime updatedat) {
        this.sellerid = sellerid;
        this.title = title;
        this.description = description;
        this.biketype = biketype;
        this.brand = brand;
        this.manufactureyear = manufactureyear;
        this.model = model;
        this.condition = condition;
        this.usagetime = usagetime;
        this.reasonforsale = reasonforsale;
        this.price = price;
        this.locationcity = locationcity;
        this.pickupaddress = pickupaddress;
        this.status = status;
        this.viewscount = viewscount;
        this.createdat = createdat;
        this.updatedat = updatedat;
    }

    public int getListingid() {
        return listingid;
    }

    public void setListingid(int listingid) {
        this.listingid = listingid;
    }

    public int getSellerid() {
        return sellerid;
    }

    public void setSellerid(int sellerid) {
        this.sellerid = sellerid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBiketype() {
        return biketype;
    }

    public void setBiketype(String biketype) {
        this.biketype = biketype;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getManufactureyear() {
        return manufactureyear;
    }

    public void setManufactureyear(int manufactureyear) {
        this.manufactureyear = manufactureyear;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getUsagetime() {
        return usagetime;
    }

    public void setUsagetime(String usagetime) {
        this.usagetime = usagetime;
    }

    public String getReasonforsale() {
        return reasonforsale;
    }

    public void setReasonforsale(String reasonforsale) {
        this.reasonforsale = reasonforsale;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getLocationcity() {
        return locationcity;
    }

    public void setLocationcity(String locationcity) {
        this.locationcity = locationcity;
    }

    public String getPickupaddress() {
        return pickupaddress;
    }

    public void setPickupaddress(String pickupaddress) {
        this.pickupaddress = pickupaddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getViewscount() {
        return viewscount;
    }

    public void setViewscount(String viewscount) {
        this.viewscount = viewscount;
    }

    public LocalDateTime getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDateTime createdat) {
        this.createdat = createdat;
    }

    public LocalDateTime getUpdatedat() {
        return updatedat;
    }

    public void setUpdatedat(LocalDateTime updatedat) {
        this.updatedat = updatedat;
    }
}
