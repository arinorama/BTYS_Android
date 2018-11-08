package com.besiktasshipyard.mobile.btys.fragments.reports;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.besiktasshipyard.mobile.btys.R;
import com.besiktasshipyard.mobile.btys.busEvents.onGetGenericReportResult;
import com.besiktasshipyard.mobile.btys.businessLayer.dataItems.reports.GenericReportResultData;
import com.besiktasshipyard.mobile.btys.businessLayer.reports.Reports;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationErrorData;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationHelpers;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GenericReportResultFragment.OnListFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GenericReportResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GenericReportResultFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener{
    private Context context;

    private static String _reportTitle = "Raporlar";

    private boolean _loadDataFromDB = true;
    private static int _reportId = 0;

    private GenericReportResultFragment .OnListFragmentInteractionListener mListener;

    private Context _context = null;

    private RecyclerView recyclerView;
    private GenericReportResultRecyclerViewAdapter _adapter;

    GenericReportResultData _reportData;
    GenericReportResultFragment.GenericReportResultFragmentState _fragmentState;

    //tepede yer alacak arama viewi
    SearchView searchView;
    MenuItem searchItem;

    TextView _tvAppBarInfo, _tvAppBarReportDate;

    String _reportDate = "";
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GenericReportResultFragment() {

    }

    @SuppressWarnings("unused")
    public static GenericReportResultFragment newInstance(int reportId, boolean loadDataFromDB, Context context, String reportTitle) {
        _reportId = reportId;
        _reportTitle = reportTitle;

        GenericReportResultFragment fragment = new GenericReportResultFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _tvAppBarInfo = (TextView) getActivity().findViewById(R.id.app_bar_reports_tvAppBarInfo);
        _tvAppBarReportDate = (TextView) getActivity().findViewById(R.id.app_bar_reports_tvAppBarReportDate);

        //eventbus register - eventbus callback handle edebilmek icin once register olmak gerek
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        //searchview içeren toolbar yaratılıyor
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //view tanımlanıyor
        View view = inflater.inflate(R.layout.fragment_generic_repor_result, container, false);

        // adapter tanimlanip layout yaratiliyor
        if (view instanceof RecyclerView && savedInstanceState == null) {
            context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            //eger veriler dbden yukleniyorsa verileri bastan yukle
//            if (_loadDataFromDB) {
//                //verileri yukle
                loadDataFromDB();
//                _loadDataFromDB = false;
//            }
//            else{ //veriler dbden yuklenmiyor, fragment yeniden aktif oldu, bunun icin eski durumunu tekrar yukle
//                restoreFragmentState();
//            }

        }


        return view;
    }

    /**
     * fragment replace edilirken calisan metod
     * fragment state save ediyorum
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        saveFragmentState();
    }

//    /**
//     * fragmenti tekrar eski haline cevirmek icin.
//     * bu fragment yeni bastan olusturulmamis, daha onceden veriler yuklenmis, sonradan baska fragmente gecilip sonra buna geri donulmus.
//     * fragment hafizadan silinmemis, sadece layout destroy edilmis, yeniden inflate oluyor
//     * yani createView - destroyView calisiyor
//     * biz de fragment view yeniden inflate edilirken, eski UI state i yeniden olusturuyoruz
//     */
//    private void restoreFragmentState(){
//        //onceden db den alinmis veriyi tekrar goster
//        showReportListData(DataCache.getInstance().get_reportListData());
//    }
//
//    private void restoreFilterState(){
//        CharSequence _filterText = _fragmentState.get_filterText();
//
//        if(_filterText.length() > 0)
//        {
//            //searchview expand
//            searchView.setIconified(false);
//
//            //searchView text set ediliyor ve yeniden filtreleme yapiliyor
//            searchView.setQuery(_filterText, true);
//        }
//    }

//    /**
//     * destroyView de calisacak, fragment UI stateini kaydedecek
//     * sonradan, restoreFragmentState yapilirken, burada kayit edilen UI statelere gore restore edilecek
//     */
//    private void saveFragmentState(){
//        String _filterText = "";
//        if(searchView != null && searchView.getQuery() != null)
//            _filterText = searchView.getQuery().toString();
//
//        _fragmentState = new GenericReportResultFragment.GenericReportResultFragmentState(_filterText, DataCache.getInstance().get_reportListData());
//    }


    /**
     * ReportList datasını DB den yükler
     */
    private void loadDataFromDB(){
//        DataCache _datacache = DataCache.getInstance();

        //eger data, cache de varsa, oradan goster
//        if (_datacache.get_reportListData() != null){
//            showReportListData(_datacache.get_reportListData());
//        }
        //yoksa yeniden yukle
//        else {
            //loading ekranı görüntüleniyor
            ((ReportsMainActivity) getActivity()).showLoadingPanel();

            Reports _reports = Reports.getInstance(context, recyclerView);
            _reports.getGenericReportResult(new onGetGenericReportResult(), _reportId);

    }




    @Override
    public void onResume() {
        super.onResume();
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        ApplicationHelpers.setFragmentTitle((AppCompatActivity) getActivity(), "Rapor");
    }

    @Override
    public void onPause() {
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        //basligi sil
        ApplicationHelpers.setFragmentTitle((AppCompatActivity) getActivity(),"");

        //info texti gizle
        if(_tvAppBarInfo != null)
            _tvAppBarInfo.setVisibility(View.GONE);

        super.onPause();
    }

    @Override
    public void onStop() {
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

        super.onStop();
    }






    /**
     * gelen datayı handle eden callback
     * @param event
     */
    @Subscribe
    public void onEvent(onGetGenericReportResult event){
        //gelen event te error varsa isle
        if (event.getError() != null)
        {
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), this.getClass().getName() + " hata: onGetGenericReportResult")
            );
            return;
        }

        //gelen event te data yoksa hata ver
        if (event.getData() == null)
        {
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, "", this.getClass().getName() + " hata: onGetGenericReportResult")
            );
            return;
        }

