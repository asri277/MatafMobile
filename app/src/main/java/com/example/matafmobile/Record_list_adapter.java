package com.example.matafmobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Record_list_adapter extends RecyclerView.Adapter<Record_list_adapter.MyViewHolder> {

    private ArrayList<Record_list> recordList;

    public Record_list_adapter(ArrayList<Record_list> recordList){
        this.recordList = recordList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView number, staffID, staffName, location, date, time;

        public MyViewHolder(final View view){
            super(view);
            number = view.findViewById(R.id.num_recycle);
            staffID = view.findViewById(R.id.ID_recycle);
            staffName = view.findViewById(R.id.name_recycle);
            location = view.findViewById(R.id.location_recycle);
            date = view.findViewById(R.id.date_recycle);
            time = view.findViewById(R.id.time_recycle);
        }
    }

    @NonNull
    @Override
    public Record_list_adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View recordView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_record, parent, false);
        return new MyViewHolder(recordView);
    }

    @Override
    public void onBindViewHolder(@NonNull Record_list_adapter.MyViewHolder holder, int position) {
        String sNumber = recordList.get(position).getNumber();
        String sID = recordList.get(position).getStaffID();
        String sName = recordList.get(position).getStaffName();
        String sLocation = recordList.get(position).getLocation();
        String sDate = recordList.get(position).getDate();
        String sTime = recordList.get(position).getTime();

        holder.number.setText(sNumber);
        holder.staffID.setText(sID);
        holder.staffName.setText(sName);
        holder.location.setText(sLocation);
        holder.date.setText(sDate);
        holder.time.setText(sTime);

    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public void filterList(ArrayList<Record_list> filteredList) {
        recordList = filteredList;
        notifyDataSetChanged();
    }

}
