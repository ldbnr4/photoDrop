package money.cache.grexActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Set;

import grexClasses.Room;
import grexClasses.RoomAdapter;
import grexClasses.User;


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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_room_feed, container, false);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Set<Room> roomsIn = User.getUser().getRoomsIn();
        RecyclerView.Adapter mAdapter = new RoomAdapter(getActivity(), roomsIn);
        mRecyclerView.setAdapter(mAdapter);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
