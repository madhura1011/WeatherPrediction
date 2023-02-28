package com.madhura.visualcrossingweatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.madhura.visualcrossingweatherapp.RecyclerViewAdapter.HourlyWeatherRecyclerViewAdapter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView currentDate,temperatureInfo,feelsLikeTemp,weatherDescription,WindInfo,humidityInfo,uvIndexInfo,visibilityInfo;
    TextView morningTemp,afternoonTemp,eveningTemp,nightTemp,sunriseInfo,sunsetInfo;
    RecyclerView hourlyRecyclerView;
    ImageView imageView;
    SwipeRefreshLayout swipeRefreshLayout;
    HourlyWeatherRecyclerViewAdapter hourlyWeatherRecyclerViewAdapter;

    SimpleDateFormat fullDate = new SimpleDateFormat("EEE MMM dd h:mm a, yyyy", Locale.getDefault());
    SimpleDateFormat timeOnly = new SimpleDateFormat("h:mm a", Locale.getDefault());
    SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
    SimpleDateFormat hourOnly = new SimpleDateFormat("h a", Locale.getDefault());
    String location = "Chicago, Illinois";
    String unitType = "us";

    Map<String, Object> daysData = new HashMap<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        if(!checkInternet()){
            setContentView(R.layout.no_internet_connection);
        }else {
            setContentView(R.layout.activity_main);
            setID();
            getSavedData();
            FetchData.fetchData(this, location, unitType);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options,menu);
        return true;
    }

    public void saveData(){
        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences = getSharedPreferences("PreviousData", MODE_APPEND);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("location", location);
        myEdit.putString("unitType", unitType);
        myEdit.apply();
    }

    public void getSavedData(){
        SharedPreferences sharedPreferences = getSharedPreferences("PreviousData", MODE_PRIVATE);
        location = sharedPreferences.getString("location", "Chicago, Illinois");
        unitType = sharedPreferences.getString("unitType", "us");
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.unit:
                if(checkInternet()){
                    if(unitType.equals("us")){
                        unitType = "metric";
                        item.setIcon(R.drawable.units_c);
                    }else{
                        unitType = "us";
                        item.setIcon(R.drawable.units_f);
                    }
                    saveData();
                    FetchData.fetchData(this, location, unitType);
                    return true;
                }else{
                    Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                    return true;
                }

            case R.id.calender:
                if(checkInternet()){
                    Intent intent = new Intent(this, WeekWeatherReport.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("DayList", (Serializable) daysData);
                    intent.putExtra("DayList", bundle);
                    startActivity(intent);
                    return true;
                }else{
                    Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                    return true;
                }

            case R.id.location:
                if(checkInternet()){
                    final EditText txtUrl = new EditText(this);
                    new AlertDialog.Builder(this)
                            .setTitle("Enter a Location")
                            .setMessage("For US locations, enter as 'City',or 'City,State'.\n"+"For international locations enter as 'City,Country'\n")
                            .setView(txtUrl)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    location = txtUrl.getText().toString();
                                    saveData();
                                    DataFetch();
                                }
                            })
                            .setNegativeButton("No",null)
                            .show();
                    return true;
                }
                else{
                    Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void DataFetch(){
        FetchData.fetchData(this, location, unitType);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkInternet(){
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }
    private String getDirection(double degrees) { if (degrees >= 337.5 || degrees < 22.5)
        return "N";
        if (degrees >= 22.5 && degrees < 67.5)
            return "NE";
        if (degrees >= 67.5 && degrees < 112.5)
            return "E";
        if (degrees >= 112.5 && degrees < 157.5)
            return "SE";
        if (degrees >= 157.5 && degrees < 202.5)
            return "S";
        if (degrees >= 202.5 && degrees < 247.5)
            return "SW";
        if (degrees >= 247.5 && degrees < 292.5)
            return "W";
        if (degrees >= 292.5 && degrees < 337.5)
            return "NW";
        return "X"; // We'll use 'X' as the default if we get a bad value
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateDetails(Map<String, Object> o) {

        Map<String, Object> map = o;
        Map<String,String> currentCondition = (Map<String, String>) map.get("currentCondition");
        String currCondition = currentCondition.get("datetimeEpoch");

        setTitle((CharSequence) map.get("address"));

        currentDate.setText(TimeConversion(currCondition, fullDate));
        imageView.setImageResource(getIcon(currentCondition.get("icon")));
        temperatureInfo.setText(currentCondition.get("temp"));
        feelsLikeTemp.setText("Feels Like " + currentCondition.get("feelsLike"));
        weatherDescription.setText(formatString(currentCondition.get("conditions")+ " ( "+ currentCondition.get("cloudcover") +"% clouds)"));
        WindInfo.setText("Winds: "+getDirection(Double.parseDouble(currentCondition.get("winddir")))+ " at "+ currentCondition.get("windspeed") + " mph " + "gusting to "+currentCondition.get("windgust")+ " mph");
        humidityInfo.setText("Humidity: "+ currentCondition.get("humidity")+"%");
        uvIndexInfo.setText("UV Index: "+ currentCondition.get("uvindex"));
        visibilityInfo.setText("Visibility: "+ currentCondition.get("visibility")+" mi");
        ArrayList<Map<String, Object>> daysList = (ArrayList<Map<String, Object>>) map.get("daysList");
        ArrayList<Map<String,String>> hourlyArrayList = (ArrayList<Map<String, String>>) daysList.get(0).get("hourlyArrayList");
        morningTemp.setText(hourlyArrayList.get(8).get("temp"));
        afternoonTemp.setText(hourlyArrayList.get(13).get("temp"));
        eveningTemp.setText(hourlyArrayList.get(17).get("temp"));
        nightTemp.setText(hourlyArrayList.get(23).get("temp"));
        sunriseInfo.setText("Sunrise: "+TimeConversion(currentCondition.get("sunriseEpoch"), timeOnly));
        sunsetInfo.setText("Sunset: "+TimeConversion(currentCondition.get("sunsetEpoch"), timeOnly));
        daysData = o;
        System.out.println(currCondition);

        ArrayList<Map<String, String>> hourlyRecyclerData = new ArrayList<>();
        boolean check = false;
        for(int i = 0; i < daysList.size(); i++){
            if(hourlyRecyclerData.size() == 48)
                break;

            ArrayList<Map<String,String>>  days = (ArrayList<Map<String,String>>) daysList.get(i).get("hourlyArrayList");
            System.out.println(days.size());
            System.out.println(days);

            for(int j = 0; j < days.size(); j++) {
                Map<String, String> temp = new HashMap<>();
                if(check) {
                    if (i == 0) {
                        temp.put("Day", "Today");
                    } else {
                        temp.put("Day", TimeConversion(days.get(j).get("hoursDatetimeEpoch"), dayFormat));
                    }
                    temp.put("time", TimeConversion(days.get(j).get("hoursDatetimeEpoch"), timeOnly));
                    temp.put("icon", days.get(j).get("hoursIcon"));
                    temp.put("temp", days.get(j).get("temp"));
                    temp.put("Description", days.get(j).get("condition"));
                    hourlyRecyclerData.add(temp);
                    if (hourlyRecyclerData.size() == 48)
                        break;
                }
                if(TimeConversion(currCondition, hourOnly).equals(TimeConversion(days.get(j).get("hoursDatetimeEpoch"), hourOnly))){
                    check = true;
                }

            }
        }
        hourlyWeatherRecyclerViewAdapter = new HourlyWeatherRecyclerViewAdapter(this, hourlyRecyclerData);
        hourlyRecyclerView.setAdapter(hourlyWeatherRecyclerViewAdapter);
        hourlyRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


    }

    public String formatString(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String TimeConversion(String str, SimpleDateFormat date){

        Date dateTime = new Date(Long.parseLong(str) * 1000);

        return date.format(dateTime);
    }
    public int getIcon(String icon){
        System.out.println(icon);
        icon = icon.replace("-", "_");
        System.out.println(icon);
        int iconID =
                this.getResources().getIdentifier(icon, "drawable", this.getPackageName());
        if (iconID == 0) {
            Log.d("Main", "parseCurrentRecord: CANNOT FIND ICON " + icon);
        }
        System.out.println(iconID);
        return iconID;
    }

    public void setID() {
        currentDate = findViewById(R.id.date);
        temperatureInfo = findViewById(R.id.temperature);
        feelsLikeTemp = findViewById(R.id.feels);
        weatherDescription = findViewById(R.id.description);
        WindInfo = findViewById(R.id.wind);
        humidityInfo = findViewById(R.id.humidity);
        uvIndexInfo = findViewById(R.id.uvIndex);
        visibilityInfo = findViewById(R.id.visibility);
        morningTemp = findViewById(R.id.morning);
        afternoonTemp = findViewById(R.id.afternoon);
        eveningTemp = findViewById(R.id.evening);
        nightTemp = findViewById(R.id.nightT);
        sunriseInfo = findViewById(R.id.sunrise);
        sunsetInfo = findViewById(R.id.sunset);
        hourlyRecyclerView = findViewById(R.id.hourlyRecyclerView);
        imageView = findViewById(R.id.icon);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
    }
}