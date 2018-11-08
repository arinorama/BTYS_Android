package com.besiktasshipyard.mobile.btys.fragments.test;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.besiktasshipyard.mobile.btys.MainActivity;
import com.besiktasshipyard.mobile.btys.R;
import android.support.v4.widget.TextViewCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Test1Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Test1Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Test1Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    CustomPagerAdapter mCustomPagerAdapter;
    ViewPager mViewPager;

    ArrayList<String> _fragmentTitles;

    public Test1Fragment() {
        // Required empty public constructor
    }

    public static Test1Fragment newInstance(String param1, String param2) {
        Test1Fragment fragment = new Test1Fragment();
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

        mCustomPagerAdapter = new CustomPagerAdapter(getActivity().getSupportFragmentManager(), MainActivity._context);

        _fragmentTitles = new ArrayList<>();
        _fragmentTitles.add("Özlük");
        _fragmentTitles.add("İzin");
        _fragmentTitles.add("Eğitim");


    }

    class CustomPagerAdapter extends FragmentPagerAdapter {

        Context mContext;

        public CustomPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }


        @Override
        public Fragment getItem(int position) {

            // Create fragment object
            Fragment fragment;

            switch (position){
                case 0:
                    fragment = Test3Fragment.newInstance(_fragmentTitles.get(position),"");
                    break;
                case 1:
                    fragment = Test4Fragment.newInstance(_fragmentTitles.get(position),"");
                    break;
                case 2:
                    fragment = Test3Fragment.newInstance(_fragmentTitles.get(position),"");
                    break;
                default:
                    fragment = Test3Fragment.newInstance(_fragmentTitles.get(position),"");
                    break;
            }

            // Attach some data to the fragment
            // that we'll use to populate our fragment layouts
            Bundle args = new Bundle();
            args.putInt("page_position", position + 1);

            // Set the arguments on the fragment
            // that will be fetched in the
            // DemoFragment@onCreateView
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return _fragmentTitles.get(position);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test1, container, false);

        android.support.v4.view.PagerTitleStrip _strip = (android.support.v4.view.PagerTitleStrip) view.findViewById(R.id.pager_title_strip);
        _strip.setNonPrimaryAlpha(0.2f);
        _strip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);


//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
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
