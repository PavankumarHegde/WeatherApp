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
import com.pavankumarhegde.weatherapp.model.Hour;
import com.squareup.picasso.Picasso;
import java.util.List;

public class HourlyListAdapter extends RecyclerView.Adapter <HourlyListAdapter.ViewHolder>{

    private List<Hour> hours;
    SharedPreferences settings;
    String UNIT;
    Context context;

    public HourlyListAdapter(Context context, List<Hour> hours) {
        super();
        this.context = context;
        this.hours = hours;
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        UNIT = settings.getString("UNIT","C°");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hour_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        if(UNIT.equals("C°"))
            viewHolder.tvTemp.setText((int) hours.get(i).getTemp_c()+"°");
        else if(UNIT.equals("F°"))
            viewHolder.tvTemp.setText((int) hours.get(i).getTemp_f()+"°");

        viewHolder.tvHum.setText(hours.get(i).getHumidity()+"%");

        String time[] = hours.get(i).getTime().split(" ");
        viewHolder.tvTime.setText(time[1]);

        Picasso.get().load("http:"+hours.get(i).getCondition().getIcon()).into(viewHolder.img);

    }

    @Override
    public int getItemCount() {
        return hours.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView img;
        TextView tvTime,tvTemp,tvHum;

        ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgHour);
            tvTime = itemView.findViewById(R.id.tvHTime);
            tvTemp = itemView.findViewById(R.id.tvHTemp);
            tvHum = itemView.findViewById(R.id.tvHHum);

        }

        @Override
        public void onClick(View view) { }

        @Override
        public boolean onLongClick(View view) { return true; }

    }

}
