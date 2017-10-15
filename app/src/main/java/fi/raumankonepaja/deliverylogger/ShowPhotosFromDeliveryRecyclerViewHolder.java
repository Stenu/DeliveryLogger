package fi.raumankonepaja.deliverylogger;

/**
 * Created by Sami on 11.10.2017.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

import static android.support.v4.content.FileProvider.getUriForFile;


public class ShowPhotosFromDeliveryRecyclerViewHolder extends RecyclerView.ViewHolder{

    private static final String TAG = "Recycler View Holder";
    public TextView mPhotoInfo;
    public ImageView mPhotoOnListImageView;
    private List<PhotoListItem> mPhotoListItems;




    public ShowPhotosFromDeliveryRecyclerViewHolder  (final View itemView, final List<PhotoListItem> mPhotoListItems) {

        super(itemView);
        this.mPhotoListItems = mPhotoListItems;

        mPhotoInfo = (TextView) itemView.findViewById(R.id.photoInfo);
        mPhotoOnListImageView = (ImageView) itemView.findViewById(R.id.PhotoOnListImageView);


        mPhotoOnListImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                // this runs when user clicks thumbail of photo
                // Download image from firebase -> pass it to device gallery app
                // (ShowOnePhoto -Activity is not needed anymore...)

                String mPictureFileName = mPhotoListItems.get(getAdapterPosition()).getPictureFileName();

                // --- get photo from firebase ---

                FirebaseStorage storage = FirebaseStorage.getInstance();

                // Create a storage reference from our app
                StorageReference storageRef = storage.getReference();

                // Create a reference with an initial file path and name
                StorageReference photoPathReference = storageRef.child("images/" + mPictureFileName);

                final File localFile;
                try {

                    File storageDir = v.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    localFile = File.createTempFile(
                            "tempFile",  /* prefix */
                            ".jpg",         /* suffix */
                            storageDir      /* directory */);

                } catch (Exception e) {
                    return; // if we cant create file -> we cant show it on gallery
                }


                photoPathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        Context context = v.getContext();

                        Uri localFileUri = getUriForFile(context, "fi.raumankonepaja.deliverylogger.fileprovider", localFile);

                        Log.i(TAG, "Open photo in gallery :" + localFileUri.toString());

                        Intent intent = new Intent(Intent.ACTION_VIEW, localFileUri);

                        // This must be set. Otherwise gallery wont find file...
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


                        context.startActivity(intent);
                    }
                });



            }
        });

    }


}