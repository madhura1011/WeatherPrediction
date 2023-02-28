package com.madhura.visualcrossingweatherapp.RecyclerViewAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.madhura.visualcrossingweatherapp.R;
import com.madhura.visualcrossingweatherapp.WeekWeatherReport;

import java.util.*;

public class WeeklyRecylcerViewAdapter extends RecyclerView.Adapter<WeeklyRecylcerViewAdapter.ViewHolder> {
    WeekWeatherReport weekWeatherReport;
    List<Map<String, String>> daysList;

    public WeeklyRecylcerViewAdapter(WeekWeatherReport weekWeatherReport, List<Map<String, String>> daysList) {
        this.weekWeatherReport = weekWeatherReport;
        this.daysList = daysList;
    }

    @NonNull
    @Override
    public WeeklyRecylcerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weekly_weather_report_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeeklyRecylcerViewAdapter.ViewHolder holder, int position) {
        holder.dayDateInfo.setText(daysList.get(position).get("dayDate"));
        holder.minimumMaximumTemp.setText(daysList.get(position).get("Temp"));
        holder.weekTempDescription.setText(daysList.get(position).get("description"));
        holder.precip.setText(daysList.get(position).get("precipprob"));
        holder.weekUVIndex.setText(daysList.get(position).get("uvIndex"));
        holder.weekMorningTemp.setText(daysList.get(position).get("morning"));
        holder.weekAfternoonTemp.setText(daysList.get(position).get("afternoon"));
        holder.weekEveningTemp.setText(daysList.get(position).get("evening"));
        holder.weekNightTemp.setText(daysList.get(position).get("night"));
        String icon = daysList.get(position).get("icon");
        icon = icon.replace("-", "_");
        int iconResId = weekWeatherReport.getResources().getIdentifier(icon,
                "drawable", weekWeatherReport.getPackageName());
        holder.imageView.setImageResource(iconResId);
    }

    @Override
    public int getItemCount() {
        return daysList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayDateInfo,minimumMaximumTemp,weekTempDescription,precip, weekUVIndex, weekMorningTemp, weekAfternoonTemp,weekEveningTemp,weekNightTemp;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayDateInfo = itemView.findViewById(R.id.day);
            minimumMaximumTemp = itemView.findViewById(R.id.minMax);
            weekTempDescription = itemView.findViewById(R.id.descriptionWeek);
            precip = itemView.findViewById(R.id.precip);
            weekUVIndex = itemView.findViewById(R.id.UVIndex);
            weekMorningTemp = itemView.findViewById(R.id.morn);
            weekAfternoonTemp = itemView.findViewById(R.id.after);
            weekEveningTemp = itemView.findViewById(R.id.even);
            weekNightTemp = itemView.findViewById(R.id.nightWeek);
            imageView = itemView.findViewById(R.id.imageView);

        }
    }
}
