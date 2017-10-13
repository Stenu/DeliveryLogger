package fi.raumankonepaja.deliverylogger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ShowOnePhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_one_photo);

        int mDeliveryNumber = getIntent().getIntExtra("EXTRA_DELIVERY_NUMBER",0);
        int mPosition = getIntent().getIntExtra("EXTRA_DELIVERY_POSITION",0);
        String mPictureFileName = getIntent().getStringExtra("EXTRA_PICTURE_FILE_NAME");

        TextView mShowOnePhotoTextView = (TextView) findViewById(R.id.showOnePhotoTextView);
        final ImageView mShowOnePhotoImageView = (ImageView) findViewById(R.id.ShowOnePhotoImageView);


        // add photo info
        mShowOnePhotoTextView.setText("Lähetenumero "+Integer.toString(mDeliveryNumber)+" pos "+Integer.toString(mPosition));


        // tässä pitää hakea kuva firebasesta
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference with an initial (thumbnail)file path and name
        StorageReference photoPathReference = storageRef.child("images/"+mPictureFileName);

        final long ONE_MEGABYTE = 1024 * 1024;

        photoPathReference.getBytes(10*ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap photo = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                mShowOnePhotoImageView.setImageBitmap(photo);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });



    }



}
