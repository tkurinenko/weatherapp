package com.example.android.sunshine;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.android.sunshine.data.WeatherDBHelper;

public class TestBD extends AndroidTestCase {

    public void testCreateDB() throws Throwable {
        mContext.deleteDatabase(WeatherDBHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDBHelper(
                this.mContext
        ).getWritableDatabase();
        assertEquals(true,db.isOpen());
        db.close();
    }
}
