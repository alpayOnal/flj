package com.muatik.flj.flj.UI.views;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.utilities.BusManager;

/**
 * Created by muatik on 25.07.2016.
 */
public class SearchForm {
    EditText keyword;
    TextView location;

    private static SearchForm instance;

    public static SearchForm get() {
        return instance;
    }

    public SearchForm(View view) {
        keyword = (EditText) view.findViewById(R.id.search_keyword);
        location = (TextView) view.findViewById(R.id.search_location);
        instance = this;
        bind();
        BusManager.get().post(new SearchFormChanged(this));
    }


    public class SearchFormChanged{
        public SearchForm form;
        public SearchFormChanged(SearchForm form) {
            this.form = form;
        }
    }


    private void bind() {
        final SearchForm that = this;
        keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                BusManager.get().post(new SearchFormChanged(that));
            }
        });
    }

    public String getKeyword() {
        return keyword.getText().toString();
    }

    public String getLocation() {
        return location.getText().toString();
    }
}
