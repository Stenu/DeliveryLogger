package fi.raumankonepaja.deliverylogger;

/**
 * Created by Sami on 11.10.2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private static final String TAG = "Recycler View Adapter";

    private List<ListItem> mListItems;
    protected Context mContext;

    public RecyclerViewAdapter(Context mContext, List<ListItem> mListItems) {
        this.mContext = mContext;
        this.mListItems = mListItems;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewHolder viewHolder = null;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_row_on_list, parent, false);
        viewHolder = new RecyclerViewHolder(layoutView, mListItems);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

       //print log
        Log.i(TAG, "Binding holder ------");
        holder.mDeliveryNumberTextView.setText(Integer.toString(mListItems.get(position).getDeliveryNumber()));

    }
    @Override
    public int getItemCount() {
        return this.mListItems.size();
    }



}
