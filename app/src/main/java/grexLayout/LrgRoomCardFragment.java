package grexLayout;

import android.content.Context;
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
 * Activities that contain this fragment must implement the
 * {@link LrgRoomCardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LrgRoomCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LrgRoomCardFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public LrgRoomCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LrgRoomCardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LrgRoomCardFragment newInstance() {
        LrgRoomCardFragment fragment = new LrgRoomCardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lrg_room_card, container, false);

        Room selRoom = ((HomeActivity) getActivity()).getFocusedRoom();
        ((TextView) view.findViewById(R.id.room_card_name)).setText(selRoom.getName());
        ((TextView) view.findViewById(R.id.room_card_host)).setText(selRoom.getHost());
        ((TextView) view.findViewById(R.id.room_card_descrip)).setText(selRoom.getDescription());
        ((ImageView) view.findViewById(R.id.room_card_image)).setImageDrawable(getActivity().getDrawable(R.drawable._image_file_150));

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.lrgRoomCardClose();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void lrgRoomCardClose();
    }
}
