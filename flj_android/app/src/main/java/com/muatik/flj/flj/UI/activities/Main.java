package com.muatik.flj.flj.UI.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.BusManager;
import com.muatik.flj.flj.UI.adapters.JobViewHolder;
import com.muatik.flj.flj.UI.entities.Job;
import com.muatik.flj.flj.UI.entities.JobFilter;
import com.muatik.flj.flj.UI.fragments.*;
import com.muatik.flj.flj.UI.fragments.JobList;
import com.muatik.flj.flj.UI.views.SearchForm;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Created by muatik on 25.07.2016.
 */
public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Bus bus = BusManager.get();
    private JobFilter currentJobFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        onSearchSubmitted(new Search.EventOnSubmit(new JobFilter("", "istanbul")));
        showFragment(new Search());
        bus.register(this);
    }



    @Subscribe
    public void onSearchSubmitted(Search.EventOnSubmit event) {
        JobList jobList = new JobList();
        Bundle bundle = new Bundle();
        bundle.putSerializable("jobFilter", event.filter);
        jobList.setArguments(bundle);
        showFragment(jobList);
    }

    @Subscribe
    public void onJobClicked(JobViewHolder.EventOnJobClicked event) {
        JobDetail jobDetail = new JobDetail();
        Bundle bundle = new Bundle();
        bundle.putSerializable("job", event.job);
        jobDetail.setArguments(bundle);
        showFragment(jobDetail);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_item_search) {
            showFragment(new Search());
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            showFragment(new Search());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content, fragment)
                .addToBackStack(null)
                .commit();

    }
}
