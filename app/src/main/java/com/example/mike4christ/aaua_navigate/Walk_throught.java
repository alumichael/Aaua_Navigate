package com.example.mike4christ.aaua_navigate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Walk_throught extends AppCompatActivity {

    private LinearLayout dotlayout;
    private ViewPager slideViewpager;
    private SlideAdapter slideAdapter;
    private TextView[] mDots;
    private Button mPrevious;
    private Button mNext;
    private int mCurrent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_throught);

        dotlayout=findViewById(R.id.dotlayout);
        slideViewpager=findViewById(R.id.slide_pager);

        slideAdapter=new SlideAdapter(this);

        slideViewpager.setAdapter(slideAdapter);
        addDotsIndicator(0);
        slideViewpager.addOnPageChangeListener(viewListener);

        mPrevious=findViewById(R.id.button);
        mNext=findViewById(R.id.button2);

        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideViewpager.setCurrentItem(mCurrent -1);
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrent==2){
                    Intent intent=new Intent(Walk_throught.this,MapsActivity.class);
                    startActivity(intent);
                    Walk_throught.this.finish();
                }else{
                    slideViewpager.setCurrentItem(mCurrent +1);
                }
            }
        });

    }

        public  void addDotsIndicator(int position){
        mDots=new TextView[3];
        dotlayout.removeAllViews();
        for(int i=0;i< mDots.length;i++){
            mDots[i]=new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.grey));
            dotlayout.addView(mDots[i]);
        }
        if(mDots.length>0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        };
        }
        ViewPager.OnPageChangeListener viewListener=new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                addDotsIndicator(i);
                mCurrent=i;
                if(i==0){
                    mNext.setEnabled(true);
                    mPrevious.setEnabled(false);
                    mPrevious.setVisibility(ViewPager.INVISIBLE);
                    mNext.setText("Next");
                    mPrevious.setText("Previous");
                }else if(i==mDots.length-1){
                    mNext.setEnabled(true);
                    mPrevious.setEnabled(true);
                    mPrevious.setVisibility(ViewPager.VISIBLE);
                    mNext.setText("Finish");
                    mPrevious.setText("Previous");


                }else{
                    mNext.setEnabled(true);
                    mPrevious.setEnabled(true);
                    mPrevious.setVisibility(ViewPager. VISIBLE);

                    mNext.setText("Next");
                    mPrevious.setText("Previous");
                }


            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        };
}
