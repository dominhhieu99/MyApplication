package com.example.simpleapplocker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class ListofAppAdapter extends RecyclerView.Adapter<ListofAppAdapter.MyViewHolder> {

    private List<Model> appList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView app_name;
        public ImageView app_icon,status;

        public MyViewHolder(View view) {
            super(view);
            app_icon = (ImageView) view.findViewById(R.id.app_icon);
            app_name = (TextView) view.findViewById(R.id.list_app_name);
            status = (ImageView) view.findViewById(R.id.lock_icon);
        }
    }


    public ListofAppAdapter(List<Model> appList) {
        this.appList = appList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.installed_app_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Model model = appList.get(position);
        holder.app_icon.setImageDrawable(model.getIcon());
        holder.app_name.setText(model.getName());
        holder.status.setImageResource(model.getLocked());
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }
}