# BaseRecyclerViewAdapter
A common adapter of recyclerview
1，普通List
-----
**使用方法：**     

1，继承BaseCommonAdapter基类     
```    
class MyAdapter extends BaseCommonAdapter<List<Person>> {
        public MyAdapter(Context context) {
            super(context);
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
```   
2，数据加载方法使用    
`adapter.setData(false, list);`：第一次加载数据，或者下拉刷新加载数据（全部替换）。   
`adapter.setData(true, list);`：上拉加载更多数据。（可选，需要设置上拉加载更多监听）    
`adapter.showLoadFailed();`：数据加载失败，包括无数据和有数据。   

3，item点击事件监听    
```   
// OnItemClickListener为自定义listener
adapter.setOnItemClickListener(new OnItemClickListener<Person>() {
    @Override
    public void onItemClick(ViewHolder viewHolder, Person data, int position) {
        Toast.makeText(NormalActivity.this, data.getName(), Toast.LENGTH_SHORT).show();
    }
    // 可选，无数据时的屏幕点击事件监听
    @Override
    public void onItemEmptyClick() {
    }
});   
```   
4，加载更多监听（可选）    
```   
adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
    @Override
    public void onLoadMore(int currentPage) {
              
    }
});
```   
**预览Screenshot：**

![image](https://github.com/XYScience/BaseRecyclerViewAdapter/raw/master/screenshot/common_list.gif)



