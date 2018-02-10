package com.liyi.loadmorelistview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private LoadMoreListView loadMoreListView;
    private MultiSwipeRefreshLayout swipeRefreshLayout;
    private LoadMoreListViewAdapter adapter;
    private ArrayList<String> list;
    private Toolbar toolBar;
    private ActionBar actionBar;
    private int touchSlop;
    private int preY;

    private View mEmptyView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrap_main);

        list=new ArrayList<>(2);
        for(int i=0;i<0;i++){
            list.add(""+i);
        }
        initView();
    }

    private void initView() {


        swipeRefreshLayout= (MultiSwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        //setColorSchemeResources()可以改变加载图标的颜色。
        swipeRefreshLayout.setColorSchemeResources(new int[]{R.color.colorAccent, R.color.colorPrimary});

        loadMoreListView= (LoadMoreListView) findViewById(R.id.LoadMoreListView);
        adapter=new LoadMoreListViewAdapter(list,this);
        loadMoreListView.setAdapter(adapter);

        // Retrieve the empty view
        mEmptyView = findViewById(android.R.id.empty);
        loadMoreListView.setEmptyView(mEmptyView);
        swipeRefreshLayout.setSwipeableChildren(R.id.LoadMoreListView, android.R.id.empty);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        list.clear();
                        adapter.clear();
                        for (int i = 0; i < 25; i++) {
                            adapter.add("" + i);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        loadMoreListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void loadMore() {
                Log.i("count", count + "");
                if (count < 3) {
                    loadError();
                    count++;
                } else if (count < 6) {
                    count++;
                    loadSuccess();
                } else {
                    count++;
                    loadFinish();
                }

            }
        });

        loadMoreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setCurrPosition(position);
                startActivity(new Intent(MainActivity.this,MyChildActivity.class));
            }
        });

        //init();
        mEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(true);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        list.clear();
                        adapter.clear();
                        for (int i = 0; i < 25; i++) {
                            adapter.add("" + i);
                        }
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }, 2000);

            }
        });

        initActionBar();

        initDrawer();
    }

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private void initDrawer() {
        drawerLayout= (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView= (NavigationView) findViewById(R.id.navigationView);

        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        actionBar.setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setCheckable(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void initActionBar() {
        toolBar= (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        actionBar=getSupportActionBar();
        touchSlop= ViewConfiguration.get(this).getScaledTouchSlop();
        loadMoreListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                int currentY= (int) ev.getY();
                switch (ev.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        preY= currentY;
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                        if(preY-currentY>touchSlop){
                            actionBar.hide();
                        }else if(currentY-preY>touchSlop){
                            actionBar.show();
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void loadFinish() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMoreListView.loadComplete();
            }
        },2000);
    }

    int count=0;

    private void loadSuccess() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.add("书籍");
                loadMoreListView.loadComplete();
            }
        },5000);
    }

    private void loadError() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMoreListView.loadError();
            }
        },2000);
    }


    //https://developer.android.com/training/appbar/action-views.html
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tool_bar,menu);
        MenuItem searchItem = menu.findItem(R.id.toolBarSearch);
        final SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);


        SearchManager searchManager= (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));


        /*searchView.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, "" + query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.toolBar1:
                Snackbar.make(loadMoreListView,"toolBar1",Snackbar.LENGTH_SHORT).setAction("ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this,"toolBar1",Toast.LENGTH_LONG).show();
                    }
                }).show();
                break;
            case R.id.toolBar2:
                Toast.makeText(this, "toolBar2", Toast.LENGTH_LONG).show();
                break;
            case R.id.toolBarSearch:
                Snackbar.make(loadMoreListView,"toolBarSearch",Snackbar.LENGTH_SHORT).setAction("ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this,"toolBar1",Toast.LENGTH_LONG).show();
                    }
                }).show();
                break;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
