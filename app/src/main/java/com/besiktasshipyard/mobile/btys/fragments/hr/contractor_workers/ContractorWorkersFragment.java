package com.besiktasshipyard.mobile.btys.fragments.hr.contractor_workers;

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

import com.besiktasshipyard.mobile.btys.MainActivity;
import com.besiktasshipyard.mobile.btys.R;
import com.besiktasshipyard.mobile.btys.busEvents.onGetContractorWorkerList;
import com.besiktasshipyard.mobile.btys.businessLayer.dataItems.dataCache.DataCache;
import com.besiktasshipyard.mobile.btys.businessLayer.dataItems.contractorWorkers.ContractorWorkerListData;
import com.besiktasshipyard.mobile.btys.businessLayer.dataItems.contractorWorkers.ContractorWorkerListData.ContractorWorkerListItem;
import com.besiktasshipyard.mobile.btys.businessLayer.hr.ContractorWorkers;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationErrorData;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationHelpers;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ContractorWorkersFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener{

    private Context context;

    private String TITLE = "Yüklenici Personelleri";

    private boolean _loadDataFromDB = true;
    private OnListFragmentInteractionListener mListener;

    private Context _context = null;

    private RecyclerView recyclerView;
    private ContractorWorkerListListRecyclerViewAdapter _adapter;

    ContractorWorkerListData _contractorWorkerListData;
    ContractorWorkerFragmentState _fragmentState;

    //tepede yer alacak arama viewi
    SearchView searchView;
    MenuItem searchItem;

    TextView _tvAppBarInfo;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContractorWorkersFragment() {

    }

    @SuppressWarnings("unused")
    public static ContractorWorkersFragment newInstance(boolean loadDataFromDB, Context context) {
        ContractorWorkersFragment fragment = new ContractorWorkersFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _tvAppBarInfo = (TextView) getActivity().findViewById(R.id.tvAppBarInfo);

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
        View view = inflater.inflate(R.layout.fragment_contractor_worker_list, container, false);

        // adapter tanimlanip layout yaratiliyor
        if (view instanceof RecyclerView && savedInstanceState == null) {
            context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            //eger veriler dbden yukleniyorsa verileri bastan yukle
            if (_loadDataFromDB) {
                //verileri yukle
                loadDataFromDB();
                _loadDataFromDB = false;
            }
            else{ //veriler dbden yuklenmiyor, fragment yeniden aktif oldu, bunun icin eski durumunu tekrar yukle
                restoreFragmentState();
            }

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
        saveFragmentState();
    }

    /**
     * fragmenti tekrar eski haline cevirmek icin.
     * bu fragment yeni bastan olusturulmamis, daha onceden veriler yuklenmis, sonradan baska fragmente gecilip sonra buna geri donulmus.
     * fragment hafizadan silinmemis, sadece layout destroy edilmis, yeniden inflate oluyor
     * yani createView - destroyView calisiyor
     * biz de fragment view yeniden inflate edilirken, eski UI state i yeniden olusturuyoruz
     */
    private void restoreFragmentState(){
        //onceden db den alinmis veriyi tekrar goster
        showContractorWorkerListData(DataCache.getInstance().get_contractorWorkerListData());
    }

    private void restoreFilterState(){
        CharSequence _filterText = _fragmentState.get_filterText();

        if(_filterText.length() > 0)
        {
            //searchview expand
            searchView.setIconified(false);

            //searchView text set ediliyor ve yeniden filtreleme yapiliyor
            searchView.setQuery(_filterText, true);
        }
    }

    /**
     * destroyView de calisacak, fragment UI stateini kaydedecek
     * sonradan, restoreFragmentState yapilirken, burada kayit edilen UI statelere gore restore edilecek
     */
    private void saveFragmentState(){
        String _filterText = "";
        if(searchView != null && searchView.getQuery() != null)
            _filterText = searchView.getQuery().toString();

        _fragmentState = new ContractorWorkerFragmentState(_filterText, DataCache.getInstance().get_contractorWorkerListData());
    }

//    private void loadDataFromState(String adapterJSON){
//        _adapter = new Gson().fromJson(adapterJSON,_adapter.getClass());
//        showUserListData();
//        // getting recyclerview position
////        mListState = mSavedInstanceState.getParcelable(SAVED_RECYCLER_VIEW_STATUS_ID);
////        // getting recyclerview items
////        mDataset = mSavedInstanceState.getParcelableArrayList(SAVED_RECYCLER_VIEW_DATASET_ID);
////        // Restoring adapter items
////        mAdapter.setItems(mDataset);
////        // Restoring recycler view position
////        mRvMedia.getLayoutManager().onRestoreInstanceState(mListState);
//    }

    /**
     * contractorWorkerList datasını DB den yükler
     */
    private void loadDataFromDB(){
        DataCache _datacache = DataCache.getInstance();

        //eger data, cache de varsa, oradan goster
        if (_datacache.get_contractorWorkerListData() != null){
//            _cachedRawData = _datacache.get_contractorWorkerListData();
            showContractorWorkerListData(_datacache.get_contractorWorkerListData());
        }
        //yoksa yeniden yukle
        else {
            //loading ekranı görüntüleniyor
            ((MainActivity) getActivity()).showLoadingPanel();

            ContractorWorkers _contractorWorkers = ContractorWorkers.getInstance(context, recyclerView);
            _contractorWorkers.getContractorWorkerList(new onGetContractorWorkerList());
        }
    }

    //    @Override
//    public void onSaveInstanceState(Bundle outState) {
////        Parcelable listState = recyclerView.getLayoutManager().onSaveInstanceState();
////        // putting recyclerview position
////        outState.putParcelable(SAVED_RECYCLER_VIEW_STATUS, listState);
////        // putting recyclerview items
////        outState.putParcelableArrayList(SAVED_RECYCLER_VIEW_DATASET, _userListData.ITEMS);
//        outState.putString("recycleview_adapter", new Gson().toJson(_adapter));
//        super.onSaveInstanceState(outState);
//    }



    @Override
    public void onResume() {
        super.onResume();
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        ApplicationHelpers.setFragmentTitle((AppCompatActivity) getActivity(),TITLE);
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
    public void onEvent(onGetContractorWorkerList event){
        //gelen event te error varsa isle
        if (event.getError() != null)
        {
//            ApplicationHelpers.getInstance(getContext()).handleError("ContractorWorkersFragment: hata: onGetContractorWorkerList - " + event.getError().toString());
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), this.getClass().getName() + " hata: onGetContractorWorkerList")
            );
            return;
        }

        //gelen event te data yoksa hata ver
        if (event.getData() == null)
        {
//            ApplicationHelpers.getInstance(getContext()).handleError("ContractorWorkersFragment: hata: onGetContractorWorkerList - DATA yok");
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, "", this.getClass().getName() + " hata: onGetContractorWorkerList")
            );
            return;
        }

        //contractorWorkerListDatayi cache e at
        DataCache.getInstance().set_contractorWorkerListData(event.getData());

        //userlist datasini gosterir
        showContractorWorkerListData(event.getData());
        ((MainActivity) getActivity()).hideLoadingPanel();
    }

    /**
     * datayi alir ve kullaniciya gosterir
     * @param data
     */
    private void showContractorWorkerListData(Object data){
        //sonradan tekrar sayfaya donulurse, tekrar bilgileri yuklememek icin datayi cache atiyoruz
        DataCache.getInstance().set_contractorWorkerListData(data);

        //datayi JSONArray e cevirip recycleriew e atiyoruz
        JSONArray _jaContractorWorkerlist = (JSONArray) data;
        _contractorWorkerListData = new ContractorWorkerListData(_jaContractorWorkerlist);
        _adapter = new ContractorWorkerListListRecyclerViewAdapter(_contractorWorkerListData.ITEMS, mListener);
        recyclerView.setAdapter(_adapter);

        showRecordCount();
    }


    private void showRecordCount(){

        int _recordCount = _adapter.getItemCount();


        _tvAppBarInfo.setText("Toplam Kayit Sayisi: " + _recordCount);
        _tvAppBarInfo.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        _context = context;
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
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
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getResources().getString(R.string.search));

        //eger UI restore yapiliyorsa, filter text i de yenile
        if(_fragmentState != null && _fragmentState._filterText != null) {
            restoreFilterState();
        }

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
        void onListFragmentInteraction(ContractorWorkerListItem item);
    }

    private class ContractorWorkerFragmentState{
        private Object _userListDataState;
        private String _filterText;

        public ContractorWorkerFragmentState(String filterText, Object userListDataState){
            _filterText = filterText;
            _userListDataState = userListDataState;
        }


        public String get_filterText() {
            return _filterText;
        }

        public Object get_userListDataState() {
            return _userListDataState;
        }
    }
}
