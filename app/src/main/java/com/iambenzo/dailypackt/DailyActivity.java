package com.iambenzo.dailypackt;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iambenzo.dailypackt.model.Packt;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class DailyActivity extends AppCompatActivity {
    private Packt packt;
    private Button bookButton;
    private LinearLayout downloadLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        TextView bookTitle = (TextView) findViewById(R.id.bookTitle);
        TextView bookDescription = (TextView) findViewById(R.id.bookDescription);
        ImageView bookImage = (ImageView) findViewById(R.id.bookImage);
        Button epubButton = (Button)findViewById(R.id.dloadEpubButton);
        Button mobiButton = (Button)findViewById(R.id.dloadMobiButton);
        Button pdfButton = (Button)findViewById(R.id.dloadPdfButton);

        //Instance variables for Async Task visibility
        bookButton = (Button) findViewById(R.id.grabButton);
        downloadLayout = (LinearLayout) findViewById(R.id.downloadLayout);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            this.packt = (Packt)extras.get("session");

            bookTitle.setText(packt.getBookTitle());
            bookDescription.setText(packt.getBookDescription());
            Picasso.with(getApplicationContext()).load(packt.getBookImage()).into(bookImage);

            if(packt.isObtained(packt.getBookTitle())){
                bookButton.setVisibility(View.GONE);
                downloadLayout.setVisibility(View.VISIBLE);
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.mainLayout), "You already have this book", Snackbar.LENGTH_LONG);

                snackbar.show();
            }

            bookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new grabBookTask().execute();
                }
            });

            epubButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downloadBook(packt.getePubDownloadLink(packt.getNid()), ".epub");
                }
            });

            mobiButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downloadBook(packt.getMobiDownloadLink(packt.getNid()), ".mobi");
                }
            });

            pdfButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downloadBook(packt.getPdfDownloadLink(packt.getNid()), ".pdf");
                }
            });

        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

            builder.setMessage("There was a problem getting book info from Packt")
                    .setTitle("Error");

            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });

            AlertDialog dialog = builder.create();

            dialog.show();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.daily_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_prefs) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        }

        return true;
    }

    private void downloadBook(String url, String extension){
        //Set up the Android Download Manager
        DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        //Keep the notification visible once complete
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(packt.getBookTitle() + extension);

        //Add our session information
        request.setDescription("Packt Daily book download");
        request.addRequestHeader("Cookie", packt.getSession());

        downloadManager.enqueue(request);

        //Let the user know we did things

        Toast.makeText(this, "Download started", Toast.LENGTH_SHORT).show();

    }

    private class grabBookTask extends AsyncTask<Void, Void, Void>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(DailyActivity.this,
                    "Please Wait",
                    "Grabbing book...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                packt.saveBook();
                packt.addObtainedBook(packt.getBookTitle(), getApplicationContext());
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            bookButton.setVisibility(View.GONE);
            downloadLayout.setVisibility(View.VISIBLE);
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.mainLayout), "Book added to account", Snackbar.LENGTH_LONG);

            snackbar.show();
        }
    }
}