//        //ReportListDatayi cache e at
//        DataCache.getInstance().set_reportListData(event.getData());

        //GenericReportResult datasini gosterir
        showGenericReportData(event.getData());
        ((ReportsMainActivity) getActivity()).hideLoadingPanel();
    }

    /**
     * datayi alir ve kullaniciya gosterir
     * @param data
     */
    private void showGenericReportData(Object data){
        //sonradan tekrar sayfaya donulurse, tekrar bilgileri yuklememek icin datayi cache atiyoruz
//        DataCache.getInstance().set_reportListData(data);

        //datayi JSONArray e cevirip recycleriew e atiyoruz
        JSONArray _jaReportData = (JSONArray) data;
        _reportData = new GenericReportResultData(_jaReportData);
        _adapter = new GenericReportResultRecyclerViewAdapter(_reportData.ITEMS, mListener);
        recyclerView.setAdapter(_adapter);


        showRecordCount();
        showReportDate();
    }

    private void showReportDate(){

        _reportDate = ApplicationHelpers.getInstance(_context).getDateFromUnixTimestamp(_reportData.reportDate);
//        String _reportDate = _adapter.getItemCount();

        _tvAppBarReportDate.setText("Rapor Tarihi: " + _reportDate);
        _tvAppBarReportDate.setVisibility(View.VISIBLE);
    }

    private void showRecordCount(){

        int _recordCount = _adapter.getItemCount();

        _tvAppBarInfo.setText(_reportTitle + ": Toplam Kayıt Sayisi: " + _recordCount);
        _tvAppBarInfo.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        _context = context;
        if (context instanceof GenericReportResultFragment.OnListFragmentInteractionListener) {
            mListener = (GenericReportResultFragment.OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " OnListFragmentInteractionListener handle edilmemis");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * fragment destroy ediliyor
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        //eventbus unregister ediliyor
        if(EventBus.getDefault().isRegistered(_context))
            EventBus.getDefault().unregister(_context);
    }

//region SearchView Metodlari

    /**
     * searchview burada set edilitor
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.main, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getResources().getString(R.string.search));

//        //eger UI restore yapiliyorsa, filter text i de yenile
//        if(_fragmentState != null && _fragmentState._filterText != null) {
//            restoreFilterState();
//        }

    }

    /**
     * search text yazılıp arama butonuna basıldığında çalışan fonk.
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        _adapter.getFilter().filter(query);
        showRecordCount();
//        TextView _tvListDescription = (TextView) getActivity().findViewById(R.id.tvListDescription);
//        _tvListDescription.setText("Toplam " + _adapter.getItemCount() + " kişi");
        return false;
    }

    /**
     * search
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        _adapter.getFilter().filter(newText);
//        showRecordCount();
        return false;
    }


    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return false;
    }
//endregion


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(GenericReportResultData.GenericReportResultItem item);
    }

    private class GenericReportResultFragmentState{
        private Object _ReportListDataState;
        private String _filterText;

        public GenericReportResultFragmentState(String filterText, Object ReportListDataState){
            _filterText = filterText;
            _ReportListDataState = ReportListDataState;
        }


        public String get_filterText() {
            return _filterText;
        }

        public Object get_reportListDataState() {
            return _ReportListDataState;
        }
    }
}
