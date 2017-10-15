package fi.raumankonepaja.deliverylogger;

/**
 * Created by Sami on 11.10.2017.
 */

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ShowPhotosFromDeliveryRecyclerViewHolder extends RecyclerView.ViewHolder{

    private static final String TAG = "Recycler View Holder";

    private List<PhotoListItem> mPhotoListItems;

    public TextView mPhotoInfo;
    public ImageView mPhotoOnListImageView;



    public ShowPhotosFromDeliveryRecyclerViewHolder  (final View itemView, final List<PhotoListItem> mPhotoListItems) {

        super(itemView);
        this.mPhotoListItems = mPhotoListItems;

        mPhotoInfo = (TextView) itemView.findViewById(R.id.photoInfo);
        mPhotoOnListImageView = (ImageView) itemView.findViewById(R.id.PhotoOnListImageView);

        mPhotoOnListImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // this runs when user clicks thumbail of photo

                int mDeliveryNumber = mPhotoListItems.get(getAdapterPosition()).getDeliveryNumber();
                int mPosition = mPhotoListItems.get(getAdapterPosition()).getDeliveryPos();
                String mPictureFileName = mPhotoListItems.get(getAdapterPosition()).getPictureFileName();


                Intent intent = new Intent(v.getContext(),ShowOnePhotoActivity.class);
                intent.putExtra("EXTRA_DELIVERY_NUMBER", mDeliveryNumber);
                intent.putExtra("EXTRA_DELIVERY_POSITION", mPosition);
                intent.putExtra("EXTRA_PICTURE_FILE_NAME", mPictureFileName);
                v.getContext().startActivity(intent);


            }
        });


    }
}