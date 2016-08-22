package com.muatik.flj.flj.UI.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.entities.AccountManager;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by muatik on 08.08.2016.
 */
public class MainNavProfile {

    static WeakReference<ImageView> profileImage;

    public static void init(View headerLayout) {

        TextView accountName = (TextView) headerLayout.findViewById(R.id.account_name);
        TextView accountEmail = (TextView) headerLayout.findViewById(R.id.account_email);
        profileImage = new WeakReference<ImageView>(
                (ImageView) headerLayout.findViewById(R.id.account_image));
        accountName.setText(AccountManager.getAuthenticatedAccount().getDisplayName());
        accountEmail.setText(AccountManager.getAuthenticatedAccount().getEmail());
        if (!AccountManager.getAuthenticatedAccount().userprofile.getPicture().isEmpty()){
            try {
                new LoadProfilePhoto().execute(new URL(
                        AccountManager.getAuthenticatedAccount().userprofile.getPicture()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            profileImage.get().setImageResource(R.drawable.photo);
        }
    }

    static class LoadProfilePhoto extends AsyncTask<URL, Bitmap, Bitmap> {
        protected Bitmap doInBackground(URL... urls) {
            Bitmap a = null;

            try {
                a = BitmapFactory.decodeStream(urls[0].openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return a;
        }

        protected void onPostExecute(Bitmap result) {
            Log.e("flj", "onPostExecute");
            if (profileImage.get() != null)
                profileImage.get().setImageBitmap(result);
        }
    }


}
