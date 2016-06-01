package com.example.android.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.sql.SQLException;

/**
 * Created by tkurinenko on 06.05.2016.
 */
public class WeatherProvider extends ContentProvider {

    private static final int WEATHER = 100;
    private static final int WEATHER_WITH_LOCATION = 101;
    private static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    private static final int LOCATION = 300;
    private static final int LOCATION_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private WeatherDBHelper mOpenHelper;

    private static final SQLiteQueryBuilder SWeatherByLocationSettingsQueryBuilder;
    private static final String sLocationSettingSelection;
    private static final String sLocationSettingWithStartDateSelection;
    private static final String sLocationSettingWithDaySelection;


//static constructor for a class that describe joins between two tables
    static {
        SWeatherByLocationSettingsQueryBuilder = new SQLiteQueryBuilder();
        SWeatherByLocationSettingsQueryBuilder.setTables(
                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        WeatherContract.LocationEntry.TABLE_NAME +
                        " ON " +WeatherContract.WeatherEntry.TABLE_NAME +
                        "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
                        " = " + WeatherContract.WeatherEntry.TABLE_NAME +
                        "." +WeatherContract.LocationEntry._ID);

    sLocationSettingSelection =
            WeatherContract.LocationEntry.TABLE_NAME +
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING +
                    " = ? ";

    sLocationSettingWithStartDateSelection =
            WeatherContract.LocationEntry.TABLE_NAME +
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING +
                    " = ? AND " + WeatherContract.WeatherEntry.COLUMN_DATE + " >= ? ";

    sLocationSettingWithDaySelection =
            WeatherContract.LocationEntry.TABLE_NAME +
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING +
                    " = ? AND " + WeatherContract.WeatherEntry.COLUMN_DATE + " = ? ";
    }

    private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        String startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == null) {
            selection = sLocationSettingSelection;
            selectionArgs = new String[] {locationSetting};
        } else {
            selection = sLocationSettingWithStartDateSelection;
            selectionArgs = new String[] {locationSetting, startDate};
        }

        return SWeatherByLocationSettingsQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getWeatherByLocationSettingwithDate(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        String day = WeatherContract.WeatherEntry.getDateFromUri(uri);

        return SWeatherByLocationSettingsQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                sLocationSettingWithDaySelection,
                new String[] {locationSetting, day},
                null,
                null,
                sortOrder
        );
    }


    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WeatherContract.CONTENT_AUTHORITY;


        matcher.addURI(authority, WeatherContract.PATH_WEATHER, WEATHER);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*/*", WEATHER_WITH_LOCATION_AND_DATE);


        matcher.addURI(authority, WeatherContract.PATH_LOCATION, LOCATION);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/#", LOCATION_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {

        mOpenHelper = new WeatherDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case WEATHER_WITH_LOCATION_AND_DATE: {
                retCursor = getWeatherByLocationSettingwithDate(uri,projection,sortOrder);
                break;
            }
            case WEATHER_WITH_LOCATION: {
                retCursor = getWeatherByLocationSetting(uri,projection,sortOrder);
                break;
            }
            case WEATHER: {
                retCursor = mOpenHelper.getReadableDatabase().query(WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,selection,selectionArgs,null,null,sortOrder);
                break;
            }
            case LOCATION_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(WeatherContract.LocationEntry.TABLE_NAME,
                        projection, WeatherContract.LocationEntry._ID + " = " + ContentUris.parseId(uri) + "'", selectionArgs ,null,null,sortOrder);
                break;
            }
            case LOCATION: {
                retCursor = mOpenHelper.getReadableDatabase().query(WeatherContract.LocationEntry.TABLE_NAME,
                        projection,selection,selectionArgs,null,null,sortOrder);
                break;
            }
            default:throw  new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //register Content Observer to watch for changes that happen of that uri
        assert retCursor != null;
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;


    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        //return uniq MIME TYPE
        switch (match) {
            case WEATHER_WITH_LOCATION_AND_DATE:
                return WeatherContract.WeatherEntry.CONTENT_ITEM;
            case WEATHER_WITH_LOCATION:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case WEATHER:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case LOCATION:
                return WeatherContract.LocationEntry.CONTENT_TYPE;
            case LOCATION_ID:
                return WeatherContract.LocationEntry.CONTENT_ITEM;
            default:
                throw new UnsupportedOperationException("Unknow uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id;

        // Only match for the base URI, this will ensure that all content observers
        // will be notified of the change; descendants of base URI will be notified
        switch (match) {
            case WEATHER:
                _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = WeatherContract.WeatherEntry.buildWeatherUri(_id);
                } else {
                    try {
                        throw new SQLException("Failed to insert row into " + uri);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case LOCATION:
                _id = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = WeatherContract.LocationEntry.buildLocationUri(_id);
                } else {
                    try {
                        throw new SQLException("Failed to insert row into " + uri);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify all observers that the URI has changed
        getContext().getContentResolver().notifyChange(uri, null);

        return uri;




    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowDeleted;

        switch (match) {
            case WEATHER:
                rowDeleted = db.delete(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case LOCATION:
                rowDeleted = db.delete(
                        WeatherContract.LocationEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (selection == null || rowDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowUpdated;

        switch (match) {
            case WEATHER:
                rowUpdated = db.update(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            case LOCATION:
                rowUpdated = db.update(
                        WeatherContract.LocationEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "  + uri);
        }

        if (rowUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowUpdated;
    }
}
