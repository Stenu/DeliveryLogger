package fi.raumankonepaja.deliverylogger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import java.util.List;
import java.util.Map;

public class ListDeliveriesActivity extends AppCompatActivity {

    private static final String TAG = "List Deliveries Act.";
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapter recyclerViewAdapter;
    private DatabaseReference databaseReference;
    private List<ListItem> mListItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_deliveries);

        // intialize array list
        mListItems = new ArrayList<ListItem>();

        // get firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();


        recyclerView = (RecyclerView)findViewById(R.id.log_entries_list);

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

    // this must be outside from for loop below
    List<String> avaimet = new ArrayList<>();

    private void getAllDeliveries(DataSnapshot dataSnapshot){



        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

            if(singleSnapshot.getKey().equals("deliveryNumber") && !avaimet.contains(singleSnapshot.getValue().toString())) {

                mListItems.add(new ListItem(Integer.parseInt(singleSnapshot.getValue().toString())));
                avaimet.add(singleSnapshot.getValue().toString());}


            recyclerViewAdapter = new RecyclerViewAdapter(ListDeliveriesActivity.this, mListItems);
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    }

}
