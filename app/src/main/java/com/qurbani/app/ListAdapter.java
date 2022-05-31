package com.qurbani.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ItemModel> list;

    public ListAdapter(Context context, ArrayList<ItemModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvPersonName.setText(list.get(position).getName());
        holder.tvPersonContNo.setText(list.get(position).getContactNo());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList(ArrayList<ItemModel> searchList) {
        list = searchList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPersonContNo, tvPersonName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPersonName = itemView.findViewById(R.id.tvPersonName);
            tvPersonContNo = itemView.findViewById(R.id.tvPersonContNo);
        }
    }
}
