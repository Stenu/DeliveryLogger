package fi.raumankonepaja.deliverylogger;

/**
 * Created by Sami on 11.10.2017.
 */

import android.app.LauncherActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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

public class RecyclerViewHolder extends RecyclerView.ViewHolder{

    private static final String TAG = "**Recycler View Holder**";

    public TextView mDeliveryNumberTextView;

    private List<ListItem> mListItems;
    public RecyclerViewHolder  (final View itemView, final List<ListItem> mListItems) {

        super(itemView);
        this.mListItems = mListItems;
        mDeliveryNumberTextView = (TextView)itemView.findViewById(R.id.textViewDeliveryNumber);

    }
}

