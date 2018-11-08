package com.besiktasshipyard.mobile.btys.fragments.hr.users;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.besiktasshipyard.mobile.btys.MainActivity;
import com.besiktasshipyard.mobile.btys.R;
import com.besiktasshipyard.mobile.btys.busEvents.onGetUserById;
import com.besiktasshipyard.mobile.btys.busEvents.onGetUserPhoto;
import com.besiktasshipyard.mobile.btys.businessLayer.hr.HRUsers;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationErrorData;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationHelpers;
import com.besiktasshipyard.mobile.btys.helpers.StringHelpers;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDetailsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER_ID = "param1";

    private String userId;

    private OnFragmentInteractionListener mListener;
    private Context _context = null;

    private EditText _etUserName, _etSectionName, _etDutyName, _etAddress, _etStatus, _etLastEnter, _etLastExit, _etEmploymentDate, _etBloodGroup, _etLastGraduatedSchoolDate;
    private Button _btnTel1, _btnTel2, _btnEmail, _btnLeaveRightCount, _btnMazeretRightCount, _btnTelInternalLine, _btnTelCompanyLine, _btnTelShortCode;
    private ImageView _ivUserImage;

    private String TITLE = "Kullanıcı Bilgileri";

    public UserDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userId Parameter 1.
     * @return A new instance of fragment UserDetailsFragment.
     */
    public static UserDetailsFragment newInstance(String userId) {
        UserDetailsFragment fragment = new UserDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        //eventbus register - eventbus callback handle edebilmek icin once register olmak gerek
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        //basligi yaz
        ApplicationHelpers.setFragmentTitle((AppCompatActivity) getActivity(),TITLE);

    }

    @Override
    public void onPause() {
        super.onPause();
        //eventbus register - eventbus callback handle edebilmek icin once register olmak gerek
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

        //basligi sil
        ApplicationHelpers.setFragmentTitle((AppCompatActivity) getActivity(),"");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_details, container, false);
        Context context = view.getContext();

        _ivUserImage = (ImageView) view.findViewById(R.id.imgUserPicture) ;
        _etUserName = (EditText) view.findViewById(R.id.etUsername);
        _etSectionName = (EditText) view.findViewById(R.id.etSectionName);
        _etDutyName = (EditText) view.findViewById(R.id.etDutyName);
        _btnEmail = (Button) view.findViewById(R.id.btnEmail);
        _etAddress = (EditText) view.findViewById(R.id.etAddress);
        _btnTel1 = (Button) view.findViewById(R.id.btnTel1);
        _btnTel2 = (Button) view.findViewById(R.id.btnTel2);
        _btnLeaveRightCount = (Button) view.findViewById(R.id.fragment_user_details_btnLeaveRightCount);
        _btnMazeretRightCount = (Button) view.findViewById(R.id.fragment_user_details_btnMazeretRightCount);
        _etStatus = (EditText) view.findViewById(R.id.etStatus);
        _etLastEnter = (EditText) view.findViewById(R.id.etLastEnter);
        _etLastExit = (EditText) view.findViewById(R.id.etLastExit);
        _etEmploymentDate = (EditText) view.findViewById(R.id.etEmploymentDate);
        _etBloodGroup = (EditText) view.findViewById(R.id.etBloodGroup);
        _etLastGraduatedSchoolDate = (EditText) view.findViewById(R.id.etLastGraduatedSchoolDate);
        _btnTelCompanyLine = (Button) view.findViewById(R.id.btnTelCompanyLine);
        _btnTelShortCode = (Button) view.findViewById(R.id.btnTelShortCode);
        _btnTelInternalLine = (Button) view.findViewById(R.id.btnTelInternalLine);

        //telefon namarasi dugmelerine basinca telefon etsin.
        _btnTel1.setOnClickListener(_phoneOnClickListener);
        _btnTel2.setOnClickListener(_phoneOnClickListener);
        _btnTelCompanyLine.setOnClickListener(_phoneOnClickListener);

        //email dugmesine basinca email atsin
        _btnEmail.setOnClickListener(_emailOnClickListener);



        //loading ekranı görüntüleniyor
        //((MainActivity) getActivity()).showLoadingPanel();

        //data çekme işlemi. sonucunu onEvent(onGetUserList event) ile handle ediyoruz

        //HRUsers _hrUsers = new HRUsers(context);
        HRUsers _hrUsers = HRUsers.getInstance(context);
        //kullanici bilgilerini gosterir
        _hrUsers.getUserById(userId, new onGetUserById(), true); //son giris cikis hareketlerini de al
        //kullanici resmini gosterir
        _hrUsers.getUserPhoto(userId, new onGetUserPhoto());

        return view;//inflater.inflate(R.layout.fragment_user_details, container, false);
    }

        /**
     * Telefon numarası yazan tuşlara pasınca o kişiyi arayan metod
     */
    View.OnClickListener _phoneOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String _phoneNumber = "tel:" + String.valueOf (((Button) v).getText());

            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse(_phoneNumber));
            //callIntent.setData(Uri.parse("tel:123456789"));
            startActivity(callIntent);
        }
    };

    View.OnClickListener _emailOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String _mailTo = String.valueOf(((Button) v).getText());

            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto",_mailTo, null));
            intent.putExtra(Intent.EXTRA_SUBJECT, "");
            intent.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(Intent.createChooser(intent, "Choose an Email client :"));
        }
    };

    /**
     * gelen datayı handle eden callback
     * @param event
     */
    @Subscribe
    public void onEvent(onGetUserById event){
        //gelen event te error varsa isle
        if (event.getError() != null)
        {
//            ApplicationHelpers.getInstance(getContext()).handleError("UserFragment: hata: onGetUserById - " + event.getError().toString());
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), this.getClass().getName() + " : hata: onGetUserById")
            );
            return;
        }

        //gelen event te data yoksa hata ver
        if (event.getData() == null)
        {
//            ApplicationHelpers.getInstance(getContext()).handleError("UserFragment: hata: onGetUserById - DATA yok");
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, "", this.getClass().getName() + " : hata: onGetUserById")
            );
            return;
        }

        JSONObject _joUserDetails = (JSONObject) event.getData();
        try {
            displayUserDetails(_joUserDetails);
        } catch (JSONException e) {
//            ApplicationHelpers.getInstance(getContext()).handleError("UserFragment: hata: displayUserDetails - DATA JSON Hatası");
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.JSON_ERROR, "", this.getClass().getName() + " hata: displayUserDetails - DATA JSON Hatası")
            );
        }

        ((MainActivity) getActivity()).hideLoadingPanel();
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
//            ApplicationHelpers.getInstance(getContext()).handleError("UserDetailsFragment: hata: onGetUserPhoto - " + event.getError().toString());
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), this.getClass().getName() + " hata: onGetUserPhoto")
            );
            return;
        }

        //gelen event te data yoksa hata ver
        if (event.getData() == null)
        {
//            ApplicationHelpers.getInstance(getContext()).handleError("UserDetailsFragment: hata: onGetUserPhoto - DATA yok" );
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, "", this.getClass().getName() + " hata: onGetUserPhoto")
            );
            return;
        }

        //gelen datayi al
        Bitmap _imageBitmap = (Bitmap) event.getData();

        //resmi goster
        displayUserImage(_imageBitmap);
    }

    private void displayUserDetails(JSONObject userDetails) throws JSONException {
        String
                _userNameSurname = StringHelpers.getCapitalizedString_Fully(userDetails.getString("name") + " " + userDetails.getString("surname"),true)
                ,_sectionName = StringHelpers.getCapitalizedString_Fully(userDetails.getString("bolum_adi"),true)
                , _dutyName = StringHelpers.getCapitalizedString_Fully(userDetails.getString("pozisyon_adi"),true)
                , _email = StringHelpers.getNormalizedString(userDetails.getString("email"), StringHelpers.ConversionType.TO_LOWERCASE)
                , _address = StringHelpers.getCapitalizedString_Fully(HRUsers.getAdressString(userDetails) ,true)
                , _employmentDate = StringHelpers.getCapitalizedString_Fully(userDetails.getString("employment_date_formatted"),true)
                , _bloodGroup = StringHelpers.getCapitalizedString_Fully(userDetails.getString("blood_group_name"),true)
                , _lastGraduatedSchool = StringHelpers.getCapitalizedString_Fully(userDetails.getString("user_last_graduated_school"),true)

                , _tel1 = StringHelpers.checkAndReplaceEmptyStringWith(userDetails.getString("tel1"), "")
                , _tel2 = StringHelpers.checkAndReplaceEmptyStringWith(userDetails.getString("tel2"), "")
                , _telCompanyLine = StringHelpers.checkAndReplaceEmptyStringWith(userDetails.getString("tel_company_line"), "")
                , _telInternalLine = StringHelpers.checkAndReplaceEmptyStringWith(userDetails.getString("tel_internal_line"), "")
                , _telShortCode = StringHelpers.checkAndReplaceEmptyStringWith(userDetails.getString("tel_short_code"), "")

                ;

        //izin bilgileri
        JSONObject _userLeaveData =  userDetails.getJSONObject("user_leave");
        int
                _leave_right_count = _userLeaveData.getInt("leave_right_count")
                ,_leave_count = _userLeaveData.getInt("leave_count")
                ,_leave_right_count_mazeret = _userLeaveData.getInt("leave_right_count_mazeret")
                ,_leave_count_mazeret = _userLeaveData.getInt("leave_count_mazeret")
                ,_kge_leave_right_count = _userLeaveData.getInt("kge_leave_right")

                ,_user_leave_left = _leave_right_count + _kge_leave_right_count - _leave_count //(yillik izin + kge izinleri) - kullanilan izin = izin hakki
                ,_user_mazeret_left = _leave_right_count_mazeret - _leave_count_mazeret
                ;


        // _lastGraduatedSchool = StringHelpers.getCapitalizedString_Fully(userDetails.getString("last_graduated_school"),true)
        //JSONObject _userLastGraduatedSchool =  userDetails.getJSONObject("user_last_graduated_school");
        //String _lastGraduatedSchoolName = _userLastGraduatedSchool.getString("durum");

        JSONObject _userLastEnterExitData =  userDetails.getJSONObject("user_last_enter_exit");
        //durum bilgileri (iceride - dısarida, son giris, son cikis)
        String
                _personInOrOut = _userLastEnterExitData.getString("durum")
                ,_lastEnter = _userLastEnterExitData.getString("giris")
                ,_lastExit = _userLastEnterExitData.getString("cikis")
                ;

        _etUserName.setText(_userNameSurname);
        _etSectionName.setText(_sectionName);
        _etDutyName.setText(_dutyName);
        _etEmploymentDate.setText(_employmentDate);
        _etBloodGroup.setText(_bloodGroup);
        _etLastGraduatedSchoolDate.setText(_lastGraduatedSchool);
        _btnEmail.setText(_email);
        _etAddress.setText(_address);
        _btnTel1.setText(_tel1);
        _btnTel2.setText(_tel2);
        _btnLeaveRightCount.setText("Yıllık İzin: " + _user_leave_left);
        _btnMazeretRightCount.setText("Mazeret İzni: " + _user_mazeret_left);
        _btnTelCompanyLine.setText(_telCompanyLine);
        _btnTelInternalLine.setText(_telInternalLine);
        _btnTelShortCode.setText(_telShortCode);

        _etStatus.setText(_personInOrOut);
        if (_personInOrOut.equals("T. Dışında"))
            _etStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        else
            _etStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));


        _etLastEnter.setText(_lastEnter);
        _etLastExit.setText(_lastExit);

        // eger telefon numaralari bossa, gosterme
        if (_telShortCode.isEmpty())
            _btnTelShortCode.setVisibility(View.INVISIBLE);
        else
            _btnTelShortCode.setVisibility(View.VISIBLE);

        if (_telInternalLine.isEmpty())
            _btnTelInternalLine.setVisibility(View.INVISIBLE);
        else
            _btnTelInternalLine.setVisibility(View.VISIBLE);

        if (_telCompanyLine.isEmpty())
            _btnTelCompanyLine.setVisibility(View.INVISIBLE);
        else
            _btnTelCompanyLine.setVisibility(View.VISIBLE);

        if (_tel1.isEmpty())
            _btnTel1.setVisibility(View.INVISIBLE);
        else
            _btnTel1.setVisibility(View.VISIBLE);

        if (_tel2.isEmpty())
            _btnTel2.setVisibility(View.INVISIBLE);
        else
            _btnTel2.setVisibility(View.VISIBLE);

        //email yoksa gosterme
        if (_email.isEmpty())
            _btnEmail.setVisibility(View.INVISIBLE);
        else
            _btnEmail.setVisibility(View.VISIBLE);

    }

    private void displayUserImage(Bitmap _userImage){
        //resmi goster
        _ivUserImage.setImageBitmap(_userImage);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _context = context;
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
        void onFragmentInteraction(Uri uri);
    }
}
