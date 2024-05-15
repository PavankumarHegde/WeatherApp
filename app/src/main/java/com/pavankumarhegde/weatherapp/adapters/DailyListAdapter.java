package com.pavankumarhegde.weatherapp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import com.pavankumarhegde.weatherapp.R;
import com.pavankumarhegde.weatherapp.model.ForecastDay;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DailyListAdapter extends RecyclerView.Adapter <DailyListAdapter.ViewHolder>{

    private List<ForecastDay> forecastDay;
    SharedPreferences settings;
    String UNIT;
    Context context;

    public DailyListAdapter(Context context, List<ForecastDay> forecastDay) {
        super();
        this.context = context;
        this.forecastDay = forecastDay;
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        UNIT = settings.getString("UNIT","C°");
    }

    @Override
    public DailyListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.day_item, viewGroup, false);
        return new DailyListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DailyListAdapter.ViewHolder viewHolder, int i) {

        if(UNIT.equals("C°")) {
            viewHolder.tvTemp.setText((int) forecastDay.get(i).getDay().getAvgtemp_c()+"°");
            viewHolder.tvMinMax.setText((int)forecastDay.get(i).getDay().getMintemp_c()+"°/"+(int)forecastDay.get(i).getDay().getMaxtemp_c()+"°");
        }else if(UNIT.equals("F°")){
            viewHolder.tvTemp.setText((int) forecastDay.get(i).getDay().getAvgtemp_f()+"°");
            viewHolder.tvMinMax.setText((int)forecastDay.get(i).getDay().getMintemp_f()+"°/"+(int)forecastDay.get(i).getDay().getMaxtemp_f()+"°");
        }

        viewHolder.tvCondition.setText(forecastDay.get(i).getDay().getCondition().getText());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = format.parse(forecastDay.get(i).getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat("EEE");
        viewHolder.tvDay.setText(format.format(newDate));

        Picasso.get().load("http:"+forecastDay.get(i).getDay().getCondition().getIcon()).into(viewHolder.imgDay);

    }

    @Override
    public int getItemCount() {
        return forecastDay.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imgDay;
        TextView tvDay,tvTemp,tvMinMax,tvCondition;

        ViewHolder(View itemView) {
            super(itemView);
            imgDay = itemView.findViewById(R.id.imgDay);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvCondition = itemView.findViewById(R.id.tvDayCondition);
            tvTemp = itemView.findViewById(R.id.tvDayTemp);
            tvMinMax = itemView.findViewById(R.id.tvDayMinMax);
        }

        @Override
        public void onClick(View view) { }

        @Override
        public boolean onLongClick(View view) {
            return true;
        }

    }
}
