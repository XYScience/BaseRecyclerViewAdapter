package com.science.baserecyclerviewadaptertest;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.science.baserecyclerviewadapter.base.BaseCommonAdapter;
import com.science.baserecyclerviewadapter.base.ViewHolder;
import com.science.baserecyclerviewadapter.interfaces.OnItemClickListener;
import com.science.baserecyclerviewadapter.interfaces.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NormalActivity extends AppCompatActivity {

    private boolean isFailed = true, isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("普通List");
        setSupportActionBar(toolbar);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));

        final MyAdapter adapter = new MyAdapter(this, recyclerView);
        adapter.setOnItemClickListener(new OnItemClickListener<Person>() {

            @Override
            public void onItemClick(Person data, int position) {
                Toast.makeText(NormalActivity.this, "name:" + data.getName(), Toast.LENGTH_SHORT).show();
//                adapter.removeData(position);
            }

            @Override
            public void onItemLongClick(Person data, int position) {
                Toast.makeText(NormalActivity.this, "long click name:" + data.getName()
                        , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemEmptyClick() {
                List<Person> list = new ArrayList<>();
                for (int i = 0; i < 8; i++) {
                    Random r = new Random();
                    List<Person.Score> listScore = new ArrayList<>();
                    for (int j = 0; j < r.nextInt(2) + 1; j++) {
                        listScore.add(j, new Person.Score("java score:" + (80 + r.nextInt(5))));
                    }
                    list.add(new Person("person:" + (adapter.getItemCount() - 1 + i), listScore));
                }
                // 首次请求失败后，点击再次请求网络
                getData(false, adapter, list);
            }
        });
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(int currentPage) {
                List<Person> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    Random r = new Random();
                    List<Person.Score> listScore = new ArrayList<>();
                    for (int j = 0; j < r.nextInt(2) + 1; j++) {
                        listScore.add(j, new Person.Score("java score:" + (80 + r.nextInt(5))));
                    }
                    list.add(new Person("person:" + (adapter.getItemCount() - 1 + i), listScore));
                }
                // 加载更多数据
                getData(true, adapter, list);
            }
        });
        recyclerView.setAdapter(adapter);

        // 模拟网络请求数据：首次请求失败
        getData(false, adapter, null);
    }

    /**
     * 模拟网络获取数据：1，第一次获取‘新’数据；2，下拉刷新获取‘新’数据；3，上拉加载更多获取数据
     *
     * @param isLoadMore
     * @param adapter
     * @param list
     */
    private void getData(final boolean isLoadMore, final MyAdapter adapter, final List<Person> list) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 模拟第一次网络访问失败
                if (list == null) {
                    adapter.showLoadFailed();
                } else {
                    if (isFirst) { // 模拟第一次网络访问成功
                        isFirst = false;
                        adapter.setData(isLoadMore, list);
                    } else {
                        // 模拟加载更多数据失败
                        if (isFailed) {
                            isFailed = false;
                            adapter.showLoadFailed();
                        }
                        // 模拟加载更多数据成功
                        else {
                            adapter.setData(isLoadMore, list);
                        }
                    }
                }
            }
        }, 2000);
    }

    class MyAdapter extends BaseCommonAdapter<List<Person>> {

        public MyAdapter(Context context, RecyclerView recyclerView) {
            super(context, recyclerView, true);
        }

        @Override
        public int getItemLayoutId() {
            return R.layout.item_common;
        }

        @Override
        public void convertCommon(ViewHolder viewHolder, List<Person> data, int position) {
            viewHolder.setText(R.id.text, data.get(position).getName());
        }
    }

}
