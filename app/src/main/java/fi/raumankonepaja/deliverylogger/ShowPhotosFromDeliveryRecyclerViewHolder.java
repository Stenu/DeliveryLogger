package fi.raumankonepaja.deliverylogger;

/**
 * Created by Sami on 11.10.2017.
 */

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.List;

public class ShowPhotosFromDeliveryRecyclerViewHolder extends RecyclerView.ViewHolder{

    private static final String TAG = "Recycler View Holder";

    private List<PhotoListItem> mPhotoListItems;

    public TextView mPositionTextView;
    public TextView mDateAndTimeTextView;
    public ImageView mPhotoOnListImageView;


    private List<ListItem> mListItems;
    public ShowPhotosFromDeliveryRecyclerViewHolder  (final View itemView, final List<PhotoListItem> mPhotoListItems) {

        super(itemView);
        this.mPhotoListItems = mPhotoListItems;

        mPositionTextView = (TextView) itemView.findViewById(R.id.positionTextView);
        mDateAndTimeTextView = (TextView) itemView.findViewById(R.id.dateAndTimeTextView);
        mPhotoOnListImageView = (ImageView) itemView.findViewById(R.id.PhotoOnListImageView);


    }
}