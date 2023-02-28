package com.madhura.visualcrossingweatherapp;

import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class FetchData {
    private static MainActivity mainActivity;
    private static RequestQueue queue;
    private static List<Object> finalList = new ArrayList<>();
    private static String unit;
    private static final String UNITF = "°F";
    private static final String UNITC = "°C";

    //https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Chicago,IL/?unitGroup=us&lang=en&key=56GFX4FERZEQ6VLUMQS6ZRRM6



    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void fetchData(MainActivity activity, String location, String units) {
        mainActivity = activity;
        queue = Volley.newRequestQueue(mainActivity);
        unit = units;
        Uri.Builder builder = Uri.parse("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/").buildUpon();
        builder.appendPath(location);
        builder.appendQueryParameter("unitGroup",units);
        builder.appendQueryParameter("lang","en");
        builder.appendQueryParameter("key", "56GFX4FERZEQ6VLUMQS6ZRRM6");
        String url = builder.build().toString();
        Response.Listener<JSONObject> jsonObjectListener = response -> parseJSON(response.toString());
        Response.ErrorListener errorListener = error -> {
            mainActivity.updateDetails(null);
        };
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,jsonObjectListener,errorListener);
        queue.add(jsonObjectRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void parseJSON(String string) {
        try {
            Map<String, Object> finalMap = new HashMap<>();

            JSONObject jsonObject = new JSONObject(string);
            String address = jsonObject.getString("address");
            String timezone = jsonObject.getString("timezone");
            String tzOffset = jsonObject.getString("tzoffset");

            finalMap.put("address",address);
            finalMap.put("timezone",timezone);
            finalMap.put("tzOffset",tzOffset);

            //JSONObject days = jsonObject.getJSONObject("days");
            ArrayList<Map<String,Object>> daysArrayList = new ArrayList<>();
            JSONArray daysArray = jsonObject.getJSONArray("days");
            for(int j = 0 ; j < daysArray.length(); j++) {
                JSONObject days = (JSONObject) daysArray.get(j);
                Map<String, Object> dayMap = new HashMap<>();

                String dateTimeEpoch = days.getString("datetimeEpoch");
                String tempMax = (int)Math.ceil(days.getDouble("tempmax"))+(unit.equals("us") ? UNITF : UNITC);
                String tempMin = (int)Math.ceil(days.getDouble("tempmin"))+(unit.equals("us") ? UNITF : UNITC);
                String precipprob = days.getString("precipprob");
                String uvIndex = days.getString("uvindex");
                String description = days.getString("description");
                String icon = days.getString("icon");

                dayMap.put("dateTimeEpoch", dateTimeEpoch);
                dayMap.put("tempMax",tempMax);
                dayMap.put("tempMin",tempMin);
                dayMap.put("precipprob",precipprob);
                dayMap.put("uvIndex",uvIndex);
                dayMap.put("description",description);
                dayMap.put("icon",icon);

                //JSONObject hours = days.getJSONObject("hours");
                ArrayList<Map<String,String>> hourlyArrayList = new ArrayList<>();
                JSONArray hours = days.getJSONArray("hours");
                for(int i = 0; i < hours.length(); i++) {
                    JSONObject jHourly = (JSONObject) hours.get(i);
                    String hoursDatetimeEpoch = jHourly.getString("datetimeEpoch");
                    String temp = (int)Math.ceil(jHourly.getDouble("temp"))+(unit.equals("us") ? UNITF : UNITC);
                    String condition = jHourly.getString("conditions");
                    String hoursIcon = jHourly.getString("icon");
                    Map<String,String> map = new HashMap<>();
                    map.put("hoursDatetimeEpoch", hoursDatetimeEpoch);
                    map.put("temp", temp);
                    map.put("condition", condition);
                    map.put("hoursIcon", hoursIcon);
                    hourlyArrayList.add(map);
                }
                dayMap.put("hourlyArrayList",hourlyArrayList);
                daysArrayList.add(dayMap);
               // finalMap.put("dayMap"+j, dayMap);
            }
            finalMap.put("daysList",daysArrayList);
            JSONObject current = jsonObject.getJSONObject("currentConditions");
            Map<String,String> currentCondition = new HashMap<>();
            currentCondition.put("datetimeEpoch",  current.getString("datetimeEpoch"));
            currentCondition.put("temp",  (int)Math.ceil(current.getDouble("temp"))+(unit.equals("us") ? UNITF : UNITC));
            currentCondition.put("feelsLike",  (int)Math.ceil(current.getDouble("feelslike"))+(unit.equals("us") ? UNITF : UNITC));
            currentCondition.put("humidity",  (int)Math.ceil(current.getDouble("humidity"))+"");
            System.out.println(current.getString("windgust").equals("null"));
            if(current.getString("windgust")!=null && !current.getString("windgust").equals("null")) {
                System.out.println(current.getString("windgust"));
                currentCondition.put("windgust", (int) Math.ceil(current.getDouble("windgust")) + "");
            }
            else
                currentCondition.put("windgust",0+"");
            currentCondition.put("windspeed",  (int)Math.ceil(current.getDouble("windspeed"))+"");
            currentCondition.put("winddir",  current.getString("winddir"));
            currentCondition.put("visibility",  current.getString("visibility"));
            currentCondition.put("cloudcover",  (int)Math.ceil(current.getDouble("cloudcover"))+"");
            currentCondition.put("uvindex",  (int)Math.ceil(current.getDouble("uvindex"))+"");
            currentCondition.put("conditions",  current.getString("conditions"));
            currentCondition.put("icon",  current.getString("icon"));
            currentCondition.put("sunriseEpoch",  current.getString("sunriseEpoch"));
            currentCondition.put("sunsetEpoch",  current.getString("sunsetEpoch"));

            finalMap.put("currentCondition",currentCondition);

            mainActivity.updateDetails(finalMap);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
