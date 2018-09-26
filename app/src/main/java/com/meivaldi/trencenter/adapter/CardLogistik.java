package com.meivaldi.trencenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.meivaldi.trencenter.R;
import com.meivaldi.trencenter.activity.DetailLogistik;
import com.meivaldi.trencenter.activity.DetailProgram;
import com.meivaldi.trencenter.activity.tim_pemenangan.DetailProgram_TimPemenangan;
import com.meivaldi.trencenter.model.Card;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Created by root on 05/09/18.
 */

public class CardLogistik extends RecyclerView.Adapter<CardLogistik.MyViewHolder> {

    private Context context;
    private List<Card> cardList;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.program_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Card card = cardList.get(position);
        holder.title.setText(card.getTitle());
        holder.date.setText("Tanggal Mulai: " + card.getDate());
        String imageUrl = card.getImage();

        Glide.with(context).load(imageUrl).into(holder.cardImage);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title, date, image;
        public ImageView cardImage;
        public View view;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
            date = (TextView) itemView.findViewById(R.id.date);
            cardImage = (ImageView) itemView.findViewById(R.id.thumbnail);

            cardImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailLogistik.class);
                    intent.putExtra("INDEX", getAdapterPosition());
                    intent.putExtra("MAIN", true);
                    context.startActivity(intent);
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailLogistik.class);
                    intent.putExtra("INDEX", getAdapterPosition());
                    intent.putExtra("MAIN", true);
                    context.startActivity(intent);
                }
            });
        }

    }

    public CardLogistik(Context context, List<Card> cardList){
        this.context = context;
        this.cardList = cardList;
    }

}
