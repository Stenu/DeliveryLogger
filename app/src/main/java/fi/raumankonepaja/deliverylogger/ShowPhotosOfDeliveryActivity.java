package fi.raumankonepaja.deliverylogger;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.FileProvider.getUriForFile;


public class ShowPhotosOfDeliveryActivity extends AppCompatActivity {

    private static final String TAG = "ShowPhotosOfDeliveryActivity";
    TextView mShowPhotosFromDeliveryTextView;
    int mDeliveryNumber;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ShowPhotosFromDeliveryRecyclerViewAdapter recyclerViewAdapter;
    private DatabaseReference databaseReference;
    private ArrayList<PhotoListItem> mPhotoListItems;
    private Task<Void> allTask;

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

        recyclerView = (RecyclerView) findViewById(R.id.photo_list);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);


        // -- get info from this delivery from firebase

        // get firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.orderByChild("deliveryNumber").equalTo(mDeliveryNumber).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                Log.i(TAG, dataSnapshot.getKey() + " -> " + dataSnapshot.getValue().toString());

                // firebase returns our custom class PhotoListItem!
                PhotoListItem photoListItem = dataSnapshot.getValue(PhotoListItem.class);

                mPhotoListItems.add(photoListItem);

                recyclerViewAdapter = new ShowPhotosFromDeliveryRecyclerViewAdapter(ShowPhotosOfDeliveryActivity.this, mPhotoListItems);
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

    // --- method that runs when email button is clicked
    // Downloads photos asynchonized. So first photos are downloaded to device, and
    // afterwards attached to email that is going to be sens.
    void sendEmail(View view) {
        Log.i(TAG, "Send email cliked!");


        // show toast
        Toast toast = Toast.makeText(this, "Hetki! Ladataan kuvia firebasesta", Toast.LENGTH_SHORT);
        toast.show();


        // We define final (unique) list that holds Uris from files we are going to attach to email.
        final ArrayList<Uri> attachmentUris = new ArrayList<>();


        // get firebase storage instance
        FirebaseStorage storage = FirebaseStorage.getInstance();


        // define list that store asynchronous tasks
        List<Task> tasks = new ArrayList<>();

        // define final (unique) list that stores photo files
        final ArrayList<File> photoFiles = new ArrayList<>();


        // **** loop every photolist item and download picture to file ***
        // ***********
        for (int i = 0; i < mPhotoListItems.size(); i++) {
            Log.i(TAG, "inside for loop run:" + Integer.toString(i + 1) + "/" + Integer.toString(mPhotoListItems.size()));


            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();

            // Create a reference that points to firebase storage file that holds photo to be downloaded...
            StorageReference photoPathReference = storageRef.child("images/" + mPhotoListItems.get(i).getPictureFileName());


            // Create new file to hold photo on device
            File tempFile = null;


            try {
                Log.i(TAG, "Created tempFile");

                // call method that returns new file
                tempFile = createImageFile();

            } catch (Exception e) {
                Log.e(TAG, " Error on createTempFile!!");
                // Terminate if we cant get new file
                break;
            }

            // get a task instance that runs asynchonously (separately)
            // this task runs separately from this "main program"
            Task task = photoPathReference.getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //this runs when this separately run task finishes succesfully
                    Log.i(TAG, "file download success!");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //these runs if this separately run task fails
                    Log.i(TAG, "Task failed to donwload file and add uri to attachmentUris...");

                    // show toast
                    Toast toast = Toast.makeText(ShowPhotosOfDeliveryActivity.this, "Virhe ladattaessa kuvaa", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

            // add this task to list that holds all download task
            tasks.add(task);

            // add file information list
            photoFiles.add(tempFile);

        }

        // *** loop ends (get download task for one photo)
        // ***

        // Print information about tasks to console
        Log.i(TAG, "Do we have tasks???:");
        Log.i(TAG, "tasks: " + tasks.toString());


        try { // --- do after all files are downloaded from firebase storage

            //get helper that holds information of all tasks
            allTask = Tasks.whenAll((List) tasks);

            //run this after all file download tasks are finished
            allTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    // get Uris from downloaded files
                    for (File file : photoFiles) {
                        attachmentUris.add(getUriForFile(ShowPhotosOfDeliveryActivity.this, "fi.raumankonepaja.deliverylogger.fileprovider", file));
                    }

                    // print to console about uris
                    Log.i(TAG, "Uris when all success:");
                    Log.i(TAG, attachmentUris.toString());

                    // now we have URI's of photos on attachemntUris ArrayList..

                    // launch email intent
                    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_EMAIL, "");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Läheteluettelon " + Integer.toString(mDeliveryNumber) + " kuvat");
                    intent.putExtra(Intent.EXTRA_STREAM, attachmentUris);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);

                    }


                }
            });
            allTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    // apologize profusely to the user!
                    Log.e(TAG, " ERROR ON ALL TASKS!");

                    // show toast
                    Toast toast = Toast.makeText(ShowPhotosOfDeliveryActivity.this, "Virhe ladattaessa kuvia!", Toast.LENGTH_SHORT);
                    toast.show();

                }
            });

            Log.i(TAG, attachmentUris.toString());
        } catch (Exception e) {
            Log.e(TAG, "TASKS ALL WAIT CRASH");
        }

    }


    // this method creates and returns new imagefile
    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_" + "IMG_";

        //print to console
        Log.i(TAG, "pictures directory is: " + Environment.DIRECTORY_PICTURES.toString());

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }


}
