package com.example.android.sunshine.network;

public class NetworkConstants {

    public static final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/";
    public static final String RESPONSE_FORMAT_JSON = "json";
    public static final String RESPONSE_UNIT_METRIC = "metric";
    public static final String RESPONSE_COUNT_DEFAULT = "14";

    public static final int SERVER_RESPONSE_OK = 200;
    public static final int SERVER_RESPONSE_CITY_NOT_FOUND = 404;
}