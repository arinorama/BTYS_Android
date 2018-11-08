package com.besiktasshipyard.mobile.btys.fragments.hr.contractor_workers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.besiktasshipyard.mobile.btys.R;
import com.besiktasshipyard.mobile.btys.businessLayer.dataItems.contractorWorkers.ContractorWorkerListData.ContractorWorkerListItem;
import com.besiktasshipyard.mobile.btys.helpers.StringHelpers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ContractorWorkerListItem} and makes a call to the
 * specified {@link ContractorWorkersFragment.OnListFragmentInteractionListener}.
 */
public class ContractorWorkerListListRecyclerViewAdapter extends RecyclerView.Adapter<ContractorWorkerListListRecyclerViewAdapter.ViewHolder> implements Filterable {

    private final List<ContractorWorkerListItem> _originalContractorWorkerList;
    private List<ContractorWorkerListItem> _contractorWorkerList;
    private List<ContractorWorkerListItem> _filteredContractorWorkerList;

    private final ContractorWorkersFragment.OnListFragmentInteractionListener mListener;
    private ContractorWorkerFilter contractorWorkerFilter;

    public ContractorWorkerListListRecyclerViewAdapter(List<ContractorWorkerListItem> items, ContractorWorkersFragment.OnListFragmentInteractionListener listener) {
        _contractorWorkerList = items;
        _originalContractorWorkerList = items;

        this._filteredContractorWorkerList = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contractor_workers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = _contractorWorkerList.get(position);
        holder.tvContractorWorkerProfessionName.setText(_contractorWorkerList.get(position).profession_name);
        holder.tvContractorWorkerNameSurname.setText(_contractorWorkerList.get(position).name_surname);
        holder.tvContractorWorkerContractorName.setText(_contractorWorkerList.get(position).contractor_name);

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
//        return  _originalUserList.size();
        return _contractorWorkerList.size();
    }

//    public int getFilteredItemCount(){
//        int _result = -1;
//        if(_isFiltered)
//            _result = _filteredUserList.size();
//        return _result;
//    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvContractorWorkerProfessionName;
        public final TextView tvContractorWorkerNameSurname;
        public final TextView tvContractorWorkerContractorName;
        public ContractorWorkerListItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvContractorWorkerProfessionName = (TextView) view.findViewById(R.id.fragment_contractor_worker_profession_name);
            tvContractorWorkerNameSurname = (TextView) view.findViewById(R.id.fragment_contractor_worker_name_surname);
            tvContractorWorkerContractorName = (TextView) view.findViewById(R.id.fragment_contractor_worker_contractor_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvContractorWorkerNameSurname.getText() + "'";
        }
    }

    //Bu adapter'e bagli view'in search edilebilmesi icin
    @Override
    public Filter getFilter() {
        if(contractorWorkerFilter == null)
            contractorWorkerFilter = new ContractorWorkerFilter(this, _contractorWorkerList);

        return contractorWorkerFilter;
    }

    private static class ContractorWorkerFilter extends Filter {

        private final ContractorWorkerListListRecyclerViewAdapter adapter;

        private final List<ContractorWorkerListItem> originalList;

        private final List<ContractorWorkerListItem> filteredList;

        private ContractorWorkerFilter(ContractorWorkerListListRecyclerViewAdapter adapter, List<ContractorWorkerListItem> originalList) {
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
                for (final ContractorWorkerListItem contractor_worker : originalList) {
                    if(StringHelpers.stringContains(contractor_worker.name_surname, filterPattern)
//                            || StringHelpers.stringContains(user.plate_number, filterPattern)
                            )
                        filteredList.add(contractor_worker);
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
            if(adapter._filteredContractorWorkerList != null) {
                adapter._filteredContractorWorkerList.clear();
                adapter._contractorWorkerList.clear();
            }

            adapter._filteredContractorWorkerList.addAll((ArrayList<ContractorWorkerListItem>) results.values);
            adapter._contractorWorkerList = adapter._filteredContractorWorkerList;

            adapter.notifyDataSetChanged();
        }
    }
}

