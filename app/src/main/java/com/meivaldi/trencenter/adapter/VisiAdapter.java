package com.meivaldi.trencenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meivaldi.trencenter.R;
import com.meivaldi.trencenter.model.VisiMisiModel;

import java.util.List;

/**
 * Created by root on 11/10/18.
 */

public class VisiAdapter extends RecyclerView.Adapter<VisiAdapter.MyViewHolder> {

    private Context context;
    private List<VisiMisiModel> VisiMisiList;

    public VisiAdapter(Context context, List<VisiMisiModel> visiMisiList) {
        this.context = context;
        VisiMisiList = visiMisiList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView value;
        public LinearLayout background, foreground;

        public MyViewHolder(View view) {
            super(view);
            value = (TextView) view.findViewById(R.id.text);
            background = (LinearLayout) view.findViewById(R.id.view_background);
            foreground = (LinearLayout) view.findViewById(R.id.view_foreground);
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.visi_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        VisiMisiModel model = VisiMisiList.get(position);
        holder.value.setText(model.getValue());
    }

    @Override
    public int getItemCount() {
        return VisiMisiList.size();
    }

}
