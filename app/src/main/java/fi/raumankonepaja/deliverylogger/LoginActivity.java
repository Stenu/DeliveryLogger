package fi.raumankonepaja.deliverylogger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;

    private EditText enteredEmailEditText;
    private EditText enteredPasswordEditText;
    private TextView userInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        enteredEmailEditText = (EditText) findViewById(R.id.loginEmailEditText);
        enteredPasswordEditText = (EditText) findViewById(R.id.loginPasswordEditText);
        userInfoTextView = (TextView) findViewById(R.id.userNameTextView);

        mAuth = FirebaseAuth.getInstance();

        showHideUserInfo();

        // todo 3 do we need mAuth.onAuthStateChangedListener ?


    }

    @Override
    protected void onResume() {
        super.onResume();

        showHideUserInfo();

    }

    /**
     * Called when the user taps the " login"- button
     */
    public void doLogin(View view) {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        // if user is not logged in lets try email & pass
        if (currentUser == null) {

            String email = enteredEmailEditText.getText().toString();
            String pass = enteredPasswordEditText.getText().toString();

            if (!email.isEmpty() && !pass.isEmpty()) {

                mAuth.signInWithEmailAndPassword(email, pass);
            }
        }

        // if we are now logged in (or were logged in)
        if (currentUser != null) {

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Called when the user taps the "logoff" - button
     */
    public void doLogoff(View view) {
        mAuth.signOut();
        finish();
    }

    private void showHideUserInfo() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            userInfoTextView.setText("");
            userInfoTextView.setVisibility(View.VISIBLE);
        } else {
            userInfoTextView.setText("Logged in as " + currentUser.getEmail());

        }
    }

}
