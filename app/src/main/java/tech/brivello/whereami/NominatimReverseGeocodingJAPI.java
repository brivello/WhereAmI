package tech.brivello.whereami;

/*
 * (C) Copyright 2014 Daniel Braun (http://www.daniel-braun.com/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Java library for reverse geocoding using Nominatim
 *
 * @author Daniel Braun
 * @version 0.1
 *
 */
public class NominatimReverseGeocodingJAPI {
    private final String NominatimInstance = "http://open.mapquestapi.com/nominatim/v1";//"http://nominatim.openstreetmap.org";

    private int zoomLevel = 18;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("use -help for instructions");
        } else if (args.length < 2) {
            if (args[0].equals("-help")) {
                System.out.println("Mandatory parameters:");
                System.out.println("   -lat [latitude]");
                System.out.println("   -lon [longitude]");
                System.out.println("\nOptional parameters:");
                System.out.println("   -zoom [0-18] | from 0 (country) to 18 (street address), default 18");
                System.out.println("   -osmid       | show also osm id and osm type of the address");
                System.out.println("\nThis page:");
                System.out.println("   -help");
            } else
                System.err.println("invalid parameters, use -help for instructions");
        } else {
            boolean latSet = false;
            boolean lonSet = false;
            boolean osm = false;

            double lat = -200;
            double lon = -200;
            int zoom = 18;

            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-lat")) {
                    try {
                        lat = Double.parseDouble(args[i + 1]);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Invalid latitude");
                        return;
                    }

                    latSet = true;
                    i++;
                    continue;
                } else if (args[i].equals("-lon")) {
                    try {
                        lon = Double.parseDouble(args[i + 1]);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Invalid longitude");
                        return;
                    }

                    lonSet = true;
                    i++;
                    continue;
                } else if (args[i].equals("-zoom")) {
                    try {
                        zoom = Integer.parseInt(args[i + 1]);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Invalid zoom");
                        return;
                    }

                    i++;
                    continue;
                } else if (args[i].equals("-osm")) {
                    osm = true;
                } else {
                    System.err.println("invalid parameters, use -help for instructions");
                    return;
                }
            }

            if (latSet && lonSet) {
                NominatimReverseGeocodingJAPI nominatim = new NominatimReverseGeocodingJAPI(zoom);
                Adress adress = nominatim.getAdress(lat, lon);
                System.out.println(adress);
                if (osm) {
                    System.out.print("OSM type: " + adress.getOsmType() + ", OSM id: " + adress.getOsmId());
                }
            } else {
                System.err.println("please specifiy -lat and -lon, use -help for instructions");
            }
        }
    }

    public NominatimReverseGeocodingJAPI() {
    }

    public NominatimReverseGeocodingJAPI(int zoomLevel) {
        if (zoomLevel < 0 || zoomLevel > 18) {
            System.err.println("invalid zoom level, using default value");
            zoomLevel = 18;
        }

        this.zoomLevel = zoomLevel;
    }

    public Adress getAdress(double lat, double lon) {
        Adress result = null;
        String urlString = NominatimInstance + "/reverse?format=json&addressdetails=1&lat=" + String.valueOf(lat) + "&lon=" + String.valueOf(lon) + "&zoom=" + zoomLevel;
        try {
            result = new Adress(getJSON(urlString), zoomLevel);
        } catch (IOException e) {
            System.err.println("Can't connect to server.");
            e.printStackTrace();
        }
        return result;
    }

    private String getJSON(String urlString) throws IOException {
        Log.d("getJSON", "start");
        URL url = new URL(urlString);
        Log.d("getJSON", String.valueOf(url));
        HttpURLConnection conn=(HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.connect();
        Log.d("getJSON", "connection open");

        try {
            //InputStream in = new BufferedInputStream(conn.getInputStream());
            int status=conn.getResponseCode();
            Log.d("getJSON", String.valueOf(status));
        } finally {
            conn.disconnect();
            Log.d("getJSON", "fail");
        }


        /*InputStream is = conn.getInputStream();
        Log.d("getJSON", "input");
        String json = IOUtils.toString(is, "UTF-8");
        is.close();//< original get json - does not get past first line*/

        /*BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        Log.d("getJSON", "input");
        String json = org.apache.commons.io.IOUtils.toString(in);
        Log.d("getJSON", "end");*/
        //^my attempt at a solution - does not get past first line

        String json="{\"place_id\":\"98935256\",\"licence\":\"Data © OpenStreetMap contributors, ODbL 1.0. http:\\/\\/www.openstreetmap.org\\/copyright\",\"osm_type\":\"way\",\"osm_id\":\"120854950\",\"lat\":\"25.5454971\",\"lon\":\"86.9894605\",\"display_name\":\"Chausa, Saharsa, Madhepura, Bihar, 852213, India\",\"address\":{\"village\":\"Chausa\",\"county\":\"Saharsa\",\"state_district\":\"Madhepura\",\"state\":\"Bihar\",\"postcode\":\"852213\",\"country\":\"India\",\"country_code\":\"in\"},\"boundingbox\":[\"25.539486\",\"25.5521165\",\"86.9802752\",\"86.9979407\"]}";
        return json;
    }
}
