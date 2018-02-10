package com.liyi.loadmorelistview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/8/12.
 */
public class LoadMoreListView extends ListView implements AbsListView.OnScrollListener {

    private Context context;
    private View footerView;
    private boolean isLastRow;
    private boolean isLoading;
    private boolean isError;
    private OnLoadMoreListener loadMoreListener;



    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener){
        this.loadMoreListener=loadMoreListener;
    }

    public  interface OnLoadMoreListener{
        void loadMore();
    }

    public LoadMoreListView(Context context) {
        this(context,null);
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    private void init() {
        footerView= LayoutInflater.from(context).inflate(R.layout.layout_footer,this,false);
        this.addFooterView(footerView);
        ( (footerView.findViewById(R.id.footerTextView))).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isError){
                    showLoading("正在加载");
                    isLoading=true;
                    loadMoreListener.loadMore();
                    isError=false;
                }
            }
        });
        setOnScrollListener(this);


    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Log.i("loadMore","onScrollStateChanged:"+scrollState+",isLastRow"+isLastRow);
        if(scrollState==SCROLL_STATE_IDLE){
            if(isLastRow && !isLoading){
                showLoading("正在加载");
                isLoading=true;
                loadMoreListener.loadMore();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.i("loadMore","onScroll---"+"firstVisibleItem:"+firstVisibleItem+",visibleItemCount:"+visibleItemCount+",totalItemCount:"+totalItemCount);
        if(firstVisibleItem+visibleItemCount>=totalItemCount && totalItemCount>0){
            isLastRow=true;
        }else {
            isLastRow=false;
        }
    }

    private void showLoading(String msg) {
        footerView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        ((TextView) (footerView.findViewById(R.id.footerTextView))) .setText(msg);
    }

    private void hiddenLoading(String msg) {
        footerView.findViewById(R.id.progressBar).setVisibility(View.GONE);
        ((TextView) (footerView.findViewById(R.id.footerTextView))) .setText(msg);
    }

    public void loadComplete(){
        Log.i("loadMore","loadComplete"+",isLastRow"+isLastRow);
        isError=false;
        isLoading=false;
        isLastRow=false;
        hiddenLoading("-- end --");
    }

    public void loadError(){
        isLoading=false;
        isError=true;
        hiddenLoading("加载失败，点击重新加载");

    }


    /**
     *  maxOverScrollY,listVIew具有弹性
     */
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, 100, isTouchEvent);
    }



}
