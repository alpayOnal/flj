package com.muatik.flj.flj.UI.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by muatik on 8/11/16.
 */
public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected Bus bus = BusManager.get();
    // since otto bus does not support to make the subscriptions for derivative classes, we manually
    // add subscriptions by creating an object. we also unsubscribe using the same object.
    // https://github.com/square/otto/issues/26
    private Object subscribers = new Object(){
        @Subscribe
        public void onSearchSubmitted(Main.EventOnSubmit event) {
            Intent intent = new Intent(BaseActivity.this, JobList.class);
            JobFilter filter = event.filter;
            intent.putExtra("jobFilter", filter);
            startActivity(intent);
        }

        @Subscribe
        public void onJobClicked(JobViewHolder.EventOnJobClicked event) {
            Intent intent = new Intent(BaseActivity.this, JobDetail.class);
            Job job = event.job;
            intent.putExtra("job", job);
            startActivity(intent);

            //        JobDetail jobDetail = new JobDetail();
            //        Bundle bundle = new Bundle();
            //        bundle.putSerializable("job", event.job);
            //        jobDetail.setArguments(bundle);
            //        showFragment(jobDetail);
        }
    };

    private Unbinder unbinder;


    protected void init() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

//    @Subscribe
//    public void onSignedIn(AccountManager.EventSuccessfulSignIn event) {
//        Toast.makeText(this, "Welcome: " + event.account.getUsername(), Toast.LENGTH_LONG).show();
//        //accountEmail.setText(AccountManager.getAuthenticatedAccount().getEmail());
//        //accountName.setText(AccountManager.getAuthenticatedAccount().getUsername());
//        //accountImage.setImageURI(Uri.parse(AccountManager.getAuthenticatedAccount().userprofile.getPicture()));
//    }

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
        // automatically handle clicks on the Home/Up signin_button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_item_search) {
            showFragment(new SearchForm());
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            startActivity(new Intent(this, JobList.class).putExtra("jobFilter", new JobFilter("python","turkey","istanbul")));
        } else if (id == R.id.nav_gallery) {
            showFragment(new SearchForm());
        } else if (id == R.id.nav_slideshow) {
            showFragment(new SearchHistoryManager());
        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(this, AlarmManager.class));
        } else if (id == R.id.nav_share) {
            startActivity(new Intent(getApplication(), GoogleSignin.class));
        } else if (id == R.id.nav_send) {
            AccountManager.signout();
            startActivity(new Intent(getApplication(), Login.class));
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

    @Override
    protected void onPause() {
        Log.i("FLJ", this.getClass().getSimpleName() + " onPause " + System.identityHashCode(this));
        super.onPause();
        SearchHistory.commit();
    }

    @Override
    protected void onStop() {
        Log.i("FLJ", this.getClass().getSimpleName() + " onStop " + System.identityHashCode(this));
        unbinder.unbind();
        bus.unregister(subscribers);
        super.onStop();
    }

    @Override
    protected void onStart() {
        Log.i("FLJ", this.getClass().getSimpleName() + " onStart " + System.identityHashCode(this));
        unbinder = ButterKnife.bind(this);
        bus.register(subscribers);
        super.onStart();
    }


//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        Log.i("FLJ", this.getClass().getSimpleName() + " onCreate " + System.identityHashCode(this));
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public void onDestroy() {
//        Log.i("FLJ", this.getClass().getSimpleName() + " onDestroy " + System.identityHashCode(this));
//        super.onDestroy();
//    }
//
//    @Override
//    public void onResume() {
//        Log.i("FLJ", this.getClass().getSimpleName() + " onResume " + System.identityHashCode(this));
//        super.onResume();
//    }

}
