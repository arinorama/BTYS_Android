package com.besiktasshipyard.mobile.btys;
//test commit
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.besiktasshipyard.mobile.btys.busEvents.onGetUserPhoto;
import com.besiktasshipyard.mobile.btys.businessLayer.dataItems.contractorWorkers.ContractorWorkerListData;
import com.besiktasshipyard.mobile.btys.businessLayer.dataItems.repairProjectsList.RepairProjectsListData;
import com.besiktasshipyard.mobile.btys.businessLayer.dataItems.userList.UserListData;
import com.besiktasshipyard.mobile.btys.businessLayer.dataItems.vehicleList.VehicleListData;
import com.besiktasshipyard.mobile.btys.businessLayer.hr.HRUsers;
import com.besiktasshipyard.mobile.btys.fragments.hr.contractor_workers.ContractorWorkerDetailsFragment;
import com.besiktasshipyard.mobile.btys.fragments.hr.contractor_workers.ContractorWorkersFragment;
import com.besiktasshipyard.mobile.btys.fragments.hr.users.UserDetailsFragment;
import com.besiktasshipyard.mobile.btys.fragments.hr.users.UsersFragment;
import com.besiktasshipyard.mobile.btys.fragments.hr.vehicles.VehicleListFragment;
import com.besiktasshipyard.mobile.btys.fragments.repair.projects.RepairProjectsListFragment;
import com.besiktasshipyard.mobile.btys.fragments.reports.ReportsMainActivity;
import com.besiktasshipyard.mobile.btys.fragments.welcome.WelcomeFragment;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationErrorData;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationErrorData.ApplicationErrorType;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationHelpers;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationVariables;
import com.besiktasshipyard.mobile.btys.login.Login;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , UsersFragment.OnListFragmentInteractionListener
        , UserDetailsFragment.OnFragmentInteractionListener
        , ContractorWorkersFragment.OnListFragmentInteractionListener
        , ContractorWorkerDetailsFragment.OnFragmentInteractionListener
        , VehicleListFragment.OnListFragmentInteractionListener
        , RepairProjectsListFragment.OnListFragmentInteractionListener
        , WelcomeFragment.OnFragmentInteractionListener

//        , Test1Fragment.OnFragmentInteractionListener
//        , Test2Fragment.OnFragmentInteractionListener
//        , Test3Fragment.OnFragmentInteractionListener
//        , Test4Fragment.OnFragmentInteractionListener

