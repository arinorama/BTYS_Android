package com.besiktasshipyard.mobile.btys.fragments.reports;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.besiktasshipyard.mobile.btys.R;
import com.besiktasshipyard.mobile.btys.businessLayer.dataItems.reports.ReportListData.ReportListItem;
import com.besiktasshipyard.mobile.btys.fragments.reports.ReportListFragment;
import com.besiktasshipyard.mobile.btys.helpers.StringHelpers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ReportListItem} and makes a call to the
 * specified {@link ReportListFragment.OnListFragmentInteractionListener
 */
public class ReportListRecyclerViewAdapter extends RecyclerView.Adapter<ReportListRecyclerViewAdapter.ViewHolder> implements Filterable {

    private final List<ReportListItem> _originalReportList;
    private List<ReportListItem> _ReportList;
    private List<ReportListItem> _filteredReportList;

    private final ReportListFragment.OnListFragmentInteractionListener mListener;
    private ReportFilter ReportFilter;

    public ReportListRecyclerViewAdapter(List<ReportListItem> items, ReportListFragment.OnListFragmentInteractionListener listener) {
        _ReportList = items;
        _originalReportList = items;

        this._filteredReportList = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_reports, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = _ReportList.get(position);
        holder.tvReportTitle.setText(_ReportList.get(position).title);
        holder.tvReportNotes.setText(_ReportList.get(position).notes);

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
//        return  _originalReportList.size();
        return _ReportList.size();
    }

//    public int getFilteredItemCount(){
//        int _result = -1;
//        if(_isFiltered)
//            _result = _filteredReportList.size();
//        return _result;
//    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvReportTitle;
        public final TextView tvReportNotes;
        public ReportListItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvReportTitle = (TextView) view.findViewById(R.id.fragment_reports_title);
            tvReportNotes = (TextView) view.findViewById(R.id.fragment_reports_notes);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvReportTitle.getText() + "'";
        }
    }

    //Bu adapter'e bagli view'in search edilebilmesi icin
    @Override
    public Filter getFilter() {
        if(ReportFilter == null)
            ReportFilter = new ReportFilter(this, _ReportList);

        return ReportFilter;
    }

    private static class ReportFilter extends Filter {

        private final ReportListRecyclerViewAdapter adapter;

        private final List<ReportListItem> originalList;

        private final List<ReportListItem> filteredList;

        private ReportFilter(ReportListRecyclerViewAdapter adapter, List<ReportListItem> originalList) {
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
                for (final ReportListItem Report : originalList) {
                    if(StringHelpers.stringContains(Report.title, filterPattern)
//                            || StringHelpers.stringContains(Report.plate_number, filterPattern)
                            )
                        filteredList.add(Report);
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(adapter._filteredReportList != null) {
                adapter._filteredReportList.clear();
                adapter._ReportList.clear();
            }

            adapter._filteredReportList.addAll((ArrayList<ReportListItem>) results.values);
            adapter._ReportList = adapter._filteredReportList;

            adapter.notifyDataSetChanged();
        }
    }
}

