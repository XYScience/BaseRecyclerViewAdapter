package com.science.baserecyclerviewadaptertest;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.science.baserecyclerviewadapter.base.BaseAdapter;
import com.science.baserecyclerviewadapter.base.ViewHolder;
import com.science.baserecyclerviewadapter.interfaces.OnItemClickListener;
import com.science.baserecyclerviewadapter.interfaces.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final MyAdapter adapter = new MyAdapter(this);
        adapter.setOnItemClickListener(new OnItemClickListener<String>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, String data, int position) {
                Toast.makeText(MainActivity.this, "item = " + data, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemEmptyClick() {
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < 5; i++) {
                    list.add(i, "item : " + i);
                }
                // 首次请求失败后，点击再次请求网络
                getData(adapter, list);
            }

            @Override
            public void onItemLoadFailedClick() {

            }
        });
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(int currentPage) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<String> list = new ArrayList<String>();
                        for (int i = 0; i < 5; i++) {
                            list.add(i, "item : " + (adapter.getItemCount() - 1 + i));
                        }
                        adapter.setLoadMoreData(list);
                    }
                }, 2000);
            }
        });
        recyclerView.setAdapter(adapter);

        // 模拟网络请求数据：首次请求失败
        getData(adapter, null);
    }

    private void getData(final MyAdapter adapter, final List<String> list) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (list == null) {
                    adapter.showLoadFailed();
                } else {
                    adapter.setNewDatas(list);
                }
            }
        }, 3000);
    }

    class MyAdapter extends BaseAdapter<String> {

        public MyAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemLayoutId() {
            return R.layout.item_common;
        }

        @Override
        public void convert(ViewHolder viewHolder, String data) {
            viewHolder.setText(R.id.text, data);
        }
    }
}
