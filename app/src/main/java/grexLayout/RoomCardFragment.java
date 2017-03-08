package grexLayout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import grexClasses.Room;
import money.cache.grex.HomeActivity;
import money.cache.grex.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomCardFragment extends Fragment {

    public RoomCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RoomCardFragment.
     */
    public static RoomCardFragment newInstance() {
        RoomCardFragment fragment = new RoomCardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_card, container, false);

        Room selRoom = ((HomeActivity) getActivity()).getFocusedRoom();
        ((TextView) view.findViewById(R.id.room_card_name)).setText(selRoom.getName());
        ((TextView) view.findViewById(R.id.room_card_host)).setText(selRoom.getHost());
        ((TextView) view.findViewById(R.id.room_card_descrip)).setText(selRoom.getDescription());
        ((ImageView) view.findViewById(R.id.room_card_image)).setImageDrawable(getActivity().getDrawable(R.drawable._image_file_150));

        return view;
    }
}
