package com.tanmay.orderfoodhere.view.interfaces;

/**
 * Created by TaNMay on 3/12/2016.
 */
public interface GeocoderListener {

    void onAddressResponse(String label, String address);
    void onCoordinateResponse(String label, Double lat, Double lng);

}
