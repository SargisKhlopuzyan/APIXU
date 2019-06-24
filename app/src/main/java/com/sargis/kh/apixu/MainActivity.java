package com.sargis.kh.apixu;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.SearchView;

import com.sargis.kh.apixu.adapters.FavoriteAdapter;
import com.sargis.kh.apixu.adapters.SearchAdapter;
import com.sargis.kh.apixu.adapters.SimpleItemTouchHelperCallback;
import com.sargis.kh.apixu.databinding.ActivityMainBinding;
import com.sargis.kh.apixu.models.favorite.CurrentWeatherDataModel;
import com.sargis.kh.apixu.models.search.SearchDataModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchAdapter.SearchItemSelectedInterface, WeatherContract.View, FavoriteAdapter.ItemInteractionInterface {

    private ActivityMainBinding binding;

    private SearchAdapter searchAdapter;
    private FavoriteAdapter favoriteAdapter;

    private WeatherPresenter weatherPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        weatherPresenter = new WeatherPresenter(this);

        setupRecyclerViewSearch();
        setupRecyclerViewFavorite();
        setupData();

        setListeners();
    }

    private void setupRecyclerViewSearch() {
        searchAdapter = new SearchAdapter(this);
        binding.recyclerViewSearch.setHasFixedSize(false);
        binding.recyclerViewSearch.setAdapter(searchAdapter);
    }

    private void setupRecyclerViewFavorite() {
        favoriteAdapter = new FavoriteAdapter(this);
        binding.recyclerViewFavorite.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerViewFavorite.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.recyclerViewFavorite.getContext(), layoutManager.getOrientation());
        binding.recyclerViewFavorite.addItemDecoration(dividerItemDecoration);
        binding.recyclerViewFavorite.setAdapter(favoriteAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(favoriteAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.recyclerViewFavorite);
    }

    private void setupData(){
        weatherPresenter.getFavoriteSavedDataFromDatabase();
    }

    private void setListeners() {
        binding.setOnRefreshListener(() -> weatherPresenter.updateFavoritesData(favoriteAdapter.getCurrentWeatherDataModels()));

        binding.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                weatherPresenter.getSearchData(newText);
                return false;
            }
        });

        binding.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.setIsSearchMode(true);
                binding.setIsSearchEmpty(String.valueOf(((SearchView)v).getQuery()).isEmpty());
            } else if (String.valueOf(((SearchView)v).getQuery()).isEmpty()){
                binding.searchView.setIconified(true);
                binding.setIsSearchMode(false);
            }
        });
    }

    @Override
    public void onSearchItemClicked(SearchDataModel searchDataModel) {
        weatherPresenter.getFavoriteData(searchDataModel.name, Long.valueOf(favoriteAdapter.getItemCount()));
    }

    @Override
    public void onFavoriteSavedDataLoadingFromDatabaseStarted() {
        binding.setIsFavoriteLoading(true);
    }

    @Override
    public void onFavoriteSavedDataLoadedFromDatabase(List<CurrentWeatherDataModel> currentWeatherDataModels) {
        Log.e("LOG_TAG", "onFavoriteSavedDataLoadedFromDatabase");
        favoriteAdapter.setData(currentWeatherDataModels);
        binding.setIsFavoriteLoading(false);
    }

    @Override
    public void onFavoriteSavedDataUpdatingStarted() {
        if (!binding.swipeRefreshLayout.isRefreshing()) {
            binding.swipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public void onFavoriteSavedDataUpdated(CurrentWeatherDataModel currentWeatherDataModel) {

        //TODO -
        Log.e("LOG_TAG", "onFavoriteSavedDataUpdated");
    }

    @Override
    public void onFavoriteSavedDataUpdatingFinished() {
        Log.e("LOG_TAG", "onFavoriteSavedDataUpdatingFinished");
        if (binding.swipeRefreshLayout.isRefreshing()) {
            binding.swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onFavoriteSavedDataUpdatingFinishedWithError(String errorMessage) {
        if (binding.swipeRefreshLayout.isRefreshing()) {
            binding.swipeRefreshLayout.setRefreshing(false);
        }
        Log.e("LOG_TAG", "onFavoriteSavedDataUpdatingFinishedWithError");
        //TODO - ERROR
    }

    @Override
    public void onFavoriteDataLoadingStarted() {
        searchAdapter.clearData();
        binding.searchView.setQuery("", false);
        binding.searchView.clearFocus();
        binding.setIsFavoriteLoading(true);
        binding.setIsSearchMode(false);
    }

    @Override
    public void onFavoriteDataLoaded(CurrentWeatherDataModel currentWeatherDataModel) {
        binding.setIsFavoriteLoading(false);
        favoriteAdapter.addData(currentWeatherDataModel);
    }

    @Override
    public void onFavoriteDataLoadedWithError(String errorMessage) {
        binding.setIsFavoriteLoading(false);
    }

    @Override
    public void onSearchDataLoadingStarted() {
        binding.setIsSearchLoading(true);
        searchAdapter.clearData();
    }

    @Override
    public void onSearchDataLoaded(ArrayList<SearchDataModel> searchDataModels) {
        binding.setIsSearchLoading(false);
        searchAdapter.updateData(searchDataModels);
    }

    @Override
    public void onSearchDataLoadedWithError(String errorMessage) {
        binding.setIsSearchLoading(false);
    }

    @Override
    public void setIsSearchMode(boolean isSearchMode) {
        binding.setIsSearchMode(isSearchMode);
    }

    @Override
    public void setIsSearchEmpty(boolean isSearchEmpty) {
        binding.setIsSearchEmpty(isSearchEmpty);
    }

    @Override
    public void stopSearchLoadingAndCleanData() {
        binding.setIsSearchLoading(false);
        searchAdapter.clearData();
    }

    @Override
    public void onFavoriteItemDeleted(CurrentWeatherDataModel currentWeatherDataModel) {
        weatherPresenter.deleteFavoriteDataFromDatabase(currentWeatherDataModel.location.name);
    }

}
