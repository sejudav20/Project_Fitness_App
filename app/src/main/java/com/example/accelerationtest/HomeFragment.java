package com.example.accelerationtest;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createGameCards();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        void onFragmentInteraction(Uri uri);
    }

    public void createGameCards() {
        //initializing GameCards
        CardView cardView1 = getView().findViewById(R.id.CardView1);
        CardView cardView2 = getView().findViewById(R.id.CardView2);
        CardView cardView3 = getView().findViewById(R.id.CardView3);
        CardView cardView4 = getView().findViewById(R.id.CardView4);
        CardView cardView5 = getView().findViewById(R.id.CardView5);
        CardView cardView6 = getView().findViewById(R.id.CardView6);
        ArrayList<CardView> cards = new ArrayList<>();

        LayoutInflater inflater = getLayoutInflater();
        inflater.inflate(R.layout.gamecard, cardView1, true);
        inflater.inflate(R.layout.gamecard, cardView2, true);
        inflater.inflate(R.layout.gamecard, cardView3, true);
        inflater.inflate(R.layout.gamecard, cardView4, true);
        inflater.inflate(R.layout.gamecard, cardView5, true);
        inflater.inflate(R.layout.gamecard, cardView6, true);

        //change any game cards below


    }

    public class GameCardView extends CardView {

        private ConstraintLayout cl;
        private ImageView iv;
        private TextView PlayerAmountDisplay;
        private TextView nameDisplay;

        public GameCardView(@NonNull Context context) {
            super(context);

            cl = (ConstraintLayout) super.getChildAt(0);
            PlayerAmountDisplay= (TextView) cl.getChildAt(2);
            nameDisplay= (TextView) cl.getChildAt(1);
            iv= (ImageView) cl.getChildAt(0);

        }

        public void addActivity(final AppCompatActivity activity) {
            if (activity instanceof Minigame) {
                super.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), activity.getClass());

                        startActivity(intent);
                    }
                });
                PlayerAmountDisplay.setText("Players: "+((Minigame) activity).getPlayers());
                nameDisplay.setText(((Minigame) activity).getName());
                iv.setImageResource(((Minigame) activity).getImageId());

            } else {

                throw new NoMinigameInterfaceError("It looks like you did not implement" +
                        " the minigame interface in the activity attached to game card id: " + this.getId());
            }


        }

        private class NoMinigameInterfaceError extends Error {
            public NoMinigameInterfaceError(String errorMessage) {
                super(errorMessage);


            }

        }

    }


}
