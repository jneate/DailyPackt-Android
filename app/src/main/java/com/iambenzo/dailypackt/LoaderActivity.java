package com.iambenzo.dailypackt;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.iambenzo.dailypackt.model.Packt;
import com.iambenzo.dailypackt.util.InvalidLoginException;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.NoSuchPaddingException;

import devliving.online.securedpreferencestore.SecuredPreferenceStore;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoaderActivity extends AppCompatActivity {
    private String user;
    private String pass;

    private static final int UI_ANIMATION_DELAY = 0; //300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loader);

        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

    }

    private void getPermissions() {
        String[] perms  = {"android.permission.WRITE_EXTERNAL_STORAGE"};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "We need to save collected book information.", 21, perms);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);

        try {
            SecuredPreferenceStore prefStore = SecuredPreferenceStore.getSharedInstance(getApplicationContext());
            user = prefStore.getString("email", null);
            pass = prefStore.getString("password", null);
        } catch (IOException | CertificateException | NoSuchAlgorithmException | InvalidKeyException
                | UnrecoverableEntryException | InvalidAlgorithmParameterException | NoSuchPaddingException
                | NoSuchProviderException | KeyStoreException e) {
            e.printStackTrace();
        }

        if(user == null){
            //login page
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        } else {
            getPermissions();
            new initTask().execute();
        }

    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private class initTask extends AsyncTask<Void, Void, Packt>{
        @Override
        protected Packt doInBackground(Void... voids) {
            try {
//                return new Packt();
                return new Packt(user, pass, getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (InvalidLoginException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Packt packt) {

            if(packt != null){

                Intent i = new Intent(getApplicationContext(), DailyActivity.class);
                i.putExtra("session", packt);
                startActivity(i);

            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(LoaderActivity.this);

                builder.setMessage("There was a problem connecting to Packt")
                        .setTitle("Error");

                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                });

                AlertDialog dialog = builder.create();

                dialog.show();
            }
        }
    }
}
