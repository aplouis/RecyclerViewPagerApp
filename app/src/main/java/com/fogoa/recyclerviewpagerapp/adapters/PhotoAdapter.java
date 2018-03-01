package com.fogoa.recyclerviewpagerapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fogoa.recyclerviewpagerapp.R;
import com.fogoa.recyclerviewpagerapp.extensions.BaseApplication;
import com.fogoa.recyclerviewpagerapp.listeners.OnLoadMore;
import com.fogoa.recyclerviewpagerapp.listeners.OnPhotoSwitch;
import com.fogoa.recyclerviewpagerapp.misc.ImageDownloaderCache;
import com.fogoa.recyclerviewpagerapp.models.GalleryItem;

import java.util.ArrayList;


public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private static final String TAG = GalleryAdapter.class.getSimpleName();
    private final Context context;
    private final ArrayList<GalleryItem> rvItemList;
    private ImageDownloaderCache imgDownloader;
    private final OnLoadMore mLoadMoreListener;
    private final OnPhotoSwitch mPhotoSwichListener;

    public PhotoAdapter(Context context, ArrayList<GalleryItem> items, ImageDownloaderCache imgDownloaderCache, OnLoadMore listener, OnPhotoSwitch listener2) {
        this.context = context;
        this.rvItemList = items;
        imgDownloader = imgDownloaderCache;
        mLoadMoreListener = listener;
        mPhotoSwichListener = listener2;
    }

    @Override
    public int getItemCount() {
        return rvItemList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = rvItemList.get(position);

        holder.ibNextLeft.setVisibility(View.VISIBLE);
        holder.ibNextRight.setVisibility(View.VISIBLE);
        if (position==0) {
            holder.ibNextLeft.setVisibility(View.INVISIBLE);
        }
        else {
            holder.ibNextLeft.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //if (position!=0) {
                    //    mPhotoSwichListener.onPhotoSwitch(position-1);
                    //}
                    if (holder.getAdapterPosition()!=0) {
                        mPhotoSwichListener.onPhotoSwitch(holder.getAdapterPosition()-1);
                    }
                }
            });
        }
        if (position==getItemCount() - 1) {
            holder.ibNextRight.setVisibility(View.INVISIBLE);
        }
        else {
            holder.ibNextRight.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //if (position < getItemCount() - 1) {
                    //    mPhotoSwichListener.onPhotoSwitch(position+1);
                    //}
                    if (holder.getAdapterPosition() < getItemCount() - 1) {
                        mPhotoSwichListener.onPhotoSwitch(holder.getAdapterPosition()+1);
                    }
                }
            });
        }

        holder.ivPhoto.setImageBitmap(null);
        holder.ivPhoto.setImageDrawable(null);
        holder.ivPhoto.setVisibility(View.VISIBLE);
        imgDownloader.loadBitmapPath(holder.mItem.img_uri, holder.ivPhoto, ImageDownloaderCache.maxImageSize);
        //imgDownloader.loadBitmapPath(holder.mItem.img_uri, holder.ivPhoto);

        //holder.ivPhoto.setImageBitmap(ImageDownloaderCache.decodeSampledBitmapFromPath(holder.mItem.img_uri, ImageDownloaderCache.maxImageSize, ImageDownloaderCache.maxImageSize, false));


        if (holder.mItem.name.isEmpty()) {
            holder.tvCaption.setVisibility(View.GONE);
        }
        else {
            holder.tvCaption.setVisibility(View.VISIBLE);
            //holder.tvCaption.setText(holder.mItem.name + " - " + holder.mItem.img_uri);
            holder.tvCaption.setText(holder.mItem.name );
        }

        //check for last item and load more if displayed
        if ((position >= getItemCount() - 1)) {
            mLoadMoreListener.onLoadMore();
        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_photo_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public GalleryItem mItem;
        public final View mView;
        public final ImageView ivPhoto;
        public final TextView tvCaption;
        public final ImageButton ibNextLeft;
        public final ImageButton ibNextRight;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ivPhoto = (ImageView) view.findViewById(R.id.ivPhoto);
            tvCaption= (TextView) view.findViewById(R.id.tvCaption);
            ibNextLeft = (ImageButton) view.findViewById(R.id.ibNextLeft);
            ibNextRight = (ImageButton) view.findViewById(R.id.ibNextRight);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItem.toString() + "'";
        }
    }

    public AppCompatActivity getActivity() {
        return (AppCompatActivity)context;
    }


}
