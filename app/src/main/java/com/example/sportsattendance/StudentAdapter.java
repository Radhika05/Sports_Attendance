package com.example.sportsattendance;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {


    ArrayList<StudentItem> studentItems;
    Context context;
    private OnItemClickListener onItemClickListener;

    public void filterList(ArrayList<StudentItem> filtered) {
        studentItems = filtered;
        notifyDataSetChanged();
    }


    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public StudentAdapter(Context context, ArrayList<StudentItem> studentItems) {
        this.studentItems = studentItems;
        this.context = context;
    }


    public static class StudentViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView roll;
        TextView name;
        TextView status;
        CardView cardView;

        public StudentViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            roll = itemView.findViewById(R.id.roll);
            name = itemView.findViewById(R.id.name);
            status = itemView.findViewById(R.id.status);
            cardView = itemView.findViewById(R.id.cardview);
            itemView.setOnClickListener(v -> onItemClickListener.onClick(getAdapterPosition()));
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            contextMenu.add(getAdapterPosition(), 0, 0, "EDIT");
            contextMenu.add(getAdapterPosition(), 1, 0, "DELETE");
        }
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_item, parent, false);
        return new StudentViewHolder(itemView, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        holder.roll.setText(Integer.toString(studentItems.get(position).getRoll()));
        holder.name.setText(studentItems.get(position).getName());
        holder.status.setText(studentItems.get(position).getStatus());
        if (!studentItems.get(position).getStatus().isEmpty() && studentItems.get(position).getStatus() != null) {
            holder.cardView.setCardBackgroundColor(getColor(position));
        } else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.normal))));
        }

    }

    private int getColor(int position) {
        String status = studentItems.get(position).getStatus();
        if (status.equals("P")) {
            return Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.present)));
        } else if (status.equals("A")) {
            return Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.abs)));
        } else if (status.equals("L")) {
           return Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.leave)));
        }

        return Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.normal)));

    }

    @Override
    public int getItemCount() {
        return studentItems.size();
    }
}
