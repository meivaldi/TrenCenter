package com.meivaldi.trencenter.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.meivaldi.trencenter.R;
import com.meivaldi.trencenter.helper.CircleTransform;
import com.meivaldi.trencenter.model.LogisticReport;
import com.meivaldi.trencenter.model.Logistik;

import java.util.List;

/**
 * Created by root on 29/09/18.
 */

public class LogisticReportAdapter extends ArrayAdapter<LogisticReport> {

    private Context context;
    private List<LogisticReport> list;


    public LogisticReportAdapter(Context context, List<LogisticReport> list) {
        super(context, 0, list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.logistic_report,parent,false);

        LogisticReport currentLogistik = list.get(position);

        TextView title = (TextView) listItem.findViewById(R.id.title);
        title.setText(currentLogistik.getName());

        TextView total = (TextView) listItem.findViewById(R.id.total);
        total.setText(currentLogistik.getTotal());

        ImageView imageView = (ImageView) listItem.findViewById(R.id.logo);
        String url = "http://156.67.221.225/trencenter/voting/dashboard/save/foto_logistik/" + currentLogistik.getImage();

        Glide.with(getContext()).load(url)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(getContext()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .override(512, 160)
                .into(imageView);

        return listItem;
    }
}
