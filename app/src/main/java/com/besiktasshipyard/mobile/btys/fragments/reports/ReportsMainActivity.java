package com.besiktasshipyard.mobile.btys.fragments.reports;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;

import com.besiktasshipyard.mobile.btys.R;
import com.besiktasshipyard.mobile.btys.businessLayer.dataItems.reports.GenericReportResultData;
import com.besiktasshipyard.mobile.btys.businessLayer.dataItems.reports.ReportListData;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationHelpers;

import java.util.HashMap;

public class ReportsMainActivity extends AppCompatActivity
implements ReportListFragment.OnListFragmentInteractionListener
           ,GenericReportResultFragment.OnListFragmentInteractionListener
{

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment _reportlistFragment = getSupportFragmentManager().findFragmentByTag(ReportListFragment.class.getName());
        if (_reportlistFragment == null || !_reportlistFragment.isVisible()) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_main);

        if (savedInstanceState != null)
            return;

        showNewFragment(ReportListFragment.newInstance(true, this));
    }

    public void showLoadingPanel(){

        findViewById(R.id.reportsActivityLoadingPanel).setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View v = super.onCreateView(name, context, attrs);
        return v;
    }

    public void hideLoadingPanel(){
        findViewById(R.id.reportsActivityLoadingPanel).setVisibility(View.GONE);
    }

    private void showNewFragment(Fragment fragment){

        if (fragment != null) {
            ApplicationHelpers.hideSoftKeyboard(this);
            FragmentManager _fragmentManager = getSupportFragmentManager();
            FragmentTransaction _fragmentTransaction = _fragmentManager.beginTransaction();


            _fragmentTransaction.replace(R.id.reports_main_content_frame,fragment,fragment.getClass().getName());

            _fragmentTransaction.addToBackStack(fragment.getClass().getName());
            _fragmentTransaction.commit();
        }
    }

    //// TODO: 8.11.2018 rapor gösteren metod
    @Override
    public void onListFragmentInteraction(ReportListData.ReportListItem item) {
//        int _reportId = Integer.valueOf(item.id);
//        String _reportTitle = item.title;
//        displayGenericReportPage(_reportId, _reportTitle);
    }

    public void displayGenericReportPage(int reportId, String reportTitle){

        GenericReportResultFragment fragment = GenericReportResultFragment.newInstance(reportId, true, this, reportTitle);

        ApplicationHelpers.hideSoftKeyboard(this);
        FragmentManager _fragmentManager = getSupportFragmentManager();
        FragmentTransaction _fragmentTransaction = _fragmentManager.beginTransaction();

        _fragmentTransaction.replace(R.id.reports_main_content_frame, fragment);
        _fragmentTransaction.addToBackStack(fragment.getClass().getName());
        _fragmentTransaction.commit();
    }

    public void displayGenericReportPage(String pid, String aid, HashMap<String,String> reportParams, String reportTitle, int containerViewId){

        GenericReportResultFragment fragment = GenericReportResultFragment.newInstance(true, this, reportTitle, pid, aid, reportParams);

        ApplicationHelpers.hideSoftKeyboard(this);
        FragmentManager _fragmentManager = getSupportFragmentManager();
        FragmentTransaction _fragmentTransaction = _fragmentManager.beginTransaction();

        _fragmentTransaction.replace(containerViewId, fragment);
        _fragmentTransaction.addToBackStack(fragment.getClass().getName());
        _fragmentTransaction.commit();
    }

    @Override
    public void onListFragmentInteraction(GenericReportResultData.GenericReportResultItem item) {

    }
}
