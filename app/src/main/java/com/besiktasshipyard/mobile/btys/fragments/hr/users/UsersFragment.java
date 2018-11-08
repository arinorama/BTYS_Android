package com.besiktasshipyard.mobile.btys.fragments.hr.users;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.besiktasshipyard.mobile.btys.MainActivity;
import com.besiktasshipyard.mobile.btys.R;
import com.besiktasshipyard.mobile.btys.busEvents.onGetUserList;
import com.besiktasshipyard.mobile.btys.businessLayer.dataItems.dataCache.DataCache;
import com.besiktasshipyard.mobile.btys.businessLayer.dataItems.general.SpinnerDataItem;
import com.besiktasshipyard.mobile.btys.businessLayer.dataItems.userList.UserListData;
import com.besiktasshipyard.mobile.btys.businessLayer.dataItems.userList.UserListData.UserListItem;
import com.besiktasshipyard.mobile.btys.businessLayer.hr.HRUsers;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationErrorData;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationHelpers;
import com.besiktasshipyard.mobile.btys.helpers.StringHelpers;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class UsersFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener{

    private Context context;

    private String TITLE = "Personel Listesi";

    private boolean _loadDataFromDB = true;
    private OnListFragmentInteractionListener mListener;

    private Context _context = null;

    private RecyclerView recyclerView;
    private UserListRecyclerViewAdapter _adapter;

    UserListData _userListData;
    UsersFragmentState _fragmentState;

    //tepede yer alacak arama viewi
    SearchView searchView;
    MenuItem searchItem;

    TextView _tvAppBarInfo;
    Spinner _spAppBarInfoFilterCombo1;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UsersFragment() {

    }

    @SuppressWarnings("unused")
    public static UsersFragment newInstance(boolean loadDataFromDB, Context context) {
        UsersFragment fragment = new UsersFragment();
//        Bundle args = new Bundle();
//        args.putBoolean(ARG_LOAD_DATA_FROM_DB, loadDataFromDB);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            _loadDataFromDB = getArguments().getBoolean(ARG_LOAD_DATA_FROM_DB);
//        }

        _tvAppBarInfo = (TextView) getActivity().findViewById(R.id.tvAppBarInfo);
        _spAppBarInfoFilterCombo1 = (Spinner) getActivity().findViewById(R.id.spAppBarInfoFilterCombo1);
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
        View view = inflater.inflate(R.layout.fragment_users_list, container, false);

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
        showUserListData(DataCache.getInstance().get_userListData());
    }

    private void restoreFilterState(){
        /*String _filterText = _fragmentState.get_filterText();
        String _filterTitleId = _fragmentState.get_filterTitleId();

        _filterText = _filterText.split("&")[0].toString();
        String _resultFilterText = "";
        if(_filterText.length() > 0){ //eger filtrede sadece text filtresi varsa
            _resultFilterText = _filterText;
        }

        if (!StringHelpers.isEmptyString(_filterTitleId) || TextUtils.equals(_filterTitleId, "-1")) { //filterTitleId -1 degil ise yada bos degilse
            _resultFilterText += "&filter_title_id=" + _filterTitleId;
        }

        if(!StringHelpers.isEmptyString(_resultFilterText)){
            //searchview expand
            searchView.setIconified(false);

            //searchView text set ediliyor ve yeniden filtreleme yapiliyor (secilmis spinner itemini da filtreye ekle)
            searchView.setQuery(_resultFilterText, true);

            searchView.setQuery(_filterText, false);
            searchView.setIconified(false);

            searchView.clearFocus();
//            searchView.setQuery(_filterText, false);
        }*/
    }

    /**
     * destroyView de calisacak, fragment UI stateini kaydedecek
     * sonradan, restoreFragmentState yapilirken, burada kayit edilen UI statelere gore restore edilecek
     */
    private void saveFragmentState(){
        String _filterText = "", _filterSelectedTitleId = "";
        if(searchView != null && searchView.getQuery() != null) {
            _filterText = searchView.getQuery().toString();
        }
        //secilmis spinner itemini da filtreye ekle
        _filterSelectedTitleId = ((SpinnerDataItem) _spAppBarInfoFilterCombo1.getSelectedItem()).get_itemId();
        //_filterText += "&filter_title_id=" + _filterSelectedTitleId;

        _fragmentState = new UsersFragmentState(_filterText, _filterSelectedTitleId, DataCache.getInstance().get_userListData());
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
     * userList datasını DB den yükler
     */
    private void loadDataFromDB(){
        DataCache _datacache = DataCache.getInstance();

        //eger data, cache de varsa, oradan goster
        if (_datacache.get_userListData() != null){
//            _cachedRawData = _datacache.get_userListData();
            showUserListData(_datacache.get_userListData());
        }
        //yoksa yeniden yukle
        else {
            //loading ekranı görüntüleniyor
            ((MainActivity) getActivity()).showLoadingPanel();

            HRUsers _hrUsers = HRUsers.getInstance(context, recyclerView);
            _hrUsers.getUserList(new onGetUserList());
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
        if(_tvAppBarInfo != null) {
            _tvAppBarInfo.setVisibility(View.GONE);
            _spAppBarInfoFilterCombo1.setVisibility(View.GONE);
        }
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
    public void onEvent(onGetUserList event){
        //gelen event te error varsa isle
        if (event.getError() != null)
        {
//            ApplicationHelpers.getInstance(getContext()).handleError("UserFragment: hata: onGetUserList - " + event.getError().toString());
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), this.getClass().getName() + " hata: onGetUserList")
            );
            return;
        }

        //gelen event te data yoksa hata ver
        if (event.getData() == null)
        {
//            ApplicationHelpers.getInstance(getContext()).handleError("UserFragment: hata: onGetUserList - DATA yok");
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, "", this.getClass().getName() + " hata: onGetUserList")
            );
            return;
        }

        //userListDatayi cache e at
        DataCache.getInstance().set_userListData(event.getData());

        //userlist datasini gosterir
        showUserListData(event.getData());
        ((MainActivity) getActivity()).hideLoadingPanel();
    }

    /**
     * datayi alir ve kullaniciya gosterir
     * @param data
     */
    private void showUserListData(Object data){
        //sonradan tekrar sayfaya donulurse, tekrar bilgileri yuklememek icin datayi cache atiyoruz
        DataCache.getInstance().set_userListData(data);

        //datayi JSONArray e cevirip recycleriew e atiyoruz
        JSONArray _jaUserlist = (JSONArray) data;
        _userListData = new UserListData(_jaUserlist);
        _adapter = new UserListRecyclerViewAdapter(_userListData.ITEMS, mListener);
        recyclerView.setAdapter(_adapter);

        showRecordCount(); //kayit sayisini goster
        fillTitlesSpinner(); //titles combosunu dolduruyoruz. bu degerler cok degismedigi icin manuel doldurdum.
    }

    public Context get_context() {
        return _context;
    }

    private void fillTitlesSpinner(){
        final List<SpinnerDataItem> _spinnerDataItemList = new ArrayList<>();
        _spinnerDataItemList.add(new SpinnerDataItem("-1", "FİLTRE: Tümünü Göster"));
        _spinnerDataItemList.add(new SpinnerDataItem("26", "Amir"));
        _spinnerDataItemList.add(new SpinnerDataItem("27", "Eleman"));
        _spinnerDataItemList.add(new SpinnerDataItem("28", "Formen"));
        _spinnerDataItemList.add(new SpinnerDataItem("29", "Müdür"));
        _spinnerDataItemList.add(new SpinnerDataItem("30", "Müdür yardımcısı"));
        _spinnerDataItemList.add(new SpinnerDataItem("31", "Mühendis"));
        _spinnerDataItemList.add(new SpinnerDataItem("32", "Operatör"));
        _spinnerDataItemList.add(new SpinnerDataItem("33", "Sorumlu"));
        _spinnerDataItemList.add(new SpinnerDataItem("34", "Şef"));
        _spinnerDataItemList.add(new SpinnerDataItem("35", "Tekniker"));
        _spinnerDataItemList.add(new SpinnerDataItem("36", "Uzman"));

        ArrayAdapter arrayAdapter = new ArrayAdapter(_context, R.layout.generic_spinner_item,_spinnerDataItemList);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        _spAppBarInfoFilterCombo1.setAdapter(arrayAdapter);
        _spAppBarInfoFilterCombo1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String _itemId = _spinnerDataItemList.get(i).get_itemId();
                String _itemName = _spinnerDataItemList.get(i).get_itemName();
//                Log.i("ALİ", "spinner-> itemId: " + _itemId + " ,itemName:" + _itemName);
                String _filterText = searchView.getQuery().toString().split("&filter_title_id=")[0];
                String _filterTextFull = _filterText;

                if (!_itemId.equals("-1"))//eger bir unvan secilmisse
                    _filterTextFull += "&filter_title_id=" + _itemId;

                searchView.setQuery(_filterTextFull, true);

                if (StringHelpers.isEmptyString(_filterText)) {
                    _filterText = "";
                    searchView.setQuery(_filterText, false);
                }
                searchView.setIconified(false);
                showRecordCount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showRecordCount(){

        int _recordCount = _adapter.getItemCount();


        _tvAppBarInfo.setText("Toplam Kayit Sayisi: " + _recordCount);
//        _tvAppBarInfo.setVisibility(View.VISIBLE);

        _spAppBarInfoFilterCombo1.setVisibility(View.VISIBLE); //unvan filtresini goster
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

        //eger UI restore yapiliyorsa, filter texti ve filterTitle i  de yenile
        if(_fragmentState != null && (!TextUtils.isEmpty(_fragmentState._filterText) || !TextUtils.isEmpty(_fragmentState._filterTitleId))) {
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
        if (!query.equals(" "))
            _adapter.getFilter().filter(query);

        showRecordCount();
        String queryText = query.split("&")[0].toString();


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
        showRecordCount();
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
        void onListFragmentInteraction(UserListItem item);
    }

    private class UsersFragmentState{
        private Object _userListDataState;
        private String _filterText, _filterTitleId;

        public UsersFragmentState(String filterText, String filterTitleId, Object userListDataState){
            _filterText = filterText;
            _userListDataState = userListDataState;
            _filterTitleId = filterTitleId;
        }


        public String get_filterText() {
            return _filterText;
        }

        public String get_filterTitleId() {
            return _filterTitleId;
        }

        public Object get_userListDataState() {
            return _userListDataState;
        }
    }
}
