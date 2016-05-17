package com.example.pc_3.uploader.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pc_3.uploader.R;

import org.apache.http.HttpResponse;

import java.io.File;
import java.io.IOException;

import api.ApiWrapper;
import api.Endpoints;
import api.Params;
import api.Request;
import api.Token;

public class MainActivity extends AppCompatActivity {

    Button uploadButton;

    private Token token;
    private ApiWrapper wrapper;
    private File file;

    private int resultCode = 0;
    private String errorString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
    }

    private void initComponents() {
        uploadButton = (Button) findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(clickListener);
    }

    private void upload() {

        try {
            Log.d(Constants.LOG_TAG, "Uploading in background");

            file = new File(Constants.PATH);
            file.setReadable(true, false);

            HttpResponse response = wrapper.post(Request.to(Endpoints.TRACKS)
                    .add(Params.Track.TITLE, "TestSound.mp3")
                    .add(Params.Track.TAG_LIST, "demo upload")
                    .withFile(Params.Track.ASSET_DATA, file));

            resultCode = Integer.valueOf(response.getStatusLine().getStatusCode());

            Log.i(Constants.LOG_TAG, "....." + resultCode);
            Log.d(Constants.LOG_TAG, "Background thread done!");

            //Toast.makeText(MainActivity.this, "Status code = " + resultCode, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.d(Constants.LOG_TAG, "Uploading error: " + e.toString());

            errorString = e.toString();
        }
    }

    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Log.d(Constants.LOG_TAG, "Click");
            Toast.makeText(MainActivity.this, "Click", Toast.LENGTH_SHORT).show();

            new UploadTask().execute();

        }
    };

    private class UploadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void...params) {

            try {
                Log.d(Constants.LOG_TAG, "Background thread started!");
                //Toast.makeText(MainActivity.this, "Background thread started!", Toast.LENGTH_SHORT).show();

                wrapper = new ApiWrapper(Constants.CLIENT_ID, Constants.CLIENT_SECRET, null, null);
                token = wrapper.login(Constants.USER_NAME, Constants.USER_PASSWORD);

                upload();

            } catch (IOException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(Constants.LOG_TAG, "Got result");

            Toast.makeText(MainActivity.this, "Got result, error string = " + errorString, Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, "Status code = " + resultCode, Toast.LENGTH_SHORT).show();
        }
    }
}
