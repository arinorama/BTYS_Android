package com.besiktasshipyard.mobile.btys.fragments.hr.users;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.besiktasshipyard.mobile.btys.R;
import com.besiktasshipyard.mobile.btys.businessLayer.dataItems.userList.UserListData.UserListItem;
import com.besiktasshipyard.mobile.btys.helpers.StringHelpers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link UserListItem} and makes a call to the
 * specified {@link UsersFragment.OnListFragmentInteractionListener}.
 */
public class UserListRecyclerViewAdapter extends RecyclerView.Adapter<UserListRecyclerViewAdapter.ViewHolder> implements Filterable {

    private final List<UserListItem> _originalUserList;
    private List<UserListItem> _userList;
    private List<UserListItem> _filteredUserList;

    private final UsersFragment.OnListFragmentInteractionListener mListener;
    private UserFilter userFilter;

    public UserListRecyclerViewAdapter(List<UserListItem> items, UsersFragment.OnListFragmentInteractionListener listener) {
        _userList = items;
        _originalUserList = items;

        this._filteredUserList = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = _userList.get(position);
        holder.tvUserProfessionName.setText(_userList.get(position).profession_name);
        holder.tvUserNameSurname.setText(_userList.get(position).name_surname);
        holder.tvCardView_SectionName.setText(_userList.get(position).section_name);

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
        return _userList.size();
    }

//    public int getFilteredItemCount(){
//        int _result = -1;
//        if(_isFiltered)
//            _result = _filteredUserList.size();
//        return _result;
//    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvUserProfessionName;
        public final TextView tvUserNameSurname;
        public final TextView tvCardView_SectionName;
        public UserListItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvUserProfessionName = (TextView) view.findViewById(R.id.profession_name);
            tvUserNameSurname = (TextView) view.findViewById(R.id.name_surname);
            tvCardView_SectionName = (TextView) view.findViewById(R.id.tvCardView_SectionName);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvUserNameSurname.getText() + "'";
        }
    }

    //Bu adapter'e bagli view'in search edilebilmesi icin
    @Override
    public Filter getFilter() {
        if(userFilter == null)
            userFilter = new UserFilter(this, _userList);

        return userFilter;
    }

    private static class UserFilter extends Filter {

        private final UserListRecyclerViewAdapter adapter;

        private final List<UserListItem> originalList;

        private final List<UserListItem> filteredList;

        private UserFilter(UserListRecyclerViewAdapter adapter, List<UserListItem> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = new LinkedList<>(originalList);
            this.filteredList = new ArrayList<>();
        }



        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();

            final FilterResults results = new FilterResults();

            if (constraint.length() == 0 || TextUtils.equals(constraint.toString(),"&filter_title_id=-1")) {
                filteredList.addAll(originalList);
            } else {
                //filtrede eger title_id spinner filtresi de varsa onlari ayir
                String[] constraintSplitArray = constraint.toString().split("&filter_title_id=");

                final String filterPattern = constraintSplitArray[0].toLowerCase().trim();
                String _filterTitleIdTemp = "";
                if (constraintSplitArray.length > 1)
                    _filterTitleIdTemp = constraintSplitArray[1].toLowerCase().trim();

                final String _filterTitleId = _filterTitleIdTemp;

//                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final UserListItem user : originalList) {
                    Boolean _filterMatchedTitle = false;
                    Boolean _filterMatchedSearchString = false;

                    if(TextUtils.equals(user.title_id, _filterTitleId))
                        _filterMatchedTitle = true;

                    if(!TextUtils.isEmpty(filterPattern) && StringHelpers.stringContains(user.name_surname, filterPattern))
                        _filterMatchedSearchString = true;

                    if(_filterMatchedTitle || _filterMatchedSearchString)
                        filteredList.add(user);
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
            if(adapter._filteredUserList != null) {
                adapter._filteredUserList.clear();
                adapter._userList.clear();
            }

            adapter._filteredUserList.addAll((ArrayList<UserListItem>) results.values);
            adapter._userList = adapter._filteredUserList;

            adapter.notifyDataSetChanged();
        }
    }
}

