package com.besiktasshipyard.mobile.btys.fragments.test;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.besiktasshipyard.mobile.btys.R;

import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Test2Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Test2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Test2Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    private FragmentTabHost tabHost;

    public Test2Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Test2Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Test2Fragment newInstance(String param1, String param2) {
        Test2Fragment fragment = new Test2Fragment();
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
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_test2, container, false);
        tabHost = new FragmentTabHost(getActivity());
        tabHost.setup(getActivity(), getChildFragmentManager(), R.layout.fragment_test2);

        Bundle arg1 = new Bundle();
        arg1.putString("LabelText", "Özlük");
        tabHost.addTab(tabHost.newTabSpec("Tab1").setIndicator("Özlük"),
                Test3Fragment.class, arg1);

        Bundle arg2 = new Bundle();
        arg2.putString("LabelText", "Eğitim");
        tabHost.addTab(tabHost.newTabSpec("Tab2").setIndicator("Eğitim"),
                Test4Fragment.class, arg2);

        Bundle arg3 = new Bundle();
        arg2.putString("LabelText", "Tecrübe");
        tabHost.addTab(tabHost.newTabSpec("Tab3").setIndicator("Tecrübe"),
                Test3Fragment.class, arg3);

        Bundle arg4 = new Bundle();
        arg2.putString("LabelText", "Sağlık");
        tabHost.addTab(tabHost.newTabSpec("Tab4").setIndicator("Sağlık"),
                Test4Fragment.class, arg4);

        Bundle arg5 = new Bundle();
        arg2.putString("LabelText", "Zimmet");
        tabHost.addTab(tabHost.newTabSpec("Tab5").setIndicator("Zimmet"),
                Test4Fragment.class, arg5);

        Bundle arg6 = new Bundle();
        arg2.putString("LabelText", "KKD");
        tabHost.addTab(tabHost.newTabSpec("Tab6").setIndicator("KKD"),
                Test4Fragment.class, arg6);


        return tabHost;
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
}
