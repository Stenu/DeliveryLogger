package fi.raumankonepaja.deliverylogger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    /** Called when the user taps the "photo delivery" - button */
    public void shootDelivery(View view) {
        Intent intent = new Intent(this, GetDeliveryNumber.class);


        startActivity(intent);
    }


}
