package com.example.android.sunshine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.sunshine.model.ResponseModel;
import com.example.android.sunshine.model.Weather;
import com.example.android.sunshine.network.NetworkConstants;
import com.example.android.sunshine.network.OpenWeatherMapService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> mWeekForecastAdapter;
    List<String> weekForecastString = new ArrayList<>();
    private Context mContext;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContext = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mWeekForecastAdapter = new ArrayAdapter<>(mContext,
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                weekForecastString);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mWeekForecastAdapter);
        retrieveData();
        return rootView;

    }


/*    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
        *//*    updateWeather();*//*
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    private void retrieveData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NetworkConstants.FORECAST_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenWeatherMapService service = retrofit
                .create(OpenWeatherMapService.class);

        Call<ResponseModel> listCall = service.getDailyForecast("Kyiv",14 , "7e9b0cb3874fe33ae8658c605de79c60");

        listCall.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call,
                                   Response<ResponseModel> response) {

                if (response.isSuccessful()) {
                    ResponseModel responseModel = response.body();
                    for (int i = 0; i < responseModel.getList().size(); i++) {

                        Weather weather = responseModel.getList().get(i);
                        //if (weather == null) break;
                        String weatherString = Utility.getReadableDateString(weather.getDt()) + " - " + weather.getWeather().get(0).getMain()
                                + " - " + Utility.formatHighLows(weather.getTemp().getMax(), weather.getTemp().getMin());
                        weekForecastString.add(weatherString);
                    }
                    mWeekForecastAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call,
                                  Throwable t) {
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
/*    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }*/

/*    private void updateWeather(){
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
        String location = Utility.getPreferredLocation(getActivity());
        weatherTask.execute(location);
    }*/

/*    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }*/

}