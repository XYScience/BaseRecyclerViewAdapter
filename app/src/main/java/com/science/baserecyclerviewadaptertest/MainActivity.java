package com.science.baserecyclerviewadaptertest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * @author 幸运Science
 * @description
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @data 2016/10/14
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.normal:
                Intent intent1 = new Intent(MainActivity.this, NormalActivity.class);
                startActivity(intent1);
                break;
            case R.id.section_header_footer:
                Intent intent2 = new Intent(MainActivity.this, SectionActivity.class);
                startActivity(intent2);
                break;
            case R.id.sticky_header:
                Intent intent3 = new Intent(MainActivity.this, StickyHeaderActivity.class);
                startActivity(intent3);
                break;
        }
    }
}
