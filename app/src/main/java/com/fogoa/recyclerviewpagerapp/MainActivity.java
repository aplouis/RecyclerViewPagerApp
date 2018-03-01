package com.fogoa.recyclerviewpagerapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.transition.Explode;
import android.support.transition.Fade;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;

import com.fogoa.recyclerviewpagerapp.adapters.GalleryAdapter;
import com.fogoa.recyclerviewpagerapp.adapters.PhotoAdapter;
import com.fogoa.recyclerviewpagerapp.extensions.BaseActivity;
import com.fogoa.recyclerviewpagerapp.extensions.GridSpacingItemDecoration;
import com.fogoa.recyclerviewpagerapp.listeners.OnLoadMore;
import com.fogoa.recyclerviewpagerapp.listeners.OnPhotoSelected;
import com.fogoa.recyclerviewpagerapp.listeners.OnPhotoSwitch;
import com.fogoa.recyclerviewpagerapp.misc.Constants;
import com.fogoa.recyclerviewpagerapp.misc.ImageDownloaderCache;
import com.fogoa.recyclerviewpagerapp.models.GalleryItem;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    FloatingActionButton fab;
    RecyclerView rvGallery;
    private RecyclerView.Adapter rvGalleryAdapter;
    private RecyclerView.LayoutManager rvGalleryLayoutManager;
    private LinearLayoutManager rvGalleryLinearLayoutManager;
    private ArrayList<GalleryItem> adapterList = new ArrayList<GalleryItem>();

    private ImageDownloaderCache imgDownloader;
    private boolean bSHowingDetail = false;
    private int selectedPosition = 0;
    private final static int pageMax = 18;
    int pageCurrent = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        imgDownloader = new ImageDownloaderCache();

        rvGallery = (RecyclerView)findViewById(R.id.rvGallery);
        rvGalleryLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        rvGalleryLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        rvGallery.setLayoutManager(rvGalleryLayoutManager);
        int spanCount = 3; // 3 columns
        int spacing = 4; // 4px
        int dbspacing = Math.round(spacing * getResources().getDisplayMetrics().density);
        boolean includeEdge = false;
        rvGallery.addItemDecoration(new GridSpacingItemDecoration(spanCount, dbspacing, includeEdge));

        bSHowingDetail = false;


        //check for read external storage permision
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
            adapterList = getCameraImages(getActivity(), pageMax, (pageMax*pageCurrent));
            //rvGalleryAdapter = new GalleryAdapter(getActivity(), adapterList, imgDownloader, rvGallery_OnLoadMoreListener, rvGallery_OnPhotoSelectedListener);
            //rvGallery.setAdapter(rvGalleryAdapter);

            //use the horizontal flipper display
            SnapHelper snapHelper = new PagerSnapHelper();
            rvGallery.setLayoutManager(rvGalleryLinearLayoutManager);
            snapHelper.attachToRecyclerView(rvGallery);
            rvGalleryAdapter = new PhotoAdapter(getActivity(), adapterList, imgDownloader, rvGallery_OnLoadMoreListener, rvGallery_OnPhotoSwitchListener);
            rvGallery.setAdapter(rvGalleryAdapter);
            bSHowingDetail = true;


        }
        else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.PERMISSIONS_REQUEST_READ_STORAGE);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    adapterList = getCameraImages(getActivity(), pageMax, (pageMax*pageCurrent));
                    //rvGalleryAdapter = new GalleryAdapter(getActivity(), adapterList, imgDownloader, rvGallery_OnLoadMoreListener, rvGallery_OnPhotoSelectedListener);
                    //rvGallery.setAdapter(rvGalleryAdapter);

                    //use the horizontal flipper display
                    SnapHelper snapHelper = new PagerSnapHelper();
                    rvGallery.setLayoutManager(rvGalleryLinearLayoutManager);
                    snapHelper.attachToRecyclerView(rvGallery);
                    rvGalleryAdapter = new PhotoAdapter(getActivity(), adapterList, imgDownloader, rvGallery_OnLoadMoreListener, rvGallery_OnPhotoSwitchListener);
                    rvGallery.setAdapter(rvGalleryAdapter);
                    bSHowingDetail = true;

                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (bSHowingDetail) {

            int cPos = rvGalleryLinearLayoutManager.findFirstVisibleItemPosition();
            if (Constants.DEBUG) Log.d(TAG, "onBackPressed cPos:"+cPos);

            //this removes the SnapHelper
            rvGallery.setOnFlingListener(null);
            rvGallery.setLayoutManager(rvGalleryLayoutManager);
            rvGalleryAdapter = new GalleryAdapter(getActivity(), adapterList, imgDownloader, rvGallery_OnLoadMoreListener, rvGallery_OnPhotoSelectedListener);
            rvGallery.setAdapter(rvGalleryAdapter);
            rvGallery.scrollToPosition(cPos);
            rvGalleryLayoutManager.scrollToPosition(cPos);

            bSHowingDetail = false;
            fab.setVisibility(View.VISIBLE);

        }
        else {
            super.onBackPressed();
        }
    }

    private OnPhotoSelected rvGallery_OnPhotoSelectedListener = new OnPhotoSelected() {
        @Override
        public void onPhotoSelected(int position, View clickedView) {


            // save rect of view in screen coordinates
            final Rect viewRect = new Rect();
            clickedView.getGlobalVisibleRect(viewRect);

            /*
            Explode explode = new Explode();
            explode.setEpicenterCallback(new Transition.EpicenterCallback() {
                @Override
                public Rect onGetEpicenter(@NonNull Transition transition) {
                    //return null;
                    return viewRect;
                }
            });
            explode.setDuration(1000);
            explode.setInterpolator(new AnticipateOvershootInterpolator());

            Fade fade = new Fade();
            fade.setEpicenterCallback(new Transition.EpicenterCallback() {
                @Override
                public Rect onGetEpicenter(@NonNull Transition transition) {
                    //return null;
                    return viewRect;
                }
            });
            fade.setDuration(1000);
            fade.setInterpolator(new AccelerateInterpolator());
            */

            //TransitionManager.beginDelayedTransition((ViewGroup)clickedView.getParent(), fade);
            //((ViewGroup) clickedView.getParent()).setVisibility(View.INVISIBLE);

            selectedPosition = position;

            TransitionManager.beginDelayedTransition(rvGallery);

            //TransitionManager.beginDelayedTransition(rvGallery, fade);
            //rvGallery.setVisibility(View.INVISIBLE);
            //LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            //LinearSnapHelper
            //http://stackoverflow.com/questions/29134094/recyclerview-horizontal-scroll-snap-in-center
            SnapHelper snapHelper = new PagerSnapHelper();
            rvGallery.setLayoutManager(rvGalleryLinearLayoutManager);
            snapHelper.attachToRecyclerView(rvGallery);

            rvGalleryAdapter = new PhotoAdapter(getActivity(), adapterList, imgDownloader, rvGallery_OnLoadMoreListener, rvGallery_OnPhotoSwitchListener);
            rvGallery.setAdapter(rvGalleryAdapter);
            rvGallery.scrollToPosition(position);
            rvGalleryLinearLayoutManager.scrollToPosition(position);

            bSHowingDetail = true;
            fab.setVisibility(View.GONE);

            //TransitionManager.beginDelayedTransition(rvGallery, fade);
            //TransitionManager.beginDelayedTransition(rvGallery, explode);
            //rvGallery.setVisibility(View.VISIBLE);

            //TransitionManager.beginDelayedTransition((ViewGroup)clickedView.getParent(), explode);
            //((ViewGroup) clickedView.getParent()).setVisibility(View.VISIBLE);

        }
    };

    private OnLoadMore rvGallery_OnLoadMoreListener = new OnLoadMore() {
        @Override
        public void onLoadMore() {
            //set the view page to the next page
            //pageCurrent++;
            //get the next page of data
            //adapterList.addAll(getCameraImages(getActivity(), pageMax, (pageMax*pageCurrent)));

        }
    };

    private OnPhotoSwitch rvGallery_OnPhotoSwitchListener = new OnPhotoSwitch() {
        @Override
        public void onPhotoSwitch(int position) {
            rvGallery.smoothScrollToPosition(position);
        }
    };


    public static ArrayList<GalleryItem> getCameraImages(Context context, int limit, int offset) {

        final String[] projection = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME  };
        final String selection = null;
        final String[] selectionArgs = null;
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC LIMIT " +(limit)+ " OFFSET "+offset;
        final Uri mediaQueryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        final Cursor cursor = context.getContentResolver().query(mediaQueryUri,
                projection,
                selection,
                selectionArgs,
                orderBy);

        ArrayList<GalleryItem> result = new ArrayList<GalleryItem>();
        int itemCnt = 0;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                final int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                final int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                final int bidColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
                final int bnameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                do {
                    String sdata = cursor.getString(dataColumn);
                    final String sid = cursor.getString(idColumn);
                    final String sname = cursor.getString(nameColumn);
                    final String sbid = cursor.getString(bidColumn);
                    final String sbname = cursor.getString(bnameColumn);
                    final GalleryItem data = new GalleryItem(sdata, sid, sname, sbid, sbname);
                    result.add(data);
                    itemCnt++;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        else {
            if (Constants.DEBUG) Log.d(TAG, "getCameraImages query cursor null");
        }

        return result;
    }



}
