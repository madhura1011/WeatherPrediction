package com.madhura.visualcrossingweatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.madhura.visualcrossingweatherapp.RecyclerViewAdapter.WeeklyRecylcerViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.*;

public class WeekWeatherReport extends AppCompatActivity {

    //https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Chicago,IL/?unitGroup=us&lang=en&key=56GFX4FERZEQ6VLUMQS6ZRRM6
    Map<String, Object> daysList;
    WeeklyRecylcerViewAdapter weeklyRecylcerViewAdapter;
    RecyclerView weeklyRecyclerView;
    List<Map<String,String>> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_weather_report);
        Bundle bundle =getIntent().getBundleExtra("DayList");
        daysList = (Map<String, Object>) bundle.getSerializable("DayList");
        setTitle((CharSequence) daysList.get("address") + " 15 Day");


        ArrayList<Map<String, Object>> daysData = (ArrayList<Map<String, Object>>) daysList.get("daysList");
        for(int i = 0; i < daysData.size(); i++){
            String dayDate = (String) daysData.get(i).get("dateTimeEpoch");
            String formatedDayDate = convertDateFormat(dayDate);
            String minTemp  = (String) daysData.get(i).get("tempMin");
            String maxTemp  = (String) daysData.get(i).get("tempMax");
            String description = (String) daysData.get(i).get("description");
            String precipprob = (String) daysData.get(i).get("precipprob");
            String uvIndex = (String) daysData.get(i).get("uvIndex");
            String icon = (String) daysData.get(i).get("icon");
            ArrayList<Map<String,String>> hourlyArrayList = (ArrayList<Map<String, String>>) daysData.get(i).get("hourlyArrayList");
            String morning = hourlyArrayList.get(8).get("temp");
            String afternoon = hourlyArrayList.get(13).get("temp");
            String evening = hourlyArrayList.get(17).get("temp");
            String night = hourlyArrayList.get(23).get("temp");
            Map<String, String> map = new HashMap<>();
            map.put("dayDate",formatedDayDate);
            map.put("Temp",maxTemp+"/"+minTemp);
            map.put("description",description);
            map.put("precipprob","("+precipprob+"% precip.)");
            map.put("uvIndex","UV Index: "+uvIndex);
            map.put("icon",icon);
            map.put("morning",morning);
            map.put("afternoon",afternoon);
            map.put("evening",evening);
            map.put("night",night);
            list.add(map);
            if(list.size()==15)
                break;
        }
        System.out.println(list);
        weeklyRecyclerView = findViewById(R.id.weeklyRecycler);
        weeklyRecylcerViewAdapter = new WeeklyRecylcerViewAdapter(this, list);
        weeklyRecyclerView.setAdapter(weeklyRecylcerViewAdapter);
        weeklyRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
    }

    private String convertDateFormat(String dayDate) {
        long datetimeEpoch = Long.parseLong(dayDate);
        Date dateTime = new Date(datetimeEpoch * 1000);
        SimpleDateFormat dayDateformat = new SimpleDateFormat("EEEE, MM/dd", Locale.getDefault());
        return dayDateformat.format(dateTime);
    }
}