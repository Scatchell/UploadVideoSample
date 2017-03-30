package com.example.ascatche.uploadvideo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.IOException;

public class UploadActivity extends AppCompatActivity {

    private static final String CFG_BUCKET = "cfg-videos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void upload(View view) throws IOException {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "you WISH you had it!", // Identity Pool ID
                Regions.EU_WEST_2 // Region
        );

        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);

        // Initialize the Cognito Sync client
        TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());

        TransferObserver observer = transferUtility.upload(
                CFG_BUCKET,     /* The bucket to upload to */
                "test-item",    /* The key for the uploaded object */
//              ********* put whatever file you have on your device here (I think you can also try to upload files from the simulator but not 100% sure how to get them on there in the first place)
                new File("storage/emulated/0/Android/data/com.thoughtworks.cfgprototype/files/1490815461843.mp4")
        );

        observer.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                // do something
                Log.i("State changed", String.valueOf(id));
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent/bytesTotal * 100);
                Log.i("progress changed", bytesCurrent + " out of... " + bytesTotal);
                //Display percentage transfered to user
            }

            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
                Log.i("error!!!", ex.toString());
                // do something
            }

        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
