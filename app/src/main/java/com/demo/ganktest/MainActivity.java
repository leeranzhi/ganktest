package com.demo.ganktest;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.demo.ganktest.db.Mito;
import com.demo.ganktest.gson.Message;
import com.demo.ganktest.util.HttpUtil;
import com.demo.ganktest.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private MitoAdapter adapter;
    private RecyclerView recyclerView;
    private SharedPreferences prefs;
    private Mito[] mitos = new Mito[20];
    private SwipeRefreshLayout swipeRefresh;

    private List<Mito> mitoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        Toast.makeText(MainActivity.this, "正在加载...客官请稍等",
                Toast.LENGTH_SHORT).show();
        swipeRefresh.setRefreshing(true);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestMessage();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.icon_drawer_indicator);
        }
        navView.setCheckedItem(R.id.nav_call);
        navView.setNavigationItemSelectedListener(new NavigationView.
                OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String mitoUrl = prefs.getString("url0", null);
        Log.d(TAG, "mitoUrl的内容是。。。。。。" + mitoUrl);
        if (mitoUrl != null) {
            //有缓存时读取本地
            Log.d(TAG, "正在尝试读取本地数据.......请等待.....");
            loadLocation();
            Log.d(TAG, "准备初始化图片加载.......");
            initMitos();
        } else {
            //无缓存时向服务器发送请求
            Log.d(TAG, "准备向服务器发送请求");
            requestMessage();
        }
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MitoAdapter(mitoList);
        recyclerView.setAdapter(adapter);
        swipeRefresh.setRefreshing(false);
    }

    /**
     * 加载本地缓存内容
     */
    private void loadLocation() {
        for (int i = 0; i < mitos.length; i++) {
            String imageUrl = prefs.getString("url" + i, null);
            String imageName = prefs.getString("name" + i, null);
            Log.d(TAG, "url" + i);
            mitos[i] = new Mito();
            mitos[i].setName(imageName);
            mitos[i].setImageUrl(imageUrl);
        }
    }

    /**
     * 向服务器请求数据
     */
    private void requestMessage() {
        String requestUrl = "http://gank.io/api/random/data/%E7%A6%8F%E5%88%A9/20";
        Log.d(TAG, "准备等待服务器回应...");
        HttpUtil.sendOkHttpRequest(requestUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "服务器响应失败",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseMessage = response.body().string();
                final List<Message> messageList = Utility.handleMessageResponse(responseMessage);
                final Mito[] mitos = Utility.handleMitoResponse(messageList);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mitos != null) {
                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(MainActivity.this).
                                    edit();
                            for (int i = 0; i < mitos.length; i++) {
                                editor.putString("url" + i, mitos[i].getImageUrl());
                                editor.putString("name" + i, mitos[i].getName());
                            }
                            editor.apply();
                            loadLocation();
                            Log.d(TAG, "准备初始化图片加载.......");
                            initMitos();
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(MainActivity.this, "获取失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }


    private void initMitos() {
        mitoList.clear();
        Log.d(TAG, "初始化列表，准备添加中.....");
        for (int i = 0; i < mitos.length; i++) {
            mitoList.add(mitos[i]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                Toast.makeText(MainActivity.this, "You clicked Setting",
                        Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }
}
