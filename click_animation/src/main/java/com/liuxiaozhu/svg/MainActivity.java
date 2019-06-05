package com.liuxiaozhu.svg;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    /**
     * imgView
     * @param view
     */
    public void onClick(View view) {
        ImageView imageView = (ImageView) view;
        Drawable drawable = imageView.getDrawable();
        ((Animatable)drawable).start();
    }
}
