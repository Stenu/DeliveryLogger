package fi.raumankonepaja.deliverylogger;

/**
 * Created by Sami on 11.10.2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ShowPhotosFromDeliveryRecyclerViewAdapter extends RecyclerView.Adapter<ShowPhotosFromDeliveryRecyclerViewHolder> {

    private static final String TAG = "Recycler View Adapter";
    protected Context mContext;
    private List<PhotoListItem> mPhotoListItems;

    public ShowPhotosFromDeliveryRecyclerViewAdapter(Context mContext, List<PhotoListItem> mPhotoListItems) {
        this.mContext = mContext;
        this.mPhotoListItems = mPhotoListItems;
    }

    @Override
    public ShowPhotosFromDeliveryRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       ShowPhotosFromDeliveryRecyclerViewHolder viewHolder = null;

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_photos_from_delivery_row, parent, false);
        viewHolder = new ShowPhotosFromDeliveryRecyclerViewHolder(layoutView, mPhotoListItems);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(final ShowPhotosFromDeliveryRecyclerViewHolder holder, final int position) {

       //print log
        Log.i(TAG, "Binding holder type 2------");



        // get and modify date and time data
        String dateAndTime = mPhotoListItems.get(position).getDateAndTime();
        String date = dateAndTime.substring(6,8);
        String month = dateAndTime.substring(4,6);
        String year = dateAndTime.substring(0,4);
        String hour = dateAndTime.substring(9,11);
        String minut = dateAndTime.substring(11,13);

       String dateString = (date+"."+month+"."+year+" "+hour+":"+minut);

        holder.mPhotoInfo.setText("Pos: "+Integer.toString(mPhotoListItems.get(position).deliveryPos)+" " + dateString);


        // -- get photo from firebase storage
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference with an initial (thumbnail)file path and name
        StorageReference photoPathReference = storageRef.child("images/thumbnail_"+mPhotoListItems.get(position).getPictureFileName());


       final long ONE_MEGABYTE = 1024 * 1024;

        photoPathReference.getBytes(10*ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                // After photo is downloaded -> put it to imageview on row
                // todo 1 rotate image/view to correct position...
                //   MyHelper.matcViewToPhotoOrientation(holder.mPhotoOnListImageView,bytes);

                Log.i(TAG, "ROTATION :" + Integer.toString(mPhotoListItems.get(position).getRotation()));

                holder.mPhotoOnListImageView.setRotation(mPhotoListItems.get(position).getRotation());
                Bitmap photo = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                holder.mPhotoOnListImageView.setImageBitmap(photo);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });







    }
    @Override
    public int getItemCount() {
        return this.mPhotoListItems.size();
    }



}
