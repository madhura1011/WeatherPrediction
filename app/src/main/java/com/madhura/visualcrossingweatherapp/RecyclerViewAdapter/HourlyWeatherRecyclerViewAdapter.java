package com.madhura.visualcrossingweatherapp.RecyclerViewAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.madhura.visualcrossingweatherapp.MainActivity;
import com.madhura.visualcrossingweatherapp.R;

import java.util.ArrayList;
import java.util.Map;

public class HourlyWeatherRecyclerViewAdapter extends RecyclerView.Adapter<HourlyWeatherRecyclerViewAdapter.ViewHolder> {
    MainActivity mainActivity;
    ArrayList<Map<String, String>> daysList = new ArrayList<>();

    public HourlyWeatherRecyclerViewAdapter(MainActivity mainActivity, ArrayList<Map<String, String>> daysList) {
        this.mainActivity = mainActivity;
        this.daysList = daysList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hourly_weather_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String icon = daysList.get(position).get("icon");
        icon = icon.replace("-", "_");
        int iconResId = mainActivity.getResources().getIdentifier(icon,
                "drawable", mainActivity.getPackageName());
        holder.dayInfo.setText(daysList.get(position).get("Day"));
        holder.timeInfo.setText(daysList.get(position).get("time"));
        holder.hourlyTemperatureInfo.setText(daysList.get(position).get("temp"));
        holder.descriptionTemp.setText(daysList.get(position).get("Description"));
        holder.weatherIcon.setImageResource(iconResId);
    }


    @Override
    public int getItemCount() {
        return daysList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayInfo, timeInfo,hourlyTemperatureInfo, descriptionTemp;
        ImageView weatherIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayInfo = itemView.findViewById(R.id.hrDay);
            timeInfo = itemView.findViewById(R.id.time);
            hourlyTemperatureInfo = itemView.findViewById(R.id.hrTemp);
            descriptionTemp = itemView.findViewById(R.id.desc);
            weatherIcon = itemView.findViewById(R.id.icon);

        }
    }
}
