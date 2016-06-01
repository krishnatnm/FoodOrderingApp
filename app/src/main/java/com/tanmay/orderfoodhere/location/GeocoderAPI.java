package com.tanmay.orderfoodhere.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.tanmay.orderfoodhere.view.interfaces.GeocoderListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by TaNMay on 3/3/2016.
 */
public class GeocoderAPI {

    public static String TAG = "AddressLocation ==>";
    public static GeocoderListener geocoderListener;

    public static void getAddressString(JSONObject jsonObj, String tag) {

        Log.d(TAG, "JSON: " + jsonObj.toString());
        String currentLocation = null;
        String street_address = null;
        String postal_code = null;

        try {
            String status = jsonObj.getString("status").toString();
            Log.d(TAG, "Status: " + status);

            if (status.equalsIgnoreCase("OK")) {
                JSONArray results = jsonObj.getJSONArray("results");
                Log.d(TAG, "Results: " + results.length()); //TODO delete this
                JSONObject r = results.getJSONObject(0);
                currentLocation = r.optString("formatted_address");
                Log.d(TAG, "Current Location: " + currentLocation);
                geocoderListener.onAddressResponse(tag, currentLocation);
            }


        } catch (JSONException e) {
            Log.e(TAG, "Testing => " + "Failed to load JSON");
            e.printStackTrace();
        }
    }

    public void getAddress(Context context, Double lat, Double lng, String label) {
        Log.d(TAG, "Latitude: " + lat + ", Longitude: " + lng);
        String address, knownName, city;
        knownName = "";
        address = "";
        city = "";
        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
        if (!geocoder.isPresent()) {
            new AsyncTaskForReverseGeocoder(lat, lng, label) {
                @Override
                protected void onPostExecute(String s) {
                    Log.d(TAG, "Geocoder " + s);
                    super.onPostExecute(s);
                }
            }.execute();
        } else {
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocation(lat, lng, 1);
                if (addresses != null) {
                    address = addresses.get(0).getAddressLine(0);
                    if (addresses.get(0).getAddressLine(1) != null) {
                        address = address + ", " + addresses.get(0).getAddressLine(1);
                    }
                    city = addresses.get(0).getLocality();
                    knownName = addresses.get(0).getFeatureName();
                }
                if (knownName == null) {
                    knownName = address;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            geocoderListener.onAddressResponse(label, address);
        }
    }

    public void getCoords(Context context, String description, String label) {
        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
        if (!geocoder.isPresent()) {
            String url = "https://maps.googleapis.com/maps/api/geocode/json?";
            try {
                description = URLEncoder.encode(description, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String address = "address=" + description;
            String sensor = "sensor=false";
            url = url + address + "&" + sensor;
            AsyncTaskForGeocoding downloadTask = new AsyncTaskForGeocoding(label);
            downloadTask.execute(url);
        } else {
            double lat = 0.0, lng = 0.0;
            LatLng latLng;
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocationName(description, 1);
                if (addresses.size() > 0) {
                    lat = addresses.get(0).getLatitude();
                    lng = addresses.get(0).getLongitude();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            geocoderListener.onCoordinateResponse(label, lat, lng);
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
            iStream.close();
        } catch (Exception e) {
            Log.d(TAG, "Exception while downloading url: " + e.toString());
        } finally {
            urlConnection.disconnect();
        }
        return data;
    }

    public class AsyncTaskForGeocoding extends AsyncTask<String, Integer, String> {
        String label, data = null;

        public AsyncTaskForGeocoding(String label) {
            this.label = label;
        }

        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask(label);
            parserTask.execute(result);
        }
    }

    public class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        JSONObject jObject;
        String label;

        public ParserTask(String label) {
            this.label = label;
        }


        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {
            List<HashMap<String, String>> places = null;
            GeocodeJsonParser parser = new GeocodeJsonParser();
            try {
                jObject = new JSONObject(jsonData[0]);
                places = parser.parse(jObject);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {
            HashMap<String, String> hmPlace = list.get(0);
            double lat = Double.parseDouble(hmPlace.get("lat"));
            double lng = Double.parseDouble(hmPlace.get("lng"));
            Log.d(TAG, "Coordinates: " + lat + ", " + lng);
            geocoderListener.onCoordinateResponse(label, lat, lng);
        }
    }

    public class AsyncTaskForReverseGeocoder extends AsyncTask<JSONObject, Integer, String> {
        Double lat, lng;
        JSONObject jsonObject;
        String label;

        public AsyncTaskForReverseGeocoder(Double lat, Double lng, String label) {
            this.lat = lat;
            this.lng = lng;
            this.label = label;
        }

        @Override
        protected String doInBackground(JSONObject... params) {
            Log.d(TAG, "URL: " + "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=true");
            HttpGet httpGet = new HttpGet("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=true");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            StringBuilder stringBuilder = new StringBuilder();

            try {
                response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                int b;
                while ((b = stream.read()) != -1) {
                    stringBuilder.append((char) b);
                }
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }

            jsonObject = new JSONObject();
            try {
                jsonObject = new JSONObject(stringBuilder.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Response: " + jsonObject.toString() + "...");

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            getAddressString(jsonObject, label);
        }
    }
}