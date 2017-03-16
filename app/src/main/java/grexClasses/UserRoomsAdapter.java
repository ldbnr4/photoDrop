package grexClasses;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import money.cache.grex.R;

/**
 * Created by boyice on 3/16/2017.
 *
 */

public class UserRoomsAdapter extends RecyclerView.Adapter<UserRoomsAdapter.ViewHodler>{
    private Room[] mDataSet;

    class ViewHodler extends RecyclerView.ViewHolder {
        @Bind(R.id.TV_room_item_name)
        TextView mFriendName;

        public ViewHodler(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        TextView getmFriendName(){return mFriendName;}

    }

    public UserRoomsAdapter(Room[] mDataSet) {
        this.mDataSet = mDataSet;
    }

    @Override
    public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_item, parent, false);
        return new ViewHodler(v);
    }

    @Override
    public void onBindViewHolder(ViewHodler holder, int position) {
        holder.getmFriendName().setText(mDataSet[position].getName());
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
}
