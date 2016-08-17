package com.muatik.flj.flj.UI.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.entities.AccountManager;
import com.muatik.flj.flj.UI.entities.Alarms;
import com.muatik.flj.flj.UI.entities.Job;
import com.muatik.flj.flj.UI.entities.JobFilter;
import com.muatik.flj.flj.UI.entities.SearchHistory;
import com.muatik.flj.flj.UI.fragments.SearchForm;
import com.muatik.flj.flj.UI.fragments.SearchHistoryManager;
import com.muatik.flj.flj.UI.utilities.BusManager;
import com.muatik.flj.flj.UI.views.JobViewHolder;
import com.muatik.flj.flj.UI.views.MainNavProfile;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by muatik on 8/11/16.
 */
public class ActivityWithDrawer extends BaseActivity {
    protected void init() {
        unbinder = ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);
        MainNavProfile.init(headerLayout);

        Alarms.init(getApplicationContext());
        SearchHistory.init(getApplicationContext());
    }

}
