package nus.me.behaviordemo;

import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyBehaviorActivity extends AppCompatActivity {

    private static RecyclerView recyclerview;
    private CoordinatorLayout coordinatorLayout;
    private MyAdapter adapter;
    private LinearLayoutManager mLayoutManager;
    private int lastVisibleItem ;//recyclerview最后显示的Item,用于判断recyclerview自动加载下一页
    private int page=1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_behavior_demo);

        StatusBarUtil.setStatusBarColor(MyBehaviorActivity.this,R.color.colorPrimaryDark);//设置状态栏颜色

        initView();
        setListener();

        new GetData().execute("http://news.at.zhihu.com/api/4/news/before/20161009");

    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.behavior_demo_coordinatorLayout);

        recyclerview=(RecyclerView)findViewById(R.id.behavior_demo_recycler);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerview.setLayoutManager(mLayoutManager);

        swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.behavior_demo_swipe_refresh) ;
        swipeRefreshLayout.setProgressViewOffset(false, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));//调整下拉控件位置

        fab=(FloatingActionButton) findViewById(R.id.fab);


    }



    private void setListener(){

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page=1;
                new GetData().execute("http://news.at.zhihu.com/api/4/news/before/20161009");
            }
        });

        //recyclerview滚动监听实现自动加载
        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                0：当前屏幕停止滚动；1时：屏幕在滚动 且 用户仍在触碰或手指还在屏幕上；2时：随用户的操作，屏幕上产生的惯性滑动；

                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem +2>=mLayoutManager.getItemCount()) {

                    new GetData().execute("http://news.at.zhihu.com/api/4/news/before/2016100"+(++page));
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int positons = mLayoutManager.findLastVisibleItemPosition();
                lastVisibleItem = positons;

            }
        });
    }

    //获取图片列表数据
    private class GetData extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {

            return MyOkhttp.get(params[0]);
        }
        private ArrayList<Info> list_info = new ArrayList<>();

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(!TextUtils.isEmpty(result)){

                JSONObject jsonObject;
                Gson gson=new Gson();
                JSONArray stories=null;

                try {
                    jsonObject = new JSONObject(result);
                    stories = jsonObject.optJSONArray("stories");

                    for (int i = 0; i < stories.length(); i++) {
                        Info info = new Info();
                        info.title = ((JSONObject) stories.get(i)).opt("title").toString();
                        info.url_img = ((JSONObject) stories.get(i)).optJSONArray("images").get(0).toString();
                        list_info.add(info);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(adapter==null){
                    adapter = new MyAdapter(MyBehaviorActivity.this,list_info);
                    recyclerview.setAdapter(adapter);
                }else{

                    adapter.notifyDataSetChanged();

                }

            }

            swipeRefreshLayout.setRefreshing(false);
        }
    }

}
