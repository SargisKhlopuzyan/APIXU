package com.sargis.kh.apixu.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sargis.kh.apixu.R;
import com.sargis.kh.apixu.databinding.LayoutRecyclerViewItemFavoriteBinding;
import com.sargis.kh.apixu.models.favorite.CurrentWeatherDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ItemViewHolder> implements ItemTouchHelperAdapter {

    public interface ItemInteractionInterface {
        void onFavoriteItemDeleted(CurrentWeatherDataModel searchDataModel);
        void onFavoriteItemMoved(int fromPosition, int toPosition);
    }

    private ItemInteractionInterface itemInteractionInterface;

    private List<CurrentWeatherDataModel> favoriteDataModels;

    public FavoriteAdapter(ItemInteractionInterface itemInteractionInterface) {
        this.itemInteractionInterface = itemInteractionInterface;
        favoriteDataModels = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutRecyclerViewItemFavoriteBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.layout_recycler_view_item_favorite,
                parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bindData(favoriteDataModels.get(position));
    }

    @Override
    public int getItemCount() {
        return favoriteDataModels.size();
    }

    public void setData(List<CurrentWeatherDataModel> currentWeatherDataModels) {
        if (currentWeatherDataModels == null)
            this.favoriteDataModels.clear();
        else
            this.favoriteDataModels = currentWeatherDataModels;
        notifyDataSetChanged();
    }

    public void addData(CurrentWeatherDataModel currentWeatherDataModel) {
        if (currentWeatherDataModel != null) {
            this.favoriteDataModels.add(0, currentWeatherDataModel);
            notifyDataSetChanged();
        }
    }

    public void updateDataAtPosition(CurrentWeatherDataModel currentWeatherDataModel, int position) {
        if (currentWeatherDataModel != null) {
            this.favoriteDataModels.set(position, currentWeatherDataModel);
            notifyItemChanged(position);
        }
    }

    public List<CurrentWeatherDataModel> getCurrentWeatherDataModels() {
        return favoriteDataModels;
    }

    public void clearData() {
        this.favoriteDataModels.clear();
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private LayoutRecyclerViewItemFavoriteBinding binding;

        public ItemViewHolder(LayoutRecyclerViewItemFavoriteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindData(final CurrentWeatherDataModel currentWeatherDataModel) {
            binding.setName(currentWeatherDataModel.location.name + ", " + currentWeatherDataModel.location.country);
            binding.setCondition(currentWeatherDataModel.current.condition.text);
            binding.setTemperature(currentWeatherDataModel.current.temp_c.toString());
            binding.setTemperatureType("ºC");
            binding.setWind(currentWeatherDataModel.current.wind_kph + " km/h");
            binding.setDirection(currentWeatherDataModel.current.wind_dir);

            Picasso.get().load("https://" +  currentWeatherDataModel.current.condition.icon)
                    .placeholder(R.drawable.partly_cloudy)
                    .into(binding.imageViewTemperature);
//            binding.setOnItemClickListener(v -> searchItemSelectedInterface.onSearchItemClicked(searchDataModel));
        }
    }

    @Override
    public void onItemDismiss(int position) {
        itemInteractionInterface.onFavoriteItemDeleted(favoriteDataModels.get(position));
        favoriteDataModels.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(favoriteDataModels, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(favoriteDataModels, i, i - 1);
            }
        }
        swapItemsOrderIndexes(fromPosition, toPosition);

        itemInteractionInterface.onFavoriteItemMoved(fromPosition, toPosition);

        notifyItemMoved(fromPosition, toPosition);

        return true;
    }

    private void swapItemsOrderIndexes(int fromPosition, int toPosition) {
        Long tempFromPosition = favoriteDataModels.get(fromPosition).orderIndex;
        favoriteDataModels.get(fromPosition).orderIndex = favoriteDataModels.get(toPosition).orderIndex;
        favoriteDataModels.get(toPosition).orderIndex = tempFromPosition;
    }

}