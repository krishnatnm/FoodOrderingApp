package com.tanmay.orderfoodhere.view.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tanmay.orderfoodhere.R;
import com.tanmay.orderfoodhere.utils.MenuData;
import com.tanmay.orderfoodhere.view.adapters.FoodItemAdapter;
import com.tanmay.orderfoodhere.view.interfaces.OnListItemClickListener;
import com.tanmay.orderfoodhere.view.models.FoodItem;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Mains.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Mains#newInstance} factory method to
 * create an instance of this fragment.
 */

/**
 * Created by TaNMay on 5/31/2016.
 */

public class Mains extends Fragment implements OnListItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public Mains() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Mains.
     */
    // TODO: Rename and change types and number of parameters
    public static Mains newInstance(String param1, String param2) {
        Mains fragment = new Mains();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fm_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FoodItemAdapter(getActivity(), getMenu());
        mRecyclerView.setAdapter(mAdapter);
        FoodItemAdapter.click = Mains.this;

        return view;
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

    @Override
    public void onItemClick(int position) {

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

    public ArrayList<FoodItem> getMenu() {
        ArrayList<FoodItem> foodItemList = new ArrayList<>();
        for (int i = 0; i < MenuData.ITEM_NAME.length; i++) {
            FoodItem foodItem = new FoodItem();
            foodItem.setName(MenuData.ITEM_NAME[i]);
            foodItem.setCost(MenuData.ITEM_COST[i]);
            foodItem.setLikes(MenuData.ITEM_LIKES[i]);
            foodItem.setImage(MenuData.ITEM_IMAGES[i]);
            foodItemList.add(foodItem);
        }
        return foodItemList;
    }
}