{

    public static final int LOGIN_REQUEST = 1;
    public static View _mainView;
//
//    private final boolean FRAGMENT_CREATE_NEW = true;
//    private final boolean FRAGMENT_CREATE_NEW_OR_EXISTING = false;
//
//    private final boolean FRAGMENT_LOAD_DATA_FROM_DB = true;
//    private final boolean FRAGMENT_DO_NOT_LOAD_DATA_FROM_DB = false;

    private final String TAG = "ali";

    private Boolean _isLoggedIn = false;

    HRUsers _hrUsers;
    NavigationView navigationView;
    View hView; //navigation view ici


    public static Context _context;

    JSONObject _login_bilgileri;
    String _currentUserName="", _currentUserSurname="", _currentUserEmail="", _photoURL="", _currentUserSectionName = "";

    //Userlist item tiklaninca calisan callback
    @Override
    public void onListFragmentInteraction(UserListData.UserListItem item) {
        String _userId = item.id;
        showUserDetails(_userId);
    }

    public void showUserDetails(String userId){
        Class fragmentClass = UserDetailsFragment.class;
        Fragment fragment = getExistingFragment(fragmentClass);
        if(fragment == null)
            fragment = UserDetailsFragment.newInstance(userId);

        showNewFragment(fragment, fragmentClass);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //yuklenici listesi tiklaninca calisiyor
    @Override
    public void onListFragmentInteraction(ContractorWorkerListData.ContractorWorkerListItem item) {
//        Log.i(TAG, "onListFragmentInteraction: Contractor: Name Surname=" + item.mark_model + " Profession Name=" + item.plate_number);
        String _contractor_worker_id = item.id;


        Class fragmentClass = UserDetailsFragment.class;
        Fragment fragment = getExistingFragment(fragmentClass);
        if(fragment == null)
            fragment = ContractorWorkerDetailsFragment.newInstance(_contractor_worker_id);


        showNewFragment(fragment, fragmentClass);
    }

    @Override
    public void onListFragmentInteraction(VehicleListData.VehicleListItem item) {

    }

    @Override
    public void onListFragmentInteraction(RepairProjectsListData.RepairProjectsListItem item) {

    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(UserListData.UserListItem item);
    }

    public void ShowMessage(String message){

        Snackbar.make(this.findViewById(R.id.content_frame), message, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //yukleme spinnerini gizle ( bunu data yuklerken kullanacagiz)
        hideLoadingPanel();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //eventbus a register ol
        EventBus.getDefault().register(this);



//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Bununla birseyler yapabilirim, ne oldugunu daha bulamadım", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);





        _context = this;//getApplicationContext();
        _hrUsers = HRUsers.getInstance(_context);

        if(get_isSavedLoginExists()) {
            getLoginBilgileriFromSharedPref();
        }else{
            //eger login olmamissa, login sayfasini ac
            if (!get_isLoggedIn()) {
                gotoLoginPage();
                return;
            }
        }

        setNavbarUserInfo(_login_bilgileri);

        loadWelcomeScreen();
    }

    private void loadWelcomeScreen(){
        if (getFragmentManager().findFragmentById(R.id.content_frame) == null) {
            displaySelectedScreen(-1); //welcome ekranini ac
        }
    }

    private boolean get_isSavedLoginExists(){
        boolean _savedLoginExists = false;

        int _loggedInUserID = ApplicationHelpers.getInstance(_context).getSavedLoggedInUserID();//getSavedLoggedInUserID();
        if(_loggedInUserID > 0)
            _savedLoginExists = true;

        return _savedLoginExists;
    }
    /**
     * daha once kaydedilmis login bilgilerini okur ve kullanici bilgilerini yazar
     */
    private void getLoginBilgileriFromSharedPref(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String _loginBilgileriString = preferences.getString("loginBilgileri", "");
        if(!_loginBilgileriString.equalsIgnoreCase(""))
        {
            //login bilgilerine gore navbar ayarla
            try {
                _login_bilgileri = new JSONObject(_loginBilgileriString);

            } catch (JSONException e) {
                ApplicationHelpers.getInstance(this).handleError(
                                new ApplicationErrorData(ApplicationErrorType.JSON_ERROR, "", e.getMessage())
                        );
            }
        }


    }

    //login sayfasini ac
    private void gotoLoginPage(){
        set_isLoggedIn(false);
        Intent _intentLogin = new Intent(this, Login.class);
        startActivityForResult(_intentLogin, LOGIN_REQUEST);
    }

    /**
     * yeni aktivite yaratıp gösterir
     * @param activityClass
     */
    private void displayActivity(Class activityClass, boolean addToBackStack){
        Intent _intentNewActivity = new Intent(this, activityClass);
//        if (!addToBackStack)
//            _intentNewActivity.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(_intentNewActivity);
    }

    /**
     * yeni aktivite yaratip aktiviteden sonuc berkler
     * sonuc: onActivityResult metodunda handle edilir
     * requestCode: genel olarak tanımlanmış bir constant olmalı: ör: GET_USER_ID gibi
     * @param activityClass
     * @param requestCode
     */
    private void displayActivityForResult(Class activityClass, int requestCode){
        Intent _intentNewActivity = new Intent(this, activityClass);
        startActivityForResult(_intentNewActivity, requestCode);
    }

    // TODO: 7.7.2017 bunu application helpers icine al
    /**
     * bilgi yukleniyor ekranini gizler
     */
    public void hideLoadingPanel(){
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

    /**
     * bilgi yukleniyor ekranini gosterir
     * gri yari seffaf bir panel ve uzerinde donen bir spinner gosterir
     */
    public void showLoadingPanel(){

        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * startActivityForResult ile cagrilan diger aktivitelerden gelen cevaplari alip islem yapan metod
     * diger aktivitelere birseyler gonderildigi zaman, cevap bu metoda gelir
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOGIN_REQUEST){
            //login başarılı
            if (resultCode==RESULT_OK){
                set_isLoggedIn(true);

                //login bilgilerini al
                String _loadMessage = data.getStringExtra(Login.EXTRA_LOGIN_BILGILERI);
                //raw response u datamap e cevirir. boylece name-value ile dataya icerigine ulasilabilecek sekle girer
                Map<String, Object> _dataMap = ApplicationHelpers.getInstance(this).getMapFromJSONString(_loadMessage);

                //login bilgilerine gore navbar ayarla
                try {
                    _login_bilgileri = new JSONObject(_dataMap.get("login_bilgileri").toString());
                    setNavbarUserInfo(_login_bilgileri);
                    saveLoginBilgileri(_login_bilgileri);
                    loadWelcomeScreen();
                } catch (JSONException e) {
//                    ApplicationHelpers.getInstance(this).handleError(e.getMessage(), findViewById(android.R.id.content));
                    ApplicationHelpers.getInstance(this).handleError(
                            new ApplicationErrorData(ApplicationErrorType.JSON_ERROR, "", e.getMessage())
                    );
                }
            }
        }
    }

    private void saveLoginBilgileri(JSONObject _login_bilgileri){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("loginBilgileri",_login_bilgileri.toString());
        editor.apply();
    }


    /**
     * Navbardaki kullanıcı adı - soyadı, bölümü ve fotoğrafını dolduran fonk.
     * @param login_bilgileri
     */
    private void setNavbarUserInfo(JSONObject login_bilgileri){


        try {
            _currentUserName = login_bilgileri.getString("name");
            _currentUserSurname = login_bilgileri.getString("surname");
            _currentUserEmail = login_bilgileri.getString("email");
            ApplicationHelpers.getInstance(this).set_currentUserId(login_bilgileri.getString("id"));
            _currentUserSectionName = login_bilgileri.getString("section_name");
            //php session icin gerekiyor
            ApplicationVariables.getInstance(_context).set_terminal_uid(Integer.valueOf(ApplicationHelpers.getInstance(this).get_currentUserId()));
        } catch (JSONException e) {
            Log.e("hata:MainActivity", "setNavbarUserInfo: " ,e);
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        hView =  navigationView.getHeaderView(0);


        //kullanıcının adı soyadı
        TextView _nav_user_name_surname = (TextView)hView.findViewById(R.id.tvNav_header_user_name_surname);
        _nav_user_name_surname.setText(_currentUserName + " " + _currentUserSurname);

        //kullanıcının bölüm bilgisi
        TextView _nav_user_name_section_name = (TextView)hView.findViewById(R.id.tvNav_header_user_section_name);
        _nav_user_name_section_name.setText(_currentUserSectionName);

        //kullanicinin resmini bul (getUserPhoto ile) ve onGetUserPhoto metodunu cagir
        _hrUsers.getUserPhoto(ApplicationHelpers.getInstance(this).get_currentUserId(), new onGetUserPhoto());
    }

    /**
     * onGetUserPhoto sonucu gelen eventi dinleyen fonk.
     * HRUsers.getUserPhoto, kullanicinin resmini bulup bunu broadcast ediyor - (onGetUserPhoto imzasi ile broadcast ediyor)
     * bu event de onGetUserPhoto imzali evente subscribe olup resmi gosteriyor
     * @param event
     */
    @Subscribe
    public void onEvent(onGetUserPhoto event){

        //gelen event te error varsa isle
        if (event.getError() != null)
        {
//            ApplicationHelpers.getInstance(this).handleError("MainActivity: hata: onGetUserPhoto - " + event.getError().toString());
            ApplicationHelpers.getInstance(this).handleError(
                    new ApplicationErrorData(ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), "MainActivity: hata: onGetUserPhoto - " + event.getError().toString())
            );
            return;
        }

        //gelen event te data yoksa hata ver
        if (event.getData() == null)
        {
//            ApplicationHelpers.getInstance(this).handleError("MainActivity: hata: onGetUserPhoto - DATA yok" );
            ApplicationHelpers.getInstance(this).handleError(
                    new ApplicationErrorData(ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), "MainActivity: hata: onGetUserPhoto - DATA yok" )
            );
            return;
        }

        //gelen datayi al
        Bitmap _imageBitmap = (Bitmap) event.getData();

        //resmi goster
        ImageView _ivUserImage = (ImageView) hView.findViewById(R.id.ivUserImage);
        _ivUserImage.setImageBitmap(_imageBitmap);
    }

    /**
     * sag ust kosedeki options menusunu yaratan metod
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_without_search, menu);

//        SearchManager manager = (SearchManager) getSystemService(_context.SEARCH_SERVICE);
//        SearchView _searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        _searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
//        _searchView.setQueryHint(getResources().getString(R.string.search));
//
//        //eger su anda gosterilen fragment, usersfragment ise, users arama yap
//        Fragment _currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
////        if(_currentFragment instanceof UsersFragment) {
//            _searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                @Override
//                public boolean onQueryTextSubmit(String query) {
//                    Log.i(TAG, "onQueryTextSubmit: " + query);
//                    return false;
//                }
//
//                @Override
//                public boolean onQueryTextChange(String newText) {
//                    Log.i(TAG, "onQueryTextChange: " + newText);
//                    return false;
//                }
//            });
////        }


        return true;
    }

    /**
     * options menusunden birsey secilince bu calisiyor
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (item.getItemId() == android.R.id.home) {
            if(drawer.isDrawerOpen(Gravity.LEFT)) {
                drawer.closeDrawer(Gravity.LEFT);
            }else{
                drawer.openDrawer(Gravity.LEFT);
            }

        }else if (item.getItemId()== R.id.action_logout) {
            logOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut(){
        ApplicationHelpers.getInstance(_context).clearCache();
        clearBackStack();
        ApplicationHelpers.getInstance(_context).clearSharedPreferencesData();
        clearUserPhoto();
        gotoLoginPage();
    }

    /**
     * User Photoyu default boş adam siluyeti yapiyoruz
     */
    private void clearUserPhoto(){
        View headerLayout = navigationView.getHeaderView(0);

        ImageView _ivUserImage = (ImageView) headerLayout.findViewById(R.id.ivUserImage);
        _ivUserImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_person_black_24dp));
    }

    /**
     * Backstack temizliyor. fragment history sifirlanmis oluyor
     */
    private void clearBackStack(){
        while (getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    /**
     * navigation menuden biri secilince bu calisiyor
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // secili menu item id
        int _selectedMenuId = item.getItemId();

        //secilmis menunun ekranini goster
        displaySelectedScreen(_selectedMenuId);

        //menuyu kapatir
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    /**
     * kisinin login olup olmadiginin kaydini tutan fonksiyonlar
     * @return
     */
    public Boolean get_isLoggedIn() {
        return _isLoggedIn;
    }

    public void set_isLoggedIn(Boolean _isLoggedIn) {
        this._isLoggedIn = _isLoggedIn;
    }

    /**
     * istenen fragmenti varsa eger daha once yaratilmislardan bulur cevirir, yoksa yeni yaratir
     * @param fragmentClass
     * @return
     */
    private Fragment getExistingFragment(Class fragmentClass){
        Fragment _resultFragment = null;

        FragmentManager _fm = getSupportFragmentManager();
        Fragment _existingFragment = _fm.findFragmentByTag(fragmentClass.getName());
        if(_existingFragment != null)
            _resultFragment = _existingFragment;

        return _resultFragment;
    }

    /**
     * menuden secilmis fragmentlari gosteren metod
     * @param itemId
     */
    private void displaySelectedScreen(int itemId) {
        Fragment fragment = null;
        Class fragmentClass = null;


        switch (itemId){
            case -1: //welcome ekrani
                fragmentClass = WelcomeFragment.class;
                fragment = getExistingFragment(fragmentClass);
                if(fragment == null)
                    fragment = new WelcomeFragment();
                break;
            case R.id.nav_my_profile:
                showUserDetails(ApplicationHelpers.getInstance(this).get_currentUserId());
                break;
//            case R.id.nav_test:
//                fragmentClass = Test1Fragment.class;
//                fragment = getExistingFragment(fragmentClass);
//                if(fragment == null)
//                    fragment = new Test1Fragment();
//                break;
            case R.id.nav_contractor_worker_list:
                fragmentClass = ContractorWorkersFragment.class;
                fragment = getExistingFragment(fragmentClass);
                if(fragment == null)
                    fragment = ContractorWorkersFragment.newInstance(true,this);
                break;
            case R.id.nav_userlist:
                fragmentClass = UsersFragment.class;
                fragment = getExistingFragment(fragmentClass);
                if(fragment == null)
                    fragment = UsersFragment.newInstance(true, this);
                break;
            case R.id.nav_vehiclelist:
                fragmentClass = VehicleListFragment.class;
                fragment = getExistingFragment(fragmentClass);
                if(fragment == null)
                    fragment = VehicleListFragment.newInstance(true, this);
                break;
            case R.id.nav_reports:
                displayActivity(ReportsMainActivity.class, false);
                return;
            case R.id.nav_repair_projects:
                fragmentClass = UsersFragment.class;
                fragment = getExistingFragment(fragmentClass);
                if(fragment == null)
                    fragment = RepairProjectsListFragment.newInstance(true, this);
                break;
        }

        showNewFragment(fragment, fragmentClass);
    }
    /***********************************************************/

    private void showNewFragment(Fragment fragment, Class fragmentClass){

        if (fragment != null) {
            ApplicationHelpers.hideSoftKeyboard(this);
            FragmentManager _fragmentManager = getSupportFragmentManager();
            FragmentTransaction _fragmentTransaction = _fragmentManager.beginTransaction();

            // TODO: 7.7.2017 fragment ADD REMOVE ORNEGI
//            ArrayList<Fragment> _currentFragments = (ArrayList<Fragment>) _fragmentManager.getFragments();
//            if(_currentFragments != null) {
//                for (Fragment _f : _currentFragments) {
//                    //aktif olmayan fakat bir sekilde daha onceden gosterilmis ve listeye eklenmis olan fragmentlar, silinmiyor, null set ediliyor
//                    //bu yuzden getFragments size 3 veriyor ama sadece 2 tane olabiliyor icinde. Bunun icin null check yapiyoruz
//                    if(_f != null)
//                        _fragmentTransaction.hide(_f);
//                }
//            }

            //_fragmentTransaction.add(R.id.content_frame, fragment, fragmentClass.getName());
            _fragmentTransaction.replace(R.id.content_frame,fragment);

            _fragmentTransaction.addToBackStack(fragment.getClass().getName());
            _fragmentTransaction.commit();
        }
    }




}
