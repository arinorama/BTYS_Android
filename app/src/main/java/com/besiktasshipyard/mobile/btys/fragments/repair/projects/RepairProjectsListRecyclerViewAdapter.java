package com.besiktasshipyard.mobile.btys.fragments.repair.projects;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.besiktasshipyard.mobile.btys.MainActivity;
import com.besiktasshipyard.mobile.btys.R;
import com.besiktasshipyard.mobile.btys.businessLayer.dataItems.repairProjectsList.RepairProjectsListData.RepairProjectsListItem;
import com.besiktasshipyard.mobile.btys.helpers.StringHelpers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link RepairProjectsListItem} and makes a call to the
 * specified {@link RepairProjectsListFragment.OnListFragmentInteractionListener}.
 */
public class RepairProjectsListRecyclerViewAdapter extends RecyclerView.Adapter<RepairProjectsListRecyclerViewAdapter.ViewHolder> implements Filterable {

    private final List<RepairProjectsListItem> _originalRepairProjectsList;
    private List<RepairProjectsListItem> _repairProjectsList;
    private List<RepairProjectsListItem> _filteredRepairProjectsList;

    private final RepairProjectsListFragment.OnListFragmentInteractionListener mListener;
    private RepairProjectsFilter repairProjectsFilter;
    private Context _context;

    public RepairProjectsListRecyclerViewAdapter(List<RepairProjectsListItem> items, RepairProjectsListFragment.OnListFragmentInteractionListener listener) {
        _repairProjectsList = items;
        _originalRepairProjectsList = items;

        this._filteredRepairProjectsList = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this._context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_repair_projects, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
//        Log.i("ali", "position: " + Integer.toString(position));
        holder.mItem = _repairProjectsList.get(position);

        String
                _projectCode = _repairProjectsList.get(position).project_code
                ,_projectDescription = _repairProjectsList.get(position).project_description
                ,_engineerId = _repairProjectsList.get(position).engineer_id
                ,_engineer2Id = _repairProjectsList.get(position).engineer_id2
                ,_engineerNameSurname = _repairProjectsList.get(position).engineer_name_surname.replace("null","")
                ,_engineer2NameSurname = _repairProjectsList.get(position).engineer2_name_surname.replace("null","")
                ,_estimatedStartDate = _repairProjectsList.get(position).estimated_start_date.replace("01.01.1970","")
                ,_estimatedFinishDate = _repairProjectsList.get(position).estimated_finish_date.replace("01.01.1970","")
                ,_actualStartDate = _repairProjectsList.get(position).actual_start_date.replace("01.01.1970","")
                ,_actualFinishDate = _repairProjectsList.get(position).actual_finish_date.replace("01.01.1970","")
                ,_status = _repairProjectsList.get(position).status.replace("null","")
                ,_workOrderCountTotal = _repairProjectsList.get(position).work_order_count_total.replace("null","")
                ,_workOrderCountFinished = _repairProjectsList.get(position).work_order_count_finished.replace("null","")
                ,_materialPriceTL = _repairProjectsList.get(position).material_price_tl.replace("null","-")
                ;

        holder.tvProjectCode.setText(_projectCode);
        holder.tvProjectDescription.setText(_projectDescription);
        holder.tvEngineerNameSurname.setText("Proje Mühendisi: " + _engineerNameSurname);
        holder.tvEngineer2NameSurname.setText("Üretim Koordinatörü: " + _engineer2NameSurname);
        holder.tvEstimatedStartDate.setText("Tahmini Başlangıç: " + _estimatedStartDate);
        holder.tvEstimatedFinishDate.setText("Tahmini Bitiş: " + _estimatedFinishDate);
        holder.tvActualStartDate.setText("Gerçekleşen Başlangıç: " + _actualStartDate);
        holder.tvActualFinishDate.setText("Gerçekleşen Bitiş: " + _actualFinishDate);
        holder.tvStatus.setText("Durum: " + _status);
        holder.tvWorkOrderCountTotal.setText("Toplam İş Sayısı: " + _workOrderCountTotal);
        holder.tvWorkOrderCountFinished.setText("Biten İş Sayısı: " + _workOrderCountFinished);
        holder.tvMaterialPriceTL.setText("Harcanan Malzeme: " + _materialPriceTL + " tl");


        if (_repairProjectsList.get(position).status.equals("Devam Ediyor"))
            holder.tvStatus.setTextColor(ContextCompat.getColor(_context, R.color.colorPrimary));
        else
            holder.tvStatus.setTextColor(ContextCompat.getColor(_context, R.color.colorAccent));

        if(StringHelpers.isEmptyString(_engineerNameSurname))
            holder.tvEngineerNameSurname.setEnabled(false);
        else
            holder.tvEngineerNameSurname.setEnabled(true);

        if(StringHelpers.isEmptyString(_engineer2NameSurname))
            holder.tvEngineer2NameSurname.setEnabled(false);
        else
            holder.tvEngineer2NameSurname.setEnabled(true);

        holder.tvWorkOrderCountTotal.setEnabled(false);
        holder.tvWorkOrderCountFinished.setEnabled(false);
        holder.tvMaterialPriceTL.setEnabled(false);

        //project engineer tuşuna basınca kişinin sayfası açılsın
        setOnClick(holder.tvEngineerNameSurname, _engineerId);

        //project engineer tuşuna basınca kişinin sayfası açılsın
        setOnClick(holder.tvEngineer2NameSurname, _engineer2Id);

        /*holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });*/
    }

