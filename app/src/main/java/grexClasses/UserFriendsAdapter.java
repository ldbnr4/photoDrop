package grexClasses;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import money.cache.grex.R;

/**
 * Created by Lorenzo on 3/14/2017.
 *
 */

public class UserFriendsAdapter extends RecyclerView.Adapter<UserFriendsAdapter.ViewHodler> {
    User[] mDataSet;

    public class ViewHodler extends RecyclerView.ViewHolder {
        @Bind(R.id.IV_friend_item_image)
        ImageView mFreindImage;
        @Bind(R.id.TV_friend_item_name)
        TextView mFriendName;
        public ViewHodler(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public ImageView getmFreindImage() {
            return mFreindImage;
        }

        public TextView getmFriendName() {
            return mFriendName;
        }
    }

    public UserFriendsAdapter(User[] dataSet){
        mDataSet = dataSet;
    }
    @Override
    public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new ViewHodler(v);
    }

    @Override
    public void onBindViewHolder(ViewHodler holder, int position) {
        holder.getmFriendName().setText(mDataSet[position].name);
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
