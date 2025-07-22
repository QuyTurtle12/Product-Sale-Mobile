package com.example.product_sale_app.model;
public class StoreLocationDto {
    private int    locationId;
    private double latitude;
    private double longitude;
    private String address;

    public int getLocationId()     { return locationId; }
    public void setLocationId(int id) { this.locationId = id; }

    public double getLatitude()       { return latitude; }
    public void setLatitude(double lat) { this.latitude = lat; }

    public double getLongitude()         { return longitude; }
    public void setLongitude(double lon) { this.longitude = lon; }

    public String getAddress()           { return address; }
    public void setAddress(String address) { this.address = address; }
}