    private void setOnClick(final Button btn, final String arg){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)_context).showUserDetails(arg);
            }
        });
    }

    @Override
    public int getItemCount() {
//        return  _originalRepairProjectsList.size();
        return _repairProjectsList.size();
    }

//    public int getFilteredItemCount(){
//        int _result = -1;
//        if(_isFiltered)
//            _result = _filteredRepairProjectsList.size();
//        return _result;
//    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView
                tvProjectCode, tvProjectDescription
                , tvEstimatedStartDate, tvEstimatedFinishDate
                , tvActualStartDate, tvActualFinishDate
                , tvStatus
                , tvWorkOrderCountTotal
                , tvWorkOrderCountFinished
                , tvMaterialPriceTL
                ;
        public final Button tvEngineerNameSurname, tvEngineer2NameSurname;

        public RepairProjectsListItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvProjectCode = (TextView) view.findViewById(R.id.fragment_repair_projects_project_code);
            tvProjectDescription = (TextView) view.findViewById(R.id.fragment_repair_projects_project_desc);
            tvStatus = (TextView) view.findViewById(R.id.fragment_repair_projects_status);
            tvEngineerNameSurname = (Button) view.findViewById(R.id.fragment_repair_projects_engineer_name_surname);
            tvEngineer2NameSurname = (Button) view.findViewById(R.id.fragment_repair_projects_prd_coo_name_surname);
            tvEstimatedStartDate = (TextView) view.findViewById(R.id.fragment_repair_projects_estimated_start_date);
            tvEstimatedFinishDate = (TextView) view.findViewById(R.id.fragment_repair_projects_estimated_finish_date);
            tvActualStartDate = (TextView) view.findViewById(R.id.fragment_repair_projects_actual_start_date);
            tvActualFinishDate = (TextView) view.findViewById(R.id.fragment_repair_projects_actual_finish_date);
            tvWorkOrderCountTotal = (TextView) view.findViewById(R.id.fragment_repair_projects_work_order_count_total);
            tvWorkOrderCountFinished = (TextView) view.findViewById(R.id.fragment_repair_projects_work_order_count_finished);
            tvMaterialPriceTL = (TextView) view.findViewById(R.id.fragment_repair_projects_material_price_tl);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvProjectDescription.getText() + "'";
        }
    }

    //Bu adapter'e bagli view'in search edilebilmesi icin
    @Override
    public Filter getFilter() {
        if(repairProjectsFilter == null)
            repairProjectsFilter = new RepairProjectsFilter(this, _repairProjectsList);

        return repairProjectsFilter;
    }

    private static class RepairProjectsFilter extends Filter {

        private final RepairProjectsListRecyclerViewAdapter adapter;

        private final List<RepairProjectsListItem> originalList;

        private final List<RepairProjectsListItem> filteredList;

        private RepairProjectsFilter(RepairProjectsListRecyclerViewAdapter adapter, List<RepairProjectsListItem> originalList) {
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
                for (final RepairProjectsListItem project : originalList) {
                    if(
                            StringHelpers.stringContains(project.project_description, filterPattern)
                            || StringHelpers.stringContains(project.engineer_name_surname, filterPattern)
                            || StringHelpers.stringContains(project.engineer2_name_surname, filterPattern)
                        )
                        filteredList.add(project);
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
            if(adapter._filteredRepairProjectsList != null) {
                adapter._filteredRepairProjectsList.clear();
                adapter._repairProjectsList.clear();
            }

            adapter._filteredRepairProjectsList.addAll((ArrayList<RepairProjectsListItem>) results.values);
            adapter._repairProjectsList = adapter._filteredRepairProjectsList;

            adapter.notifyDataSetChanged();
        }
    }
}

