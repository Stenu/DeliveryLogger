package fi.raumankonepaja.deliverylogger;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class TakePhotoFromDelivery extends AppCompatActivity {

    private static final String TAG = "TakePhotoFromDelivery";
    private static final String KEY_M_CURRENT_PHOTO_PATH = "mCurrentPhotoPath";
    private static final String KEY_M_LAST_CURRENT_PHOTO_PATH = "mLastCurrentPhotoPath";
    private boolean pictureSent;

    Uri photoURI;
    String mCurrentPhotoPath;
    String mLastCurrentPhotoPath;

    EditText deliveryNumber;
    EditText deliveryPosition;
    Button shootButton;
    ImageView takenPictureImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo_from_delivery);

        deliveryNumber = (EditText) findViewById(R.id.editTextDeliveryNumber);
        deliveryPosition = (EditText) findViewById(R.id.editTextDeliveryPosition);
        shootButton = (Button) findViewById(R.id.shootButton);
        takenPictureImageView = (ImageView) findViewById(R.id.takenPictureImageView);

        pictureSent = false;


        TextChangeListener textChangeListener = new TextChangeListener();


        // piilotetaan heti ekaksi  nappula
        showHideButton();

        deliveryNumber.addTextChangedListener(textChangeListener);
        deliveryPosition.addTextChangedListener(textChangeListener);

        // ei hukkaa kuvaa orientaatiota vaihdettaessa
        if (savedInstanceState != null) {
            mCurrentPhotoPath = savedInstanceState.getString(KEY_M_CURRENT_PHOTO_PATH);
            mLastCurrentPhotoPath = savedInstanceState.getString(KEY_M_LAST_CURRENT_PHOTO_PATH);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        // tarvittaessa upataan tiedot firebaseen
        if(mCurrentPhotoPath != null){
            File photoFile = new File (mCurrentPhotoPath);
            Log.i(TAG, "onResume metodissa katsomassa onko kuva vaihtunut");
            Log.i(TAG, "tiedoston koko:" + Long.toString(photoFile.length()));

            if(photoFile.length() != 0 && mCurrentPhotoPath != mLastCurrentPhotoPath ){
                mLastCurrentPhotoPath = mCurrentPhotoPath;

                uploadDataToFirebase();
            }

            // jos on palattu kuvaus intentistä ilman kuvaa niin ei hukata viimeistä otettua kuvaa
            if(photoFile.length() == 0){
                mCurrentPhotoPath = mLastCurrentPhotoPath;
            }

        }




        // vaihdetaan kuva samalla imgavieviin
        Log.d(TAG, " onResume() - metodissa");

        if (mCurrentPhotoPath != null) {

            takenPictureImageView.setVisibility(View.VISIBLE);

            Bitmap takenPictureImage = (BitmapFactory.decodeFile(mCurrentPhotoPath));
            takenPictureImageView.setImageBitmap(takenPictureImage);

            // katsotaan missä asennossa kuva on otettu ja käännetään
            // imageview oikeaan asentoon

            try {
                Log.i(TAG, " ExifInterface kohdassa");

                ExifInterface exif = new ExifInterface(mCurrentPhotoPath.toString());

                int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotation = 0;

                if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
                    rotation = 90;
                } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
                    rotation = 180;
                } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
                    rotation = 270;
                }

                takenPictureImageView.setRotation(rotation);

            } catch (Exception e) {
                Log.e(TAG, "exifInterface lataus ei onnistunut");
                Log.e(TAG, e.toString());
                Log.e(TAG, "mCurrentPhotoPath :" + mCurrentPhotoPath.toString());
            }


        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.i(TAG, "onSaveInstanceState - metodissa");

        outState.putString(KEY_M_CURRENT_PHOTO_PATH, mCurrentPhotoPath);

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


        if (deliveryNumber.getText().length() > 0 && deliveryPosition.getText().length() > 0) {
            shootButton.setVisibility(View.VISIBLE);
            Log.i(TAG, "showHideButton metodi: näytä nappi");
        } else {
            shootButton.setVisibility(View.INVISIBLE);
        }


    }


    // tämä metodi laukaisee intentin, jossa kamera-apilla otetaan kuva
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
                //    Uri photoURI = FileProvider.getUriForFile(this,
                photoURI = FileProvider.getUriForFile(this,
                        "fi.raumankonepaja.deliverylogger.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);


            }
        }
    }


    // tässä luodaan uusi tiedosto, johon kuva sitten tallennetaan...
    private File createImageFile() throws IOException {
        // Create an image file name
        // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        // String imageFileName = "JPEG_" + timeStamp + "_";
        String imageFileName = "JPEG_" + "kuva1";
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

    private void uploadDataToFirebase (){

        Toast toast = Toast.makeText(this," FIREBASE lähetys", Toast.LENGTH_SHORT);
        toast.show();

    }


}
