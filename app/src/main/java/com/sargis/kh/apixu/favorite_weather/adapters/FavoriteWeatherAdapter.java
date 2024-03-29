package com.sargis.kh.apixu.favorite_weather.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sargis.kh.apixu.R;
import com.sargis.kh.apixu.databinding.LayoutRecyclerViewItemFavoriteBinding;
import com.sargis.kh.apixu.favorite_weather.enums.StateMode;
import com.sargis.kh.apixu.favorite_weather.models.favorite.CurrentWeatherDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FavoriteWeatherAdapter extends RecyclerView.Adapter<FavoriteWeatherAdapter.ItemViewHolder> implements ItemTouchHelperAdapter {

    public interface ItemInteractionInterface {
        void onFavoriteWeatherItemDismiss(List<CurrentWeatherDataModel> currentWeatherDataModels, CurrentWeatherDataModel deletedDataModel, int position);
        void onFavoriteWeatherItemMoved(int fromPosition, int toPosition);
        void onFavoriteItemSelectedStateChanged(CurrentWeatherDataModel currentWeatherDataModel, int itemsSize, int position, Boolean isSelected);
    }

    private ItemInteractionInterface itemInteractionInterface;

    private List<CurrentWeatherDataModel> favoriteDataModels;

    private StateMode stateMode = StateMode.Normal;

    public FavoriteWeatherAdapter(ItemInteractionInterface itemInteractionInterface) {
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
        holder.bindData(favoriteDataModels.get(position), stateMode, itemInteractionInterface);
    }

    @Override
    public int getItemCount() {
        return favoriteDataModels.size();
    }

    public void setStateMode(StateMode stateMode) {
        if ((stateMode != this.stateMode) && (stateMode == StateMode.Delete || this.stateMode == StateMode.Delete)) {
            this.stateMode = stateMode;
            notifyDataSetChanged();
        } else {
            this.stateMode = stateMode;
        }
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

    public void onItemRemoved(int position) {
        favoriteDataModels.remove(position);
        notifyItemRemoved(position);
    }

    public void updateDataAtPosition(CurrentWeatherDataModel currentWeatherDataModel, int position) {
        if (currentWeatherDataModel != null) {
            this.favoriteDataModels.set(position, currentWeatherDataModel);
            notifyItemChanged(position);
        }
    }

    public List<CurrentWeatherDataModel> getFavoriteWeatherDataModels() {
        return favoriteDataModels;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private LayoutRecyclerViewItemFavoriteBinding binding;

        public ItemViewHolder(LayoutRecyclerViewItemFavoriteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindData(final CurrentWeatherDataModel currentWeatherDataModel, StateMode stateMode, ItemInteractionInterface itemInteractionInterface) {
            binding.setName(currentWeatherDataModel.location.name + ", " + currentWeatherDataModel.location.country);
            binding.setCondition(currentWeatherDataModel.current.condition.text);
            binding.setTemperature(currentWeatherDataModel.current.temp_c.toString());
            binding.setTemperatureType("ºC");
            binding.setWind(currentWeatherDataModel.current.wind_kph + " km/h");
            binding.setDirection(currentWeatherDataModel.current.wind_dir);
            binding.setStateMode(stateMode);

            if (stateMode == StateMode.Delete) {
                binding.checkBox.setOnCheckedChangeListener(null);
                binding.checkBox.setChecked(currentWeatherDataModel.isSelected);
                binding.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    currentWeatherDataModel.isSelected = isChecked;
                    itemInteractionInterface.onFavoriteItemSelectedStateChanged(currentWeatherDataModel, getItemCount(), getAdapterPosition(), isChecked);
                });
            }

            Picasso.get().load("https://" +  currentWeatherDataModel.current.condition.icon)
                    .placeholder(R.drawable.partly_cloudy)
                    .into(binding.imageViewTemperature);
        }
    }

    @Override
    public void onItemDismiss(int position) {
        itemInteractionInterface.onFavoriteWeatherItemDismiss(favoriteDataModels, favoriteDataModels.get(position), position);
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

        itemInteractionInterface.onFavoriteWeatherItemMoved(fromPosition, toPosition);

        notifyItemMoved(fromPosition, toPosition);

        return true;
    }

    private void swapItemsOrderIndexes(int fromPosition, int toPosition) {
        Integer tempFromPosition = favoriteDataModels.get(fromPosition).orderIndex;
        favoriteDataModels.get(fromPosition).orderIndex = favoriteDataModels.get(toPosition).orderIndex;
        favoriteDataModels.get(toPosition).orderIndex = tempFromPosition;
    }

}