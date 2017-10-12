package fi.raumankonepaja.deliverylogger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ShowPhotosOfDelivery extends AppCompatActivity {

    private static final String TAG = "ShowPhotosOfDelivery";
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ShowPhotosFromDeliveryRecyclerViewAdapter recyclerViewAdapter;
    TextView mShowPhotosFromDeliveryTextView;
    private DatabaseReference databaseReference;

    private ArrayList<PhotoListItem> mPhotoListItems;

    int mDeliveryNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photos_of_delivery);

        mShowPhotosFromDeliveryTextView = (TextView) findViewById(R.id.showPhotosFromDeliveryTitleTextView);

        // if we are here and for somereasen dont have delivery number, activity stops
        if (!getIntent().hasExtra("EXTRA_DELIVERY_NUMBER")) {
            finish();
        }

        mPhotoListItems = new ArrayList<>();

        mDeliveryNumber = getIntent().getIntExtra("EXTRA_DELIVERY_NUMBER", 0);

        mShowPhotosFromDeliveryTextView.setText("LÄHETE " + Integer.toString(mDeliveryNumber));

        recyclerView = (RecyclerView)findViewById(R.id.photo_list);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);


        // haetaan firebasesta tämän lähetyksen tiedot jonnekkin eka
        // get firebase database reference

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.orderByChild("deliveryNumber").equalTo(mDeliveryNumber).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                Log.i(TAG, dataSnapshot.getKey() +  " -> " + dataSnapshot.getValue().toString());

                //   int deliveryPos = dataSnapshot.getValue("deliveryPos");
                PhotoListItem photoListItem = dataSnapshot.getValue(PhotoListItem.class);

                mPhotoListItems.add(photoListItem);


                recyclerViewAdapter = new ShowPhotosFromDeliveryRecyclerViewAdapter(ShowPhotosOfDelivery.this, mPhotoListItems);
                recyclerView.setAdapter(recyclerViewAdapter);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }




}
