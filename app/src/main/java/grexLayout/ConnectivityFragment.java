package grexLayout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import money.cache.grex.R;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link ConnectivityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConnectivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConnectivityFragment extends Fragment {
    private static final String MESSAGE = "param1";
    private String mMessage;

    private OnFragmentInteractionListener mListener;
    private Button mRetryButton;

    public ConnectivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ConnectivityFragment.
     */
    public static ConnectivityFragment newInstance(String param1) {
        ConnectivityFragment fragment = new ConnectivityFragment();
        Bundle args = new Bundle();
        args.putString(MESSAGE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMessage = getArguments().getString(MESSAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connectivity, container, false);

        TextView mMessageTView = (TextView) view.findViewById(R.id.txt_conncetivity_msg);
        mMessageTView.setText(mMessage);
        mRetryButton = (Button) view.findViewById(R.id.btn_retry);
        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRetryButton.setEnabled(false);
                onButtonPressed();
                mRetryButton.setEnabled(true);
            }
        });

        ImageView mImageView = (ImageView) view.findViewById(R.id.imageView);
        mImageView.setImageResource(R.drawable._disconnected_100);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
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
        void onFragmentInteraction();
    }

}
