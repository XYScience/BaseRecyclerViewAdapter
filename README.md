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

2，Section List
-----
**使用方法：**       

1，继承SectionEntity基类，用于区分各个section部分
```
class MySection extends SectionEntity<Course> {
        public MySection(boolean isHeader, boolean isFooter, Course data) {
            super(isHeader, isFooter, data);
        }
        public MySection(Course data) {
            super(data);
        }
    }
```

2，继承BaseSectionAdapter<MySection>基类     

3，数据添加          
```
List<SectionActivity.MySection> list = new ArrayList<>();
list.add(new SectionActivity.MySection(true, false, new Course("头部1", 22)));
list.add(new SectionActivity.MySection(true, false, new Course("头部2", 22)));
for (int i = 0; i < 5; i++) {
    list.add(new SectionActivity.MySection(new Course("item:" + (adapter.getItemCount() - 1 + i), 20 + i)));
}
list.add(new SectionActivity.MySection(false, true, new Course("尾部", 22)));     
```

**预览Screenshot：**       
![image](https://github.com/XYScience/BaseRecyclerViewAdapter/raw/master/screenshot/section_list.gif)

3，粘性header List         
-----           
**使用方法**      

1，继承BaseStickyAdapter<List<Person>>基类    
```
class StickyAdapter extends BaseStickyAdapter<List<Person>> {
    private List<Person> listPerson = new ArrayList<>();
    public StickyAdapter(Context context) {
        super(context);
    }
    @Override
    public int getItemHeaderLayoutId() {
        return R.layout.item_header;
    }
    @Override
    public int getItemLayoutId() {
        return R.layout.item_common;
    }
    @Override
    public void convertCommon(ViewHolder viewHolder, List<Person> data, int section, int position) {
        viewHolder.setText(R.id.text, data.get(section).getCourse().get(position).getJava());
    }
    @Override
    public void convertHeader(ViewHolder viewHolder, List<Person> data, int section) {
        viewHolder.setText(R.id.header, data.get(section).getName());
    }
    @Override
    public int getSectionCount() {
        return listPerson.size();
    }
    @Override
    public int getCountOfSection(int section) {
        return listPerson.get(section).getCourse().size();
    }
    @Override
    public void updateData(boolean isLoadMore, List<Person> list) {
        if (isLoadMore) {
             listPerson.addAll(list);
        } else {
             listPerson.clear();
             listPerson.addAll(list);
        }
    }      
```             
2，调用recyclerview的addItemDecoration方法实现粘性头部               
```
recyclerView.addItemDecoration(new StickyHeaderItemDecoration());
```     
**预览Screenshot：**       
![image](https://github.com/XYScience/BaseRecyclerViewAdapter/raw/master/screenshot/sticky_header_list.gif)             

4，如何使用
-----
Step 1. Add it in your root build.gradle at the end of repositories:            
```
allprojects {
    repositories {
         ...
         jcenter()
         maven { url "https://jitpack.io" }
    }
}             
```             
Step 2. Add the dependency              
```
dependencies {
    compile 'com.github.XYScience:BaseRecyclerViewAdapter:1.0.7'
}     
```
