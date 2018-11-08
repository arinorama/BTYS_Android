package com.besiktasshipyard.mobile.btys.fragments.hr.vehicles;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.besiktasshipyard.mobile.btys.R;
import com.besiktasshipyard.mobile.btys.businessLayer.dataItems.vehicleList.VehicleListData.VehicleListItem;
import com.besiktasshipyard.mobile.btys.helpers.StringHelpers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link VehicleListItem} and makes a call to the
 * specified {@link VehicleListFragment.OnListFragmentInteractionListener}.
 */
public class VehicleListRecyclerViewAdapter extends RecyclerView.Adapter<VehicleListRecyclerViewAdapter.ViewHolder> implements Filterable {

    private final List<VehicleListItem> _originalVehicleList;
    private List<VehicleListItem> _vehicleList;
    private List<VehicleListItem> _filteredVehicleList;

    private final VehicleListFragment.OnListFragmentInteractionListener mListener;
    private VehicleFilter vehicleFilter;
    private Context _context;

    public VehicleListRecyclerViewAdapter(List<VehicleListItem> items, VehicleListFragment.OnListFragmentInteractionListener listener) {
        _vehicleList = items;
        _originalVehicleList = items;

        this._filteredVehicleList = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this._context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_vehicles, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = _vehicleList.get(position);
        holder.tvMarkModel.setText(_vehicleList.get(position).mark_model);
        holder.tvPlateNumber.setText(_vehicleList.get(position).plate_number);
        holder.tvVehicleStatus.setText(_vehicleList.get(position).status);
        if (_vehicleList.get(position).status.equals("T. Dışında"))
            holder.tvVehicleStatus.setTextColor(ContextCompat.getColor(_context, R.color.colorPrimary));
        else
            holder.tvVehicleStatus.setTextColor(ContextCompat.getColor(_context, R.color.colorAccent));

        //
        holder.tvDriver.setText("Son Sürücü: " + _vehicleList.get(position).driver);
        holder.tvLastEnterDate.setText("Giriş: " + _vehicleList.get(position).lastEnterDate);
        holder.tvLastExitDate.setText("Çıkış: " +_vehicleList.get(position).lastExitDate);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
//        return  _originalVehicleList.size();
        return _vehicleList.size();
    }

//    public int getFilteredItemCount(){
//        int _result = -1;
//        if(_isFiltered)
//            _result = _filteredVehicleList.size();
//        return _result;
//    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvMarkModel, tvPlateNumber, tvVehicleStatus, tvDriver, tvLastExitDate, tvLastEnterDate;

        public VehicleListItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvMarkModel = (TextView) view.findViewById(R.id.fragment_vehicles_mark_model);
            tvPlateNumber = (TextView) view.findViewById(R.id.fragment_vehicles_plate_number);
            tvVehicleStatus = (TextView) view.findViewById(R.id.fragment_vehicles_vehicle_status);
            tvDriver = (TextView) view.findViewById(R.id.fragment_vehicles_vehicle_driver);
            tvLastExitDate = (TextView) view.findViewById(R.id.fragment_vehicles_last_exit_date);
            tvLastEnterDate = (TextView) view.findViewById(R.id.fragment_vehicles_last_enter_date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvPlateNumber.getText() + "'";
        }
    }

    //Bu adapter'e bagli view'in search edilebilmesi icin
    @Override
    public Filter getFilter() {
        if(vehicleFilter == null)
            vehicleFilter = new VehicleFilter(this, _vehicleList);

        return vehicleFilter;
    }

    private static class VehicleFilter extends Filter {

        private final VehicleListRecyclerViewAdapter adapter;

        private final List<VehicleListItem> originalList;

        private final List<VehicleListItem> filteredList;

        private VehicleFilter(VehicleListRecyclerViewAdapter adapter, List<VehicleListItem> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = new LinkedList<>(originalList);
            this.filteredList = new ArrayList<>();
        }



        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();

            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final VehicleListItem vehicle : originalList) {
                    if(StringHelpers.stringContains(vehicle.mark_model, filterPattern)
                            || StringHelpers.stringContains(vehicle.plate_number, filterPattern)
                            )
                        filteredList.add(vehicle);
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

//        private String getNormalizedString(String inputString){
//            String _normalizedFilterPattern = Normalizer.normalize(inputString, Normalizer.Form.NFD);
//            _normalizedFilterPattern = _normalizedFilterPattern.replaceAll("[^\\p{ASCII}]", "");
//            _normalizedFilterPattern = _normalizedFilterPattern.toUpperCase();
//
//            return _normalizedFilterPattern;
//        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(adapter._filteredVehicleList != null) {
                adapter._filteredVehicleList.clear();
                adapter._vehicleList.clear();
            }

            adapter._filteredVehicleList.addAll((ArrayList<VehicleListItem>) results.values);
            adapter._vehicleList = adapter._filteredVehicleList;

            adapter.notifyDataSetChanged();
        }
    }
}

