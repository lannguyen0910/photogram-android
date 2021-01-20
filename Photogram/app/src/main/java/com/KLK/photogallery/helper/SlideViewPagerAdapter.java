package com.KLK.photogallery.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.KLK.photogallery.R;
import com.KLK.photogallery.camera.NextActivity;
import com.KLK.photogallery.camera.StyleActivity;


public class SlideViewPagerAdapter extends PagerAdapter {
    Activity activity;
    Context ctx;

    public SlideViewPagerAdapter(Activity activity) {
        this.activity = activity;
        this.ctx = activity;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.layout_style_slide_screen, container,false);

        ImageView logo = view.findViewById(R.id.logo);

        ImageView ind1 = view.findViewById(R.id.ind1);
        ImageView ind2 = view.findViewById(R.id.ind2);
        ImageView ind3 = view.findViewById(R.id.ind3);
        ImageView ind4 = view.findViewById(R.id.ind4);
        ImageView ind5 = view.findViewById(R.id.ind5);
        ImageView ind6 = view.findViewById(R.id.ind6);

        ImageView next = view.findViewById(R.id.next);
        ImageView back = view.findViewById(R.id.back);

        TextView title = view.findViewById(R.id.title);
        TextView desc = view.findViewById(R.id.desc);

        Button btnGetStarted = view.findViewById(R.id.btnGetStarted);
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("chosenStyleID",String.valueOf(position));
                activity.setResult(Activity.RESULT_OK,returnIntent);
                activity.finish();
            }

        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StyleActivity.viewPager.setCurrentItem(position + 1);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StyleActivity.viewPager.setCurrentItem(position - 1);
            }
        });

        switch (position)
        {
            case 0:
                logo.setImageResource(R.drawable.style_1);
                ind1.setImageResource(R.drawable.selected);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.unselected);
                ind5.setImageResource(R.drawable.unselected);
                ind6.setImageResource(R.drawable.unselected);

                title.setText(R.string.style_1_title);
                desc.setText(R.string.style_1_desc);
                back.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                break;

            case 1:
                logo.setImageResource(R.drawable.style_2);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.selected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.unselected);
                ind5.setImageResource(R.drawable.unselected);
                ind6.setImageResource(R.drawable.unselected);

                title.setText(R.string.style_2_title);
                desc.setText(R.string.style_2_desc);
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);

                break;

            case 2:
                logo.setImageResource(R.drawable.style_3);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.selected);
                ind4.setImageResource(R.drawable.unselected);
                ind5.setImageResource(R.drawable.unselected);
                ind6.setImageResource(R.drawable.unselected);

                title.setText(R.string.style_3_title);
                desc.setText(R.string.style_3_desc);
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);

                break;

            case 3:
                logo.setImageResource(R.drawable.style_4);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.selected);
                ind5.setImageResource(R.drawable.unselected);
                ind6.setImageResource(R.drawable.unselected);

                title.setText(R.string.style_4_title);
                desc.setText(R.string.style_4_desc);
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);

                break;

            case 4:
                logo.setImageResource(R.drawable.style_5);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.unselected);
                ind5.setImageResource(R.drawable.selected);
                ind6.setImageResource(R.drawable.unselected);

                title.setText(R.string.style_5_title);
                desc.setText(R.string.style_5_desc);
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);

                break;

            case 5:
                logo.setImageResource(R.drawable.style_6);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.unselected);
                ind5.setImageResource(R.drawable.unselected);
                ind6.setImageResource(R.drawable.selected);

                title.setText(R.string.style_6_title);
                desc.setText(R.string.style_6_desc);
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.GONE);

                break;

        }

        container.addView(view);
        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}

