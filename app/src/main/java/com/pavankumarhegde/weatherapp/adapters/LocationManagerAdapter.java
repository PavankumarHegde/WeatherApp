package com.pavankumarhegde.weatherapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.pavankumarhegde.weatherapp.LocationManagerActivity;
import com.pavankumarhegde.weatherapp.R;
import com.pavankumarhegde.weatherapp.model.Location;
import java.util.List;

public class LocationManagerAdapter extends RecyclerView.Adapter<LocationManagerAdapter.ViewHolder>{

    LocationManagerActivity context;
    List<Location> locList;

    public LocationManagerAdapter(LocationManagerActivity context, List<Location> locList) {
        this.locList = locList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!context.isContextualOpen)
                    Toast.makeText(context, "Press and hold to delete", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "Please select", Toast.LENGTH_SHORT).show();

            }
        });
        ViewHolder viewHolder = new ViewHolder(view,context);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Location loc = locList.get(position);
        String str[] = loc.getName().split(",");
        holder.tvCity.setText(str[0]);
        holder.tvDesc.setText(loc.getRegion()+" "+loc.getCountry());

        if(!context.isContextualOpen){
            holder.checkbox.setVisibility(View.GONE);
        }else{
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return locList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView tvCity,tvDesc;
        public CheckBox checkbox;
        View view;

        public ViewHolder(View itemView,LocationManagerActivity activity) {
            super(itemView);

            tvCity= itemView.findViewById(R.id.tvCity);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            checkbox = itemView.findViewById(R.id.checkbox);
            view = itemView;
            view.setOnLongClickListener(activity);
            checkbox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            context.MakeSelection(v,getAdapterPosition());
        }

    }

}
