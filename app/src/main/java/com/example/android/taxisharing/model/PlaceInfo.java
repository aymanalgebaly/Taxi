package com.example.android.taxisharing.model;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class PlaceInfo {

    private String address;
    private String id;
    private String attributions;
    private String phoneNumber;
    private Uri webSite;
    private Float rating;
    private String name;
    private LatLng latLng;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttributions() {
        return attributions;
    }

    public void setAttributions(String attributions) {
        this.attributions = attributions;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Uri getWebSite() {
        return webSite;
    }

    public void setWebSite(Uri webSite) {
        this.webSite = webSite;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public PlaceInfo(String address, String id, String attributions, String phoneNumber,
                     Uri webSite, Float rating, String name, LatLng latLng) {
        this.address = address;
        this.id = id;
        this.attributions = attributions;
        this.phoneNumber = phoneNumber;
        this.webSite = webSite;
        this.rating = rating;
        this.name = name;
        this.latLng = latLng;
    }

    public PlaceInfo() {
    }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "address='" + address + '\'' +
                ", id='" + id + '\'' +
                ", attributions='" + attributions + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", webSite=" + webSite +
                ", rating=" + rating +
                ", name='" + name + '\'' +
                ", latLng=" + latLng +
                '}';
    }
}
