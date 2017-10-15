
package fi.raumankonepaja.deliverylogger;

/**
 * Created by Sami on 11.10.2017.
 */

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewHolder extends RecyclerView.ViewHolder{

    private static final String TAG = "Recycler View Holder";

    public TextView mDeliveryNumberTextView;
    private Button mShowPicturesButton;

    private List<ListItem> mListItems;
    public RecyclerViewHolder(final View itemView, final List<ListItem> mListItems) {

        super(itemView);
        this.mListItems = mListItems;

        mDeliveryNumberTextView = (TextView)itemView.findViewById(R.id.textViewDeliveryNumber);
        mShowPicturesButton = (Button) itemView.findViewById(R.id.showPicturesButton);


        mShowPicturesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int mDeliveryNumber = mListItems.get(getAdapterPosition()).getDeliveryNumber();

                Intent intent = new Intent(v.getContext(),ShowPhotosOfDelivery.class);
                intent.putExtra("EXTRA_DELIVERY_NUMBER", mDeliveryNumber);
                v.getContext().startActivity(intent);


            }
        });
    }
}
