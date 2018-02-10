package com.example.guohouxiao.photogallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guohouxiao on 2017/4/27.
 */

public class PhotoGalleryFragment extends VisibleFragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem.PhotosBean.PhotoBean> mItems = new ArrayList<>();
    //private static int page = 1;//初始化页数
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);//保留fragment
        setHasOptionsMenu(true);//让fragment接收菜单回调方法
        //new FetchItemsTask().execute();//调用execute()方法启动AsynTask，继而出发购台线程并调用doInBackground()方法
        updateItems();

/*        //服务启动代码
        Intent i = PollService.newIntent(getActivity());
        getActivity().startService(i);*/
/*        PollService.setServiceAlarm(getActivity(), true);//添加定时器启动代码*/

        //创建反馈Handler，因为在onCreate()方法中创建，因此会与主线程的Looper相关联
        Handler responseHandler = new Handler();
        //创建并启动线程
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        //关联使用反馈Handler
        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
            @Override
            //图片下载完成交给UI去显示时调用该方法
            public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
                //使用返回的Bitmap执行UI更新操作，即用新下载的Bitmap来设置PhotoHolder的Drawable
                Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                target.bindDrawable(drawable);
            }
        });
        mThumbnailDownloader.start();
        //在start()方法之后调用getLooper()方法，可以保证线程就绪，避免潜在竞争
        //调用getLooper()方法之前，不能保证onLooperPrepared()方法得到调用
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);

        //mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        //mPhotoRecyclerView.addOnScrollListener(mOnScrollListener);

        mPhotoRecyclerView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        //动态调整网格列
                        int columns = mPhotoRecyclerView.getWidth() / 240;
                        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),columns));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mPhotoRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        setupAdapter();
                        mPhotoRecyclerView.addOnScrollListener(mOnScrollListener);
                    }
                });

        /*setupAdapter();//配置adapter*/
        return v;
    }

    //分页
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            int lastPosition = gridLayoutManager.findLastCompletelyVisibleItemPosition();
            if (newState == RecyclerView.SCROLL_STATE_IDLE && lastPosition >= recyclerView.getAdapter().getItemCount() - 1) {
                //page++;
                updateItems();
            }
        }
    };

    //adapter的配置和关联
    private void setupAdapter() {
        //检查isAdded()的返回值是否为true，确认fragment是否已与目标activity相关联，进而保证getActivity()方法返回结果不为空
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();//调用清理方法，清空下载器
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();//退出线程，如不终止HandlerThread，它会一直运行
        Log.i(TAG, "Background thread destoryed");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        //日志记录SearchView.OnQueryTextListener事件
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "QueryTextChange: " + query);
                QueryPreferences.setStoredQuery(getActivity(), query);//存储用户提交的查询信息
                updateItems();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "QueryTextChange: " + newText);
                return false;
            }
        });

        //让SearchView文本框默认显示已保存查询信息
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query,false);
            }
        });

        //菜单项切换
        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarmOn(getActivity())) {
            toggleItem.setTitle(R.string.stop_polling);
        } else {
            toggleItem.setTitle(R.string.start_polling);
        }
    }

    @Override
    //清除查询信息
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateItems();
                return true;
            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
                getActivity().invalidateOptionsMenu();//让选项菜单失效
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //调用FetchItemsTask的封装方法
    private void updateItems() {
        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
    }

    private class PhotoHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        //private TextView mTitleTextView;
        private ImageView mItemImageView;
        private GalleryItem.PhotosBean.PhotoBean mPhotoBean;

        public PhotoHolder(View itemView) {
            super(itemView);
            //mTitleTextView = (TextView) itemView;
            mItemImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
            itemView.setOnClickListener(this);
        }

/*        public void bindGalleryItem(GalleryItem.PhotosBean.PhotoBean item) {
            mTitleTextView.setText(item.getTitle());
        }*/

        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }

        public void bindPhotoBean(GalleryItem.PhotosBean.PhotoBean photoBean) {
            mPhotoBean = photoBean;
        }

        @Override
        public void onClick(View v) {
            //Intent i = new Intent(Intent.ACTION_VIEW, mPhotoBean.getPhotoPageUri());
            Intent i = PhotoPageActivity.newIntent(getActivity(), mPhotoBean.getPhotoPageUri());
            startActivity(i);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem.PhotosBean.PhotoBean> mGalleryItems;

        public PhotoAdapter(List<GalleryItem.PhotosBean.PhotoBean> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
/*            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);*/
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem.PhotosBean.PhotoBean galleryItem = mGalleryItems.get(position);
            //holder.bindGalleryItem(galleryItem);
            holder.bindPhotoBean(galleryItem);
            Drawable placeholder = getResources().getDrawable(R.drawable.inory);
            holder.bindDrawable(placeholder);
            mThumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl_s());//关联使用ThumbnailDownloader

            //为前十个和接下来十个GalleryItem预加载Bitmap
            for (int i = Math.max(0, position - 10); i < Math.min(mGalleryItems.size() - 1, position + 10); i++) {
                mThumbnailDownloader.queuePreloadThumbnail(mGalleryItems.get(i).getUrl_s());
            }
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }

    }

    //使用AsyncTask工具类创建后台线程，在该线程上调用doInBackground()方法运行网络连接代码
    //第一个类型参数可指定输入参数的类型
    //第二个类型参数可指定发送进度更新需要的类型
    //第三个泛型参数为AsyncTask返回的结果数据类型，设置了doInBackground()方法返回结果的数据类型，以及onPostExecute()方法输入参数的数据类型
    private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem.PhotosBean.PhotoBean>> {

        private String mQuery;

        public FetchItemsTask(String query) {
            mQuery = query;
        }

        @Override
        protected List<GalleryItem.PhotosBean.PhotoBean> doInBackground(Void... params) {
 /*           try {
                String result = new FlickrFetchr().getUrlString("https://www.bignerdranch.com");
                Log.i(TAG, "Fetched contents of URL: " + result);
            } catch (IOException e) {
                Log.e(TAG, "Failed to fetch URL: " + e);
            }*/
            //return new FlickrFetchr().fetchItems(page);//获取指定页的item
            /*return null;*/

            //硬编码的搜索字符串
            if (mQuery == null) {
                return new FlickrFetchr().fetchRecentPhotos();
            } else {
                return new FlickrFetchr().searchPhotos(mQuery);
            }
        }

        @Override
        //onPostExecute()方法在doInBackground()方法执行完毕后运行，而且是在主线程而非后台线程上运行
        //为避安全隐患，不推荐也不允许从后台线程更新UI，因此可在此方法内更新UI，较为安全
        protected void onPostExecute(List<GalleryItem.PhotosBean.PhotoBean> items) {
            mItems = items;
            setupAdapter();
        }
    }
}
