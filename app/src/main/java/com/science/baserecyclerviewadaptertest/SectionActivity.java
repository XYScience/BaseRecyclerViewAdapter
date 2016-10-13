package com.science.baserecyclerviewadaptertest;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.science.baserecyclerviewadapter.base.BaseSectionAdapter;
import com.science.baserecyclerviewadapter.base.ViewHolder;
import com.science.baserecyclerviewadapter.entity.SectionEntity;
import com.science.baserecyclerviewadapter.interfaces.OnItemClickListener;
import com.science.baserecyclerviewadapter.interfaces.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 幸运Science
 * @description
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @data 2016/10/13
 */

public class SectionActivity extends AppCompatActivity {

    private boolean isFailed = true, isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final SectionActivity.MySectionAdapter adapter = new SectionActivity.MySectionAdapter(this);
        adapter.setOnItemClickListener(new OnItemClickListener<SectionActivity.MySection>() {

            @Override
            public void onItemClick(ViewHolder viewHolder, SectionActivity.MySection data, int position) {
                Toast.makeText(SectionActivity.this, data.data.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemEmptyClick() {
                List<SectionActivity.MySection> list = new ArrayList<>();
                list.add(new SectionActivity.MySection(true, new Person("头部", 22)));
                for (int i = 0; i < 5; i++) {
                    list.add(new SectionActivity.MySection(false, new Person("item:" + (adapter.getItemCount() - 1 + i), 20 + i)));
                }
                // 首次请求失败后，点击再次请求网络
                getData(false, adapter, list);
            }
        });
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(int currentPage) {
                List<SectionActivity.MySection> list = new ArrayList<>();
                list.add(new SectionActivity.MySection(true, new Person("头部", 22)));
                for (int i = 0; i < 5; i++) {
                    list.add(new SectionActivity.MySection(false, new Person("item:" + (adapter.getItemCount() - 1 + i), 20 + i)));
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
    private void getData(final boolean isLoadMore, final SectionActivity.MySectionAdapter adapter, final List<SectionActivity.MySection> list) {
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

    class MySectionAdapter extends BaseSectionAdapter<SectionActivity.MySection> {

        public MySectionAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemHeaderLayoutId() {
            return R.layout.item_header;
        }

        @Override
        public void convertHeader(ViewHolder viewHolder, SectionActivity.MySection data) {
            Person person = (Person) data.data;
            viewHolder.setText(R.id.header, String.valueOf(person.getName()));
        }

        @Override
        public int getItemLayoutId() {
            return R.layout.item_common;
        }

        @Override
        public void convert(ViewHolder viewHolder, SectionActivity.MySection data) {
            Person person = (Person) data.data;
            viewHolder.setText(R.id.text, person.getName());
        }
    }

    class MySection extends SectionEntity<Person> {

        public MySection(boolean isHeader, Person data) {
            super(isHeader, data);
        }
    }
}