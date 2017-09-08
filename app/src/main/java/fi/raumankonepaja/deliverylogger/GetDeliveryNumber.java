package fi.raumankonepaja.deliverylogger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class GetDeliveryNumber extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_delivery_number);

        EditText deliveryNumber = (EditText) findViewById(R.id.editTextDeliveryNumber);
        EditText deliveryPosition = (EditText) findViewById(R.id.editTextDeliveryPosition);

        TextChangeListener textChangeListener = new TextChangeListener();

        // piilotetaan heti ekaksi  nappula
        showHideButton();

        deliveryNumber.addTextChangedListener(textChangeListener);
        deliveryPosition.addTextChangedListener(textChangeListener);


    }


    // tekstin muuttuja kuuntelija
    private class TextChangeListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            showHideButton();
        }
    }


    // metodi joka piilottaa kuvaa napin jos tietoja ei ole tarpeeksi
    private void showHideButton() {

        Button shootButton = (Button) findViewById(R.id.shootButton);
        EditText deliveryNumber = (EditText) findViewById(R.id.editTextDeliveryNumber);
        EditText deliveryPosition = (EditText) findViewById(R.id.editTextDeliveryPosition);

        if (deliveryNumber.getText().length() > 0 && deliveryPosition.getText().length() > 0) {
            shootButton.setVisibility(View.VISIBLE);
        } else {
            shootButton.setVisibility(View.INVISIBLE);
        }


    }
}
