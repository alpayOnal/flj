package com.muatik.flj.flj.UI.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.multidex.MultiDex;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.entities.AccountManager;
import com.muatik.flj.flj.UI.entities.Alarms;
import com.muatik.flj.flj.UI.entities.SearchHistory;
import com.muatik.flj.flj.UI.fragments.SearchForm;
import com.muatik.flj.flj.UI.utilities.BusManager;
import com.muatik.flj.flj.UI.views.*;
import com.muatik.flj.flj.UI.entities.JobFilter;
import com.muatik.flj.flj.UI.fragments.*;
import com.muatik.flj.flj.UI.fragments.JobList;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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
        View headerLayout = navigationView.getHeaderView(0);

        TextView accountName = (TextView) headerLayout.findViewById(R.id.account_name);
        TextView accountEmail = (TextView) headerLayout.findViewById(R.id.account_email);
        ImageView accountImage = (ImageView) headerLayout.findViewById(R.id.account_image);

        accountName.setText(AccountManager.getAuthenticatedAccount().getUsername());
        accountEmail.setText(AccountManager.getAuthenticatedAccount().getEmail());
        accountImage.setImageURI(Uri.parse(AccountManager.getAuthenticatedAccount().userprofile.getPicture()));




//        AccountManager.signinBasicAuth("amuatik", "123456");
        Log.e("FLJ", "main opening");
        Alarms.init(getApplicationContext());
        SearchHistory.init(getApplicationContext());
//        Alarms.init(getApplicationContext());

        onSearchSubmitted(new SearchForm.EventOnSubmit(new JobFilter("php", "turkey", "istanbul")));
//        showFragment(new SearchForm());

//        bus.register(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }



    @Subscribe
    public void onSearchSubmitted(SearchForm.EventOnSubmit event) {
        JobList jobList = new JobList();
        Bundle bundle = new Bundle();
        bundle.putSerializable("jobFilter", event.filter);
        SearchHistory.insert(event.filter);
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

    @Subscribe
    public void onSignedIn(AccountManager.EventSuccessfulSignIn event) {
        Toast.makeText(this, "Welcome: " + event.account.getUsername(), Toast.LENGTH_LONG).show();
        //accountEmail.setText(AccountManager.getAuthenticatedAccount().getEmail());
        //accountName.setText(AccountManager.getAuthenticatedAccount().getUsername());
        //accountImage.setImageURI(Uri.parse(AccountManager.getAuthenticatedAccount().userprofile.getPicture()));
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();

            if (count > 1) {
                getSupportFragmentManager().popBackStack();
            }
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
//            Account.Manager.signinBasicAuth("kamil", "412");
            AccountManager.signinBasicAuth("muatik", "123456");
            Alarms.init(getApplicationContext());
        } else if (id == R.id.nav_gallery) {
            showFragment(new SearchForm());
        } else if (id == R.id.nav_slideshow) {
            showFragment(new SearchHistoryManager());
        } else if (id == R.id.nav_manage) {
            showFragment(new AlarmManager());
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
        super.onPause();
        SearchHistory.commit();
    }

    @Override
    protected void onStop() {
        BusManager.get().unregister(this);
        AccountManager.saveState();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        BusManager.get().register(this);
    }
}
