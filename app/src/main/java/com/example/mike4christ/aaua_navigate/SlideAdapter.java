package com.example.mike4christ.aaua_navigate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SlideAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    public SlideAdapter(Context context){
        this.context=context;
    }
    // Array
    public int[] slide_images={
            R.drawable.slide12,
            R.drawable.slide22,
            R.drawable.slide32

    };
    public String[]slide_heading={
            "You are Welcome !","Route your Way","Real Location"
    };

    public String[] slide_desc={
      "This is Adekunle Ajasin University Navigation  App !\n" +
              "- Enjoy Smart Movement - \"",
      "Routing Current Location to desire destination in school",
      "Find your direction through real time location awareness "
    };



    @Override
    public int getCount() {
        return slide_heading.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view ==(LinearLayout) o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.slide_layout,container,false);
        ImageView slideImageView=(ImageView)view.findViewById(R.id.imageView2);
        TextView slideHeading=(TextView)view.findViewById(R.id.title);
        TextView slideDescription=(TextView)view.findViewById(R.id.slide_desc);
        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_heading[position]);
        slideDescription.setText(slide_desc[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
