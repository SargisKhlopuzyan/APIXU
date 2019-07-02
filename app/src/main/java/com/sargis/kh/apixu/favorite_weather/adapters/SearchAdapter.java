package com.sargis.kh.apixu.favorite_weather.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sargis.kh.apixu.R;
import com.sargis.kh.apixu.databinding.LayoutRecyclerViewItemSearchBinding;
import com.sargis.kh.apixu.enums.SearchStateMode;
import com.sargis.kh.apixu.models.search.SearchDataModel;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ItemViewHolder> {

    public void setSearchStateMode(SearchStateMode searchStateMode) {

    }

    public interface SearchItemSelectedInterface {
        void onSearchItemClicked(SearchDataModel searchDataModel);
    }

    private SearchItemSelectedInterface searchItemSelectedInterface;

    private List<SearchDataModel> searchDataModels;

    public SearchAdapter(SearchItemSelectedInterface searchItemSelectedInterface) {
        this.searchItemSelectedInterface = searchItemSelectedInterface;
        searchDataModels = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutRecyclerViewItemSearchBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.layout_recycler_view_item_search,
                parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bindData(searchDataModels.get(position));
    }

    @Override
    public int getItemCount() {
        return searchDataModels.size();
    }

    public List<SearchDataModel> getSearchDataModels() {
        return searchDataModels;
    }

    public void updateData(List<SearchDataModel> searchDataModels) {
        this.searchDataModels.clear();
        if (searchDataModels != null) {
//            this.searchDataModels.addAll(searchDataModels);
            this.searchDataModels = searchDataModels;
        }
        notifyDataSetChanged();
    }

    public void clearData() {
        this.searchDataModels.clear();
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private LayoutRecyclerViewItemSearchBinding binding;

        public ItemViewHolder(LayoutRecyclerViewItemSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindData(final SearchDataModel searchDataModel) {
            binding.setName(searchDataModel.name);
            binding.setOnItemClickListener(v -> searchItemSelectedInterface.onSearchItemClicked(searchDataModel));
        }
    }
}