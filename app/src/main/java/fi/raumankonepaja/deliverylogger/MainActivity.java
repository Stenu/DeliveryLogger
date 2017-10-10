package fi.raumankonepaja.deliverylogger;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.apache.commons.io.FileUtils;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    /**
     * Called when the user taps the "photo delivery" - button
     */
    public void shootDelivery(View view) {
        Intent intent = new Intent(this, TakePhotoFromDelivery.class);


        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


//        try {
//            if (getExternalFilesDir(Environment.DIRECTORY_PICTURES) != null) {
//                Log.i(TAG, "Deleting files");
//                FileUtils.cleanDirectory(getExternalFilesDir(Environment.DIRECTORY_PICTURES));
//            }
//        } catch (Exception e) {
//
//        }


    }
}
