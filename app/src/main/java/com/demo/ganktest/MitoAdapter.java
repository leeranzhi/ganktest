package com.demo.ganktest;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.demo.ganktest.db.Mito;

import java.util.List;

public class MitoAdapter extends RecyclerView.Adapter<MitoAdapter.ViewHolder> {
    private Context mContext;
    private List<Mito> mMitoList;
    private String TAG = "MitoAdapter";

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView mitoImage;
        TextView mitoName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            mitoImage = (ImageView) view.findViewById(R.id.mito_image);
            mitoName = (TextView) view.findViewById(R.id.mito_name);
        }
    }

    public MitoAdapter(List<Mito> mitoList) {
        mMitoList = mitoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "创建视图中....");
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.mito_item,
                parent, false);
        final ViewHolder holder=new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Mito mito=mMitoList.get(position);
                Intent intent=new Intent(mContext,MitoActivity.class);
                intent.putExtra(MitoActivity.MITO_NAME,mito.getName());
                intent.putExtra(MitoActivity.MITO_IMAGE_URL,mito.getImageUrl());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Mito mito = mMitoList.get(position);
        if (mito != null) {
            holder.mitoName.setText(mito.getName());
            Glide.with(mContext).load(mito.getImageUrl()).into(holder.mitoImage);
        }
    }

    @Override
    public int getItemCount() {
        return mMitoList.size();
    }
}
