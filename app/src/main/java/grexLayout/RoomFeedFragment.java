package grexLayout;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.SortedMap;

import grexClasses.EndlessRecyclerViewScrollListener;
import grexClasses.Room;
import grexClasses.User;
import money.cache.grexActivities.R;

import static grexClasses.GrexSocket.DF;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomFeedFragment extends Fragment {

    public RoomFeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RoomFeedFragment.
     */
    public static RoomFeedFragment newInstance() {
        return new RoomFeedFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_room_feed, container, false);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

            }
        });

        mRecyclerView.setAdapter(new RoomAdapter());

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Created by Lorenzo on 1/28/2017.
     * TODO: load all images with Picasso
     * TODO: give cards a dropdown option
     */
    class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
        SortedMap<Object, Object> mDataSet = User.getUser().getRoomsIn();

        RoomAdapter() {
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_room_card, parent, false);
            return new ViewHolder(view);
        }

        private File createImageFile() throws IOException {
            File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            return File.createTempFile(
                    "tmp",  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Room room = (Room) Array.get(mDataSet.values().toArray(), position);
            String img = room.getImage();
            File file = null;
            if (img != null) {
                byte[] decode = Base64.decode(img, Base64.DEFAULT);
                try {
                    file = createImageFile();
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    bos.write(decode);
                    bos.flush();
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                holder.mImage.getLayoutParams().height = 200;
                holder.mImage.getLayoutParams().width = 200;
                //Render image using Picasso library
                Picasso.with(getContext()).load(file)
                        .error(R.drawable._xlarge_icons_100)
                        .placeholder(R.drawable._xlarge_icons_100)
                        .into(holder.mImage);
                holder.mImage.setVisibility(View.VISIBLE);
            } else {
                holder.mImage.setVisibility(View.GONE);
            }
            holder.mName.setText(room.getName());
            try {
                holder.mEndTime.setText(DF.format(DF.parse(room.getDeath())));
                holder.mStartTime.setText(DF.format(DF.parse(room.getBirth())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.mHost.setText("@" + room.getHost());
        }

        @Override
        public int getItemCount() {
            return (null != mDataSet ? mDataSet.size() : 0);
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class ViewHolder extends RecyclerView.ViewHolder {

            // each data item is just a string in this case
            TextView mStartTime, mEndTime, mName, mHost;
            ImageView mImage;

            ViewHolder(View itemView) {
                super(itemView);
                this.mImage = (ImageView) itemView.findViewById(R.id.img_feed_event);
                this.mHost = (TextView) itemView.findViewById(R.id.txt_feed_host);
                this.mStartTime = (TextView) itemView.findViewById(R.id.txt_feed_from);
                this.mEndTime = (TextView) itemView.findViewById(R.id.txt_feed_till);
                this.mName = (TextView) itemView.findViewById(R.id.txt_feed_event_name);
            }

        }
    }
}
