package com.besiktasshipyard.mobile.btys.fragments.hr.contractor_workers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.besiktasshipyard.mobile.btys.MainActivity;
import com.besiktasshipyard.mobile.btys.R;
import com.besiktasshipyard.mobile.btys.busEvents.onGetContractorWorkerById;
import com.besiktasshipyard.mobile.btys.busEvents.onGetContractorWorkerPhoto;
import com.besiktasshipyard.mobile.btys.businessLayer.hr.ContractorWorkers;
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
 * {@link ContractorWorkerDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContractorWorkerDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContractorWorkerDetailsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CONTRACTOR_WORKER_ID = "param1";

    private String contractorWorkerId;

    private OnFragmentInteractionListener mListener;
    private Context _context = null;

    private EditText _etUserName, _etSectionName, _etDutyName, _etAddress;
    private Button _btnTel1, _btnTel2, _btnEmail;
    private ImageView _ivUserImage;

    private String TITLE = "Kişi Bilgileri";

    public ContractorWorkerDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param contractorWorkerId Parameter 1.
     * @return A new instance of fragment UserDetailsFragment.
     */
    public static ContractorWorkerDetailsFragment newInstance(String contractorWorkerId) {
        ContractorWorkerDetailsFragment fragment = new ContractorWorkerDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTRACTOR_WORKER_ID, contractorWorkerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            contractorWorkerId = getArguments().getString(ARG_CONTRACTOR_WORKER_ID);
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

        _btnTel1.setOnClickListener(_phoneOnClickListener);
        _btnTel2.setOnClickListener(_phoneOnClickListener);


        //loading ekranı görüntüleniyor
        ((MainActivity) getActivity()).showLoadingPanel();

        //data çekme işlemi. sonucunu onEvent(onGetUserList event) ile handle ediyoruz

//        //HRUsers _hrUsers = new HRUsers(context);
//        HRUsers _hrUsers = HRUsers.getInstance(context);
//        //kullanici bilgilerini gosterir
//        _hrUsers.getUserById(contractorWorkerId, new onGetUserById());
//        //kullanici resmini gosterir
//        _hrUsers.getUserPhoto(contractorWorkerId, new onGetUserPhoto());

        ContractorWorkers _contractorWorkers = ContractorWorkers.getInstance(context);
        //yuklenici calisani bilgilerini gosterir
        _contractorWorkers.getContractorWorkerById(contractorWorkerId, new onGetContractorWorkerById());
        //kullanici resmini gosterir
        _contractorWorkers.getContractorWorkerPhoto(contractorWorkerId, new onGetContractorWorkerPhoto());

        return view;
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


    /**
     * gelen datayı handle eden callback
     * @param event
     */
    @Subscribe
    public void onEvent(onGetContractorWorkerById event){
        //gelen event te error varsa isle
        if (event.getError() != null)
        {
//            ApplicationHelpers.getInstance(getContext()).handleError("ContractorWorkerDetailsFragment: hata: onGetContractorWorkerById - " + event.getError().toString());
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, "", this.getClass().getName() + " hata: onGetContractorWorkerById")
            );
            return;
        }

        //gelen event te data yoksa hata ver
        if (event.getData() == null)
        {
//            ApplicationHelpers.getInstance(getContext()).handleError("ContractorWorkerDetailsFragment: hata: onGetContractorWorkerById - DATA yok");
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, "", this.getClass().getName() + " hata: onGetContractorWorkerById")
            );
            return;
        }

        JSONObject _joContractorWorkerDetails = (JSONObject) event.getData();
        try {
            displayContractorWorkerDetails(_joContractorWorkerDetails);
        } catch (JSONException e) {
//            ApplicationHelpers.getInstance(getContext()).handleError("ContractorWorkerDetailsFragment: hata: displayContractorWorkerDetails - DATA JSON Hatası");
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.JSON_ERROR, "", this.getClass().getName() + " hata: displayContractorWorkerDetails - DATA JSON Hatası")
            );
        }

        ((MainActivity) getActivity()).hideLoadingPanel();
    }

    /**
     * onGetContractorWorkerPhoto sonucu gelen eventi dinleyen fonk.
     * HRUsers.getUserPhoto, kullanicinin resmini bulup bunu broadcast ediyor - (onGetContractorWorkerPhoto imzasi ile broadcast ediyor)
     * bu event de onGetContractorWorkerPhoto imzali evente subscribe olup resmi gosteriyor
     * @param event
     */
    @Subscribe
    public void onEvent(onGetContractorWorkerPhoto event){

        //gelen event te error varsa isle
        if (event.getError() != null)
        {
//            ApplicationHelpers.getInstance(getContext()).handleError("UserDetailsFragment: hata: onGetContractorWorkerPhoto - " + event.getError().toString());
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), this.getClass().getName() + " hata: onGetContractorWorkerPhoto")
            );
            return;
        }

        //gelen event te data yoksa hata ver
        if (event.getData() == null)
        {
//            ApplicationHelpers.getInstance(getContext()).handleError("UserDetailsFragment: hata: onGetContractorWorkerPhoto - DATA yok" );
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, "", this.getClass().getName() + " onGetContractorWorkerPhoto - DATA yok")
            );
            return;
        }

        //gelen datayi al
        Bitmap _imageBitmap = (Bitmap) event.getData();

        //resmi goster
        displayUserImage(_imageBitmap);
    }

    private void displayContractorWorkerDetails(JSONObject contractorWorkerDetails) throws JSONException {
        String
                _userNameSurname = StringHelpers.getCapitalizedString_Fully(contractorWorkerDetails.getString("name") + " " + contractorWorkerDetails.getString("surname"),true)
                ,_contractorName = StringHelpers.getCapitalizedString_Fully(contractorWorkerDetails.getString("con_name"),true)
                , _dutyName = StringHelpers.getCapitalizedString_Fully(contractorWorkerDetails.getString("profession_name_short"),true)
                , _address = StringHelpers.getCapitalizedString_Fully(HRUsers.getAdressString(contractorWorkerDetails) ,true)
                , _tel1 = contractorWorkerDetails.getString("tel1")
                , _tel2 = contractorWorkerDetails.getString("tel2")
//                , _email = contractorWorkerDetails.getString("email")
                ;

        _etUserName.setText(_userNameSurname);
        _etSectionName.setText(_contractorName);
        _etDutyName.setText(_dutyName);
        _etAddress.setText(_address);
//        _btnEmail.setText(_email);
        _btnTel1.setText(_tel1);
        _btnTel2.setText(_tel2);

        if (_tel1.isEmpty())
            _btnTel1.setVisibility(View.INVISIBLE);
        else
            _btnTel1.setVisibility(View.VISIBLE);

        if (_tel2.isEmpty())
            _btnTel2.setVisibility(View.INVISIBLE);
        else
            _btnTel2.setVisibility(View.VISIBLE);

//        if (_email.isEmpty())
//            _btnEmail.setVisibility(View.INVISIBLE);
//        else
//            _btnEmail.setVisibility(View.VISIBLE);
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
