package fi.raumankonepaja.deliverylogger;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    @Override
    protected void onStart() {
        super.onStart();




    }

    /**
     * Called when the user taps the "photo delivery" - button
     */
    public void shootDelivery(View view) {
        Intent intent = new Intent(this, TakePhotoFromDelivery.class);


        startActivity(intent);
    }

    /**
     * Called when the user taps the "list deliveries" - button
     */
    public void listDeliveries(View view) {
        Intent intent = new Intent(this, ListDeliveriesActivity.class);


        startActivity(intent);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        File photoDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        boolean success = MyHelper.deleteDirectory(photoDirectory);

        if (success) {
            Log.i(TAG, "Files deleted!");
        } else {
            Log.i(TAG, "Error on files deletion");
        }



    }






}
