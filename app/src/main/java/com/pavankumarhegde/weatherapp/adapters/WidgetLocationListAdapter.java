package com.pavankumarhegde.weatherapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.pavankumarhegde.weatherapp.AppWidgetConfigureActivity;
import com.pavankumarhegde.weatherapp.R;
import com.pavankumarhegde.weatherapp.model.Location;

import java.util.List;

public class WidgetLocationListAdapter extends RecyclerView.Adapter<WidgetLocationListAdapter.ViewHolder>{
    AppWidgetConfigureActivity context;
    List<Location> locList;

    public WidgetLocationListAdapter(AppWidgetConfigureActivity context, List<Location> locList) {
        this.locList = locList;
        this.context = context;
    }

    @Override
    public WidgetLocationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false);
        WidgetLocationListAdapter.ViewHolder viewHolder = new WidgetLocationListAdapter.ViewHolder(view,context);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WidgetLocationListAdapter.ViewHolder holder, int position) {
        Location loc = locList.get(position);
        String str[] = loc.getName().split(",");
        holder.tvCity.setText(str[0]);
        holder.tvDesc.setText(loc.getRegion()+" "+loc.getCountry());
        holder.checkbox.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return locList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView tvCity,tvDesc;
        public CheckBox checkbox;
        View view;

        public ViewHolder(View itemView, AppWidgetConfigureActivity activity) {
            super(itemView);
            tvCity= itemView.findViewById(R.id.tvCity);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            checkbox = itemView.findViewById(R.id.checkbox);
            view = itemView;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            context.MakeSelection(v,getAdapterPosition());
        }
    }

}
