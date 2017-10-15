package fi.raumankonepaja.deliverylogger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListDeliveriesActivity extends AppCompatActivity {

    private static final String TAG = "List Deliveries Act.";
    // this must be outside from for loop below
    List<String> avaimet = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapter recyclerViewAdapter;
    private DatabaseReference databaseReference;
    private List<ListItem> mListItems;
    private EditText searchForDeliveryEditText;
    private Button searchForDeliveryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_deliveries);

        searchForDeliveryEditText = (EditText) findViewById(R.id.searchForDeliveryEditText);
        searchForDeliveryButton = (Button) findViewById(R.id.searchForDeliveryButton);

        // intialize array list
        mListItems = new ArrayList<ListItem>();

        // get firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();


        recyclerView = (RecyclerView) findViewById(R.id.log_entries_list);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);


        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getAllDeliveries(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getAllDeliveries(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //taskDeletion(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void getAllDeliveries(DataSnapshot dataSnapshot) {


        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

            if (singleSnapshot.getKey().equals("deliveryNumber") && !avaimet.contains(singleSnapshot.getValue().toString())) {

                mListItems.add(new ListItem(Integer.parseInt(singleSnapshot.getValue().toString())));
                avaimet.add(singleSnapshot.getValue().toString());
            }

            // sort recycledviews list items on reverse order
            Collections.reverse(mListItems);

            recyclerViewAdapter = new RecyclerViewAdapter(ListDeliveriesActivity.this, mListItems);
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    }

    public void searchDelivery(View v) {

        Log.i(TAG, "Search button pressed!");
        Log.i(TAG, "editText contains: " + searchForDeliveryEditText.getText());

        int deliveryNumber = 0;

        if (!searchForDeliveryEditText.getText().toString().isEmpty()) {
            deliveryNumber = Integer.parseInt(searchForDeliveryEditText.getText().toString());
        } else {
            // if there is no delivery number to search for, we can quit now
            return;
        }

        Log.i(TAG, "searching delivery no" + Integer.toString(deliveryNumber));
        Log.i(TAG, "From: " + mListItems);

        boolean sisaltaa = false;
        for (ListItem listItem : mListItems) {
            if (listItem.getDeliveryNumber() == deliveryNumber) {
                sisaltaa = true;
                Intent intent = new Intent(v.getContext(), ShowPhotosOfDelivery.class);
                intent.putExtra("EXTRA_DELIVERY_NUMBER", deliveryNumber);
                v.getContext().startActivity(intent);
            }
        }

        if (!sisaltaa) {
            // show toast
            Toast toast = Toast.makeText(this, "Lähetettä numero\n" + Integer.toString(deliveryNumber) + "\nei löydy.", Toast.LENGTH_SHORT);
            toast.show();
        }


    }


}
