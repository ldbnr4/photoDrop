package grexClasses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.Set;

import money.cache.grexActivities.R;

import static android.support.v7.widget.RecyclerView.Adapter;

/**
 * Created by Lorenzo on 1/28/2017.
 * TODO: load all images with Picasso
 */
public class RoomAdapter extends Adapter<RoomAdapter.ViewHolder> {
    private Set<Room> mDataSet;
    private Context mContext;

    public RoomAdapter(Context context, Set<Room> roomsIn) {
        mDataSet = roomsIn;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_room_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Room room = (Room) Array.get(mDataSet.toArray(), position);
        String img = room.getImage();
        //Render image using Picasso library
        if (!TextUtils.isEmpty(img)) {
            Picasso.with(mContext).load(img)
                    .error(R.drawable._xlarge_icons_100)
                    .placeholder(R.drawable._xlarge_icons_100)
                    .into(holder.mImage);
        }
        holder.mName.setText(room.getName());
        holder.mEndTime.setText(room.getDeath());
        holder.mStartTime.setText(room.getBirth());
        holder.mHost.setText(room.getHost());
    }

    @Override
    public int getItemCount() {
        return (null != mDataSet ? mDataSet.size() : 0);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public TextView mStartTime, mEndTime, mName, mHost;
        public ImageView mImage;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mImage = (ImageView) itemView.findViewById(R.id.img_feed_event);
            this.mHost = (TextView) itemView.findViewById(R.id.txt_feed_host);
            this.mStartTime = (TextView) itemView.findViewById(R.id.txt_feed_from);
            this.mEndTime = (TextView) itemView.findViewById(R.id.txt_feed_till);
            this.mName = (TextView) itemView.findViewById(R.id.txt_feed_event_name);
        }

    }
}