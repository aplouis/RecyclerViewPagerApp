package com.fogoa.recyclerviewpagerapp.adapters;

import android.content.Context;
import android.graphics.Rect;
import android.support.transition.Explode;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fogoa.recyclerviewpagerapp.R;
import com.fogoa.recyclerviewpagerapp.listeners.OnLoadMore;
import com.fogoa.recyclerviewpagerapp.listeners.OnPhotoSelected;
import com.fogoa.recyclerviewpagerapp.misc.Constants;
import com.fogoa.recyclerviewpagerapp.misc.ImageDownloaderCache;
import com.fogoa.recyclerviewpagerapp.models.GalleryItem;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private static final String TAG = GalleryAdapter.class.getSimpleName();
    private final Context context;
    private final ArrayList<GalleryItem> rvItemList;
    private ImageDownloaderCache imgDownloader;
    private final OnLoadMore mLoadMoreListener;
    private final OnPhotoSelected mPhotoSelectedListener;

    public GalleryAdapter(Context context, ArrayList<GalleryItem> items, ImageDownloaderCache imgDownloaderCache, OnLoadMore listener, OnPhotoSelected listener2) {
        this.context = context;
        this.rvItemList = items;
        imgDownloader = imgDownloaderCache;
        mLoadMoreListener = listener;
        mPhotoSelectedListener= listener2;
    }

    @Override
    public int getItemCount() {
        return rvItemList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = rvItemList.get(position);

        if (holder.mItem.img_uri != null && !holder.mItem.img_uri.isEmpty()) {

            int valueInPx = context.getResources().getDimensionPixelSize(R.dimen.gallery_image_width);
            float valueInDp = context.getResources().getDimension(R.dimen.gallery_image_width);
            if (Constants.DEBUG) Log.d(TAG, "valueInDp: "+ valueInDp+ "  valueInPx: "+valueInPx);

            holder.ivGalleryItem.setImageResource(R.drawable.ic_photo_black_48dp);
            //imgDownloader.loadBitmapPath(holder.mItem.img_uri, holder.ivGalleryItem, valueInPx);
            imgDownloader.loadBitmapPath(holder.mItem.img_uri, holder.ivGalleryItem, ImageDownloaderCache.maxImageSize);
            //imgDownloader.loadBitmapPath(holder.mItem.img_uri, holder.ivGalleryItem);
            //holder.ivGalleryItem.setImageBitmap(ImageDownloaderCache.decodeSampledBitmapFromPath(holder.mItem.img_uri, valueInPx, valueInPx, false));

            holder.ivGalleryItem.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mPhotoSelectedListener!=null) {
                        mPhotoSelectedListener.onPhotoSelected(position, v);
                    }
                }
            });

        }

        //check for last item and load more if displayed
        if ((position >= getItemCount() - 1)) {
            mLoadMoreListener.onLoadMore();
        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_gallery_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public GalleryItem mItem;
        public final View mView;
        public final ImageView ivGalleryItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ivGalleryItem = (ImageView) view.findViewById(R.id.ivGalleryItem);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItem.toString() + "'";
        }
    }

    public AppCompatActivity getActivity() {
        return (AppCompatActivity)context;
    }

    /*
    private View.OnClickListener ivGalleryItem_OnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int pos = (Integer)v.getTag();
            //ProfileGalleryItem item = rvItemList.get(pos);
            if (mPhotoSelectedListener!=null) {
                mPhotoSelectedListener.onPhotoSelected(pos, v);
            }
        }
    };
    */


}
