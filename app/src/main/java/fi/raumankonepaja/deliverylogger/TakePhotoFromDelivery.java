package fi.raumankonepaja.deliverylogger;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

// todo 1 Find out why files are not stored locally anymore.
// (they dont need to be stored locally.... but why they are not saved??)


public class TakePhotoFromDelivery extends AppCompatActivity {

    private static final String TAG = "TakePhotoFromDelivery";
    private static final String KEY_M_CURRENT_PHOTO_PATH = "mCurrentPhotoPath";
    private static final String KEY_M_LAST_CURRENT_PHOTO_PATH = "mLastCurrentPhotoPath";

    // introduce class variables
    Uri mPhotoURI;
    String mCurrentPhotoPath;
    String mLastCurrentPhotoPath;

    // introduce variables from layout components
    EditText mDeliveryNumber;
    EditText mDeliveryPosition;
    Button mTakePictureButton;
    Button mShowPhotosButton;
    ImageView mTakenPictureImageView;

    UploadTask mUploadTask; // firebase upload task (storage)
    List<ListItem> mListItems;
    List<String> avaimet;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo_from_delivery);

        // set class variables
        mDeliveryNumber = (EditText) findViewById(R.id.editTextDeliveryNumber);
        mDeliveryPosition = (EditText) findViewById(R.id.editTextDeliveryPosition);
        mTakePictureButton = (Button) findViewById(R.id.shootButton);
        mShowPhotosButton = (Button) findViewById(R.id.showPhotosButton);
        mTakenPictureImageView = (ImageView) findViewById(R.id.takenPictureImageView);

        mLastCurrentPhotoPath = null;

        // instantiate lists
        mListItems = new ArrayList<>();
        avaimet = new ArrayList<>();



        // hide mTakePictureButton  until the necessary information is provided
        showHideButton(); // checks that correct buttons are visible

        //hide mTakenPictureImageView because the photo is not taken
        mTakenPictureImageView.setVisibility(View.INVISIBLE);

        // instantiate textChangeListener ...
        TextChangeListener textChangeListener = new TextChangeListener();
        // ... and put it on both editable textview
        mDeliveryNumber.addTextChangedListener(textChangeListener);
        mDeliveryPosition.addTextChangedListener(textChangeListener);

        // don't lose data on orientation change
        if (savedInstanceState != null) {
            mCurrentPhotoPath = savedInstanceState.getString(KEY_M_CURRENT_PHOTO_PATH);
            mLastCurrentPhotoPath = savedInstanceState.getString(KEY_M_LAST_CURRENT_PHOTO_PATH);
        }


        // intialize firebase data connection
        databaseReference = FirebaseDatabase.getInstance().getReference();

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

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }



    @Override
    protected void onResume() {
        super.onResume();

        // *** print log message
        Log.i(TAG, " onResume() - metodissa");

        // check that correct buttons are visible
        showHideButton();

        // check if there is photo taken
        if (mCurrentPhotoPath != null) {

            // create file to memory
            File photoFile = new File(mCurrentPhotoPath);

            // *** print log message
            Log.i(TAG, "onResume metodissa katsomassa onko kuva vaihtunut");
            Log.i(TAG, "tiedoston koko:" + Long.toString(photoFile.length()));


            // if there is data in photo file and data is not old
            if (photoFile.length() != 0 && !mCurrentPhotoPath.equals(mLastCurrentPhotoPath)) {

                // *** print log message
                Log.i(TAG, "kutsutaan upload to firebase");
                Log.i(TAG, "mCurrentPhotoPath     =" + mCurrentPhotoPath.toString());
                if (mLastCurrentPhotoPath != null) {
                    Log.i(TAG, "mLastCurrentPhotoPath =" + mLastCurrentPhotoPath.toString());
                }


                // tell that data is old
                mLastCurrentPhotoPath = mCurrentPhotoPath;

                // call upload method
                uploadDataToFirebase();
            }

            // don't lose photo if returned from "take photo" -intent without photo
            if (photoFile.length() == 0) {
                mCurrentPhotoPath = mLastCurrentPhotoPath;
            }

            // put photo to mTakenPictureImageView

            mTakenPictureImageView.setVisibility(View.VISIBLE);
            Bitmap takenPictureImage = (BitmapFactory.decodeFile(mCurrentPhotoPath));
            mTakenPictureImageView.setImageBitmap(takenPictureImage);

            // MyHelper - is a class that holds static methods that are needed on app..
            // todo 2 get rotation and save it to custom metainformation to tuhmbail on uploadtofirebase method...
            MyHelper.matchViewToPhotoOrientation(mTakenPictureImageView,mCurrentPhotoPath);


        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // print console
        Log.i(TAG, "on onSaveInstanceState -methdod");

        // save information during activity lifecycle
        outState.putString(KEY_M_CURRENT_PHOTO_PATH, mCurrentPhotoPath);
        outState.putString(KEY_M_LAST_CURRENT_PHOTO_PATH, mLastCurrentPhotoPath);

    }

    // text change listener class for hiding mTakePictureButton
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




    // **** hides/shows mTakePictureButton
    private void showHideButton() {

        // if deliverunumber is entered on layout ...
        if (mDeliveryNumber.getText().length() > 0) {

            // ...and position is also entered -> show take picture button
            if (mDeliveryPosition.getText().length() > 0) {
                mTakePictureButton.setVisibility(View.VISIBLE);
                Log.i(TAG, "show Take Picture button");
                Log.i(TAG, " ");
            } else {
                //... and if position is not entered -> hide show picture button
                mTakePictureButton.setVisibility(View.INVISIBLE);
                Log.i(TAG, "hide Take Picture button");
            }

            // checks that if typed deliverynumber is exist on firebase database
            int deliveryNumberAtTheMoment = Integer.parseInt(mDeliveryNumber.getText().toString());
            if (hasDeliveryInformation(deliveryNumberAtTheMoment)) {

                // if deliverynumber exists show show_photos_button
                mShowPhotosButton.setVisibility(View.VISIBLE);
            } else {
                //// if deliverynumber is not exist hide show_photos_button
                mShowPhotosButton.setVisibility(View.INVISIBLE);
            }

        } else { // no deliverynumber -> hide both buttons
            mShowPhotosButton.setVisibility(View.INVISIBLE);
            mTakePictureButton.setVisibility(View.INVISIBLE);
            Log.i(TAG, "hide Take Picture button");
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

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // this method launches androids camera intent
    static final int REQUEST_TAKE_PHOTO = 1;

    public void takePicture(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "ongelmia " + ex.getMessage().toString());

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                mPhotoURI = FileProvider.getUriForFile(this,
                        "fi.raumankonepaja.deliverylogger.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);


            }
        }
    }





    // this method uploads data to firebase (works but could be improved...)
    private void uploadDataToFirebase() {

        int deliveryInt = Integer.parseInt(mDeliveryNumber.getText().toString());
        int positionInt = Integer.parseInt(mDeliveryPosition.getText().toString());

        String pictureFileName = mPhotoURI.getLastPathSegment();

        // get photo time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());

        // show toast
        Toast toast = Toast.makeText(this, "Siirretään tiedot\n" +
                "lähetyslista:" + Integer.toString(deliveryInt) +
                " \npositio:" + Integer.toString(positionInt), Toast.LENGTH_SHORT);
        toast.show();


        // firebase database upload (data)

        LogEntry logEntry = new LogEntry(deliveryInt, positionInt, pictureFileName, currentDateandTime);
        databaseReference.push().setValue(logEntry);

        // firebase storage upload (photo - jpg)
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        //create reference to images folder and assing a name to the file that will be uploaded
        StorageReference imageRef = storageRef.child("/images/" + pictureFileName);

        if (mPhotoURI != null) {
            mUploadTask = imageRef.putFile(mPhotoURI);
        }

        //now we make thumbnail from picture
        //we are decoding bitmap for 2nd time... improve code here
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mCurrentPhotoPath), 640, 640);

        //create reference to images folder and assing a name to the file that will be uploaded
        imageRef = storageRef.child("/images/thumbnail_" + pictureFileName);

        // prepare thumbail to be send
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] thumbnailData = baos.toByteArray();

        mUploadTask = imageRef.putBytes(thumbnailData);


    }



    private void getAllDeliveries(DataSnapshot dataSnapshot) {

        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

            if (singleSnapshot.getKey().equals("deliveryNumber") && !avaimet.contains(singleSnapshot.getValue().toString())) {

                mListItems.add(new ListItem(Integer.parseInt(singleSnapshot.getValue().toString())));
                avaimet.add(singleSnapshot.getValue().toString());
            }

            Collections.reverse(mListItems);

        }
    }

    // check that if the list contains information about deliverynumber
    private boolean hasDeliveryInformation(int delNumber) {

        for (ListItem item : mListItems) {
            if (item.getDeliveryNumber() == delNumber) {
                return true;
            }
        }
        return false;
    }

    public void showPhotos(View view) {

        Intent intent = new Intent(view.getContext(), ShowPhotosOfDelivery.class);
        intent.putExtra("EXTRA_DELIVERY_NUMBER", Integer.parseInt(mDeliveryNumber.getText().toString()));
        view.getContext().startActivity(intent);
    }



}
