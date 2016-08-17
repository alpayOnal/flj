package com.muatik.flj.flj.UI.activities;

import android.view.MenuItem;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.fragments.SearchForm;

/**
 * Created by muatik on 17.08.2016.
 */
public class DetailActivity extends BaseActivity {
    @Override
    protected void init() {
        super.init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up signin_button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
