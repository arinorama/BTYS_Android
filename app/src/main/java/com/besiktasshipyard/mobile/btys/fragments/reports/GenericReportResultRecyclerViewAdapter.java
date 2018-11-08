package com.besiktasshipyard.mobile.btys.fragments.reports;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.besiktasshipyard.mobile.btys.R;
import com.besiktasshipyard.mobile.btys.businessLayer.dataItems.reports.GenericReportResultData.GenericReportResultItem;
import com.besiktasshipyard.mobile.btys.fragments.reports.GenericReportResultFragment;
import com.besiktasshipyard.mobile.btys.helpers.StringHelpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link GenericReportResultItem} and makes a call to the
 * specified {@link ReportListFragment.OnListFragmentInteractionListener
 */
public class GenericReportResultRecyclerViewAdapter extends RecyclerView.Adapter<GenericReportResultRecyclerViewAdapter.ViewHolder> implements Filterable {

    private final List<GenericReportResultItem> _originalGenericReportList;
    private List<GenericReportResultItem> _genericReportList;
    private List<GenericReportResultItem> _filteredGenericReportList;

    private final GenericReportResultFragment.OnListFragmentInteractionListener mListener;
    private GenericReportFilter GenericReportFilter;

    private Context _context;

    public GenericReportResultRecyclerViewAdapter(List<GenericReportResultItem> items, GenericReportResultFragment.OnListFragmentInteractionListener listener) {
        _genericReportList = items;
        _originalGenericReportList = items;

        this._filteredGenericReportList = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        _context = parent.getContext();
        View view = LayoutInflater.from(_context)
                .inflate(R.layout.fragment_generic_report_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = _genericReportList.get(position);
        JSONObject _joListItem = holder.mItem._genericJSONObject;
        clearViewHolder(holder);
        addReportItem(holder, _joListItem);
    }

    @Override
    public int getItemCount() {
        return _genericReportList.size();
    }

    private void clearViewHolder(ViewHolder holder){
        holder._itemLayout.removeAllViews();
    }

    /**
     * generic rapora item ekler
     * JSON objesi gonderilen nesnenin özelliklerini ve değerini alır ve
     * holder ile gönderilen containerin icine sokar
     * @param holder
     * @param reportItem
     */
    private void addReportItem(ViewHolder holder, JSONObject reportItem){
        int _idNo = 0;
        for(Iterator<String> keys = reportItem.keys(); keys.hasNext();) {
            try {
                String _property = keys.next();
                //RAPOR_TARIHI, rapor tablolarinda kullandigim raporun tarihini tutan alan,
                //bunu gosterme
                    if(!_property.equals("RAPOR_TARIHI")) {
                    String _value = String.valueOf(reportItem.get(_property));
                    TextView _tvItem = new TextView(_context);
                    _tvItem.setText(_property + ": " + _value);
                    _idNo += 1;
                    RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    _tvItem.setId(_idNo);
                    if (_idNo > 1) {
                        p.addRule(RelativeLayout.BELOW, _idNo - 1);
                        _tvItem.setLayoutParams(p);
                    }
                    holder._itemLayout.addView(_tvItem);
                }
            } catch (JSONException e) {
                Log.i("ali", "GenericReportResultData: JSON");
            }
        }
    }

//    public int getFilteredItemCount(){
//        int _result = -1;
//        if(_isFiltered)
//            _result = _filteredGenericReportList.size();
//        return _result;
//    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public GenericReportResultItem mItem;

        public RelativeLayout _itemLayout;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            _itemLayout = (RelativeLayout) view.findViewById(R.id.tvGenericReportListItemLayout);
        }

        @Override
        public String toString() {
            return super.toString() + " 'item'";
        }
    }

    //Bu adapter'e bagli view'in search edilebilmesi icin
    @Override
    public Filter getFilter() {
        if(GenericReportFilter == null)
            GenericReportFilter = new GenericReportFilter(this, _genericReportList);

        return GenericReportFilter;
    }

    private static class GenericReportFilter extends Filter {

        private final GenericReportResultRecyclerViewAdapter adapter;

        private final List<GenericReportResultItem> originalList;

        private final List<GenericReportResultItem> filteredList;

        private GenericReportFilter(GenericReportResultRecyclerViewAdapter adapter, List<GenericReportResultItem> originalList) {
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
                for (final GenericReportResultItem reportItem : originalList) {
                    if(StringHelpers.checkIfJSONObjectContainsString(reportItem._genericJSONObject, filterPattern))
                        filteredList.add(reportItem);
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(adapter._filteredGenericReportList != null) {
                adapter._filteredGenericReportList.clear();
                adapter._genericReportList.clear();
            }

            adapter._filteredGenericReportList.addAll((ArrayList<GenericReportResultItem>) results.values);
            adapter._genericReportList = adapter._filteredGenericReportList;

            adapter.notifyDataSetChanged();
        }
    }
}

