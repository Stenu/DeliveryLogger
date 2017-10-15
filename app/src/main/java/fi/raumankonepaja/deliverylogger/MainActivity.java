package fi.raumankonepaja.deliverylogger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;


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




     try {

         // todo 1 delete photos from phone after app is closed
//           if (getExternalFilesDir(DIRECTORY_PICTURES) != null) {
//               Log.i(TAG, "Deleting files!:  "+ getFilesDir() +"/"+Environment.DIRECTORY_PICTURES);
//          //     FileUtils.cleanDirectory(getExternalFilesDir(Environment.DIRECTORY_PICTURES));
//               FileUtils.deleteDirectory(new File(getFilesDir() +"/"+Environment.DIRECTORY_PICTURES));
//          //     getFilesDir().deleteOnExit();
//
//           }

       } catch (Exception e) {
     }


    }






}
