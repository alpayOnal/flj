package com.muatik.flj.flj.UI.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.muatik.flj.flj.R;

public class PostJob extends DetailActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_job_activity);
        init();

        getSupportActionBar().setTitle(getResources().getString(R.string.post_job_activity_title));

    }
}
