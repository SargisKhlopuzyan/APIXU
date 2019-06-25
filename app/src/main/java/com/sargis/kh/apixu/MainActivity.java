package com.sargis.kh.apixu;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.sargis.kh.apixu.adapters.FavoriteWeatherAdapter;
import com.sargis.kh.apixu.adapters.SearchAdapter;
import com.sargis.kh.apixu.adapters.SimpleItemTouchHelperCallback;
import com.sargis.kh.apixu.databinding.ActivityMainBinding;
import com.sargis.kh.apixu.enums.SelectedState;
import com.sargis.kh.apixu.enums.StateMode;
import com.sargis.kh.apixu.models.favorite.CurrentWeatherDataModel;
import com.sargis.kh.apixu.models.search.SearchDataModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchAdapter.SearchItemSelectedInterface, WeatherContract.View, FavoriteWeatherAdapter.ItemInteractionInterface {

    private ActivityMainBinding binding;

    private SearchAdapter searchAdapter;
    private FavoriteWeatherAdapter favoriteWeatherAdapter;

    private FavoriteWeatherPresenter favoriteWeatherPresenter;

    private ItemTouchHelper touchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        favoriteWeatherPresenter = new FavoriteWeatherPresenter(this);

        setupRecyclerViewSearch();
        setupRecyclerViewFavorite();
        setupData();
        setSupportActionBar(binding.toolbar);
        setListeners();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        setRecyclerViewAnimationState();

        switch (binding.getStateMode()) {
            case Normal:
                menu.findItem(R.id.edit).setVisible(true);
                menu.findItem(R.id.delete).setVisible(true);
                break;
            case Search:
            case Edit:
            case Delete:
                menu.findItem(R.id.edit).setVisible(false);
                menu.findItem(R.id.delete).setVisible(false);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                setStateMode(StateMode.Edit);
                favoriteWeatherAdapter.setStateMode(StateMode.Edit);
                invalidateOptionsMenu();
                break;
            case R.id.delete:
                setStateMode(StateMode.Delete);
                favoriteWeatherAdapter.setStateMode(StateMode.Delete);
                invalidateOptionsMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setStateMode(StateMode stateMode) {
        binding.setStateMode(stateMode);
    }

    private void setSelectedState(SelectedState selectedState) {
        binding.setSelectedState(selectedState);
    }

    private void setupRecyclerViewSearch() {
        searchAdapter = new SearchAdapter(this);
        binding.recyclerViewSearch.setHasFixedSize(false);
        binding.recyclerViewSearch.setAdapter(searchAdapter);
    }

    private void setupRecyclerViewFavorite() {
        favoriteWeatherAdapter = new FavoriteWeatherAdapter(this);
        binding.recyclerViewFavorite.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerViewFavorite.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.recyclerViewFavorite.getContext(), layoutManager.getOrientation());
        binding.recyclerViewFavorite.addItemDecoration(dividerItemDecoration);
        binding.recyclerViewFavorite.setAdapter(favoriteWeatherAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(favoriteWeatherAdapter);
        touchHelper = new ItemTouchHelper(callback);
        setRecyclerViewAnimationState();
    }

    private void setRecyclerViewAnimationState() {
        StateMode stateMode = binding.getStateMode();
        if (stateMode == null) {
            setStateMode(StateMode.Normal);
            stateMode = StateMode.Normal;
        }

        switch (stateMode) {
            case Edit:
                touchHelper.attachToRecyclerView(binding.recyclerViewFavorite);
                break;
            case Normal:
            case Search:
            case Delete:
                touchHelper.attachToRecyclerView(null);
                break;
        }
    }

    private void setupData(){
        setStateMode(StateMode.Normal);
        setSelectedState(SelectedState.Unselected);
        binding.setSelectedItemsCount(0);
        favoriteWeatherPresenter.getFavoriteSavedDataFromDatabase();
    }

    private void setListeners() {
        binding.setOnRefreshListener(() -> favoriteWeatherPresenter.updateFavoritesData(favoriteWeatherAdapter.getCurrentWeatherDataModels()));

        binding.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                favoriteWeatherPresenter.getSearchData(newText);
                return false;
            }
        });

        binding.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            setStateMode(hasFocus ? StateMode.Search : StateMode.Normal);
            invalidateOptionsMenu();

            if (hasFocus) {
                binding.setIsSearchMode(true);
                binding.setIsSearchEmpty(String.valueOf(((SearchView)v).getQuery()).isEmpty());
            } else if (String.valueOf(((SearchView)v).getQuery()).isEmpty()){
                binding.searchView.setIconified(true);
                binding.setIsSearchMode(false);
            }
        });

        binding.setOnCloseEditModeClickListener(v -> {
            //TODO
//            setSelectedState(SelectedState.Unselected);

            if (binding.getStateMode() == StateMode.Delete) {
                favoriteWeatherPresenter.resetSelectedItems(favoriteWeatherAdapter.getCurrentWeatherDataModels());
            }

            setStateMode(StateMode.Normal);
            favoriteWeatherAdapter.setStateMode(StateMode.Normal);
            invalidateOptionsMenu();
        });

        binding.setOnSelectAllClickListener(v -> {
            setSelectedState(binding.getSelectedState() == SelectedState.AllSelected ? SelectedState.Unselected : SelectedState.AllSelected);
            favoriteWeatherPresenter.setAllItemsSelectedState(favoriteWeatherAdapter.getCurrentWeatherDataModels(), binding.getSelectedState());
        });

        binding.setOnDeleteClickListener(v -> {
            //TODO
        });
    }

    @Override
    public void onSearchItemClicked(SearchDataModel searchDataModel) {
        favoriteWeatherPresenter.getFavoriteData(searchDataModel, Long.valueOf(favoriteWeatherAdapter.getItemCount()));
    }

    @Override
    public void onFavoriteSavedDataLoadingFromDatabaseStarted() {
        binding.setIsFavoriteLoading(true);
    }

    @Override
    public void onFavoriteSavedDataLoadedFromDatabase(List<CurrentWeatherDataModel> currentWeatherDataModels) {
        favoriteWeatherAdapter.setData(currentWeatherDataModels);
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
        favoriteWeatherAdapter.updateDataAtPosition(currentWeatherDataModel, favoriteWeatherAdapter.getItemCount() - currentWeatherDataModel.orderIndex.intValue() - 1);
        //TODO
    }

    @Override
    public void onFavoriteSavedDataUpdatingFinished() {
        if (binding.swipeRefreshLayout.isRefreshing()) {
            binding.swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onFavoriteSavedDataUpdatingFinishedWithError(String errorMessage) {
        if (binding.swipeRefreshLayout.isRefreshing()) {
            binding.swipeRefreshLayout.setRefreshing(false);
        }
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
        favoriteWeatherAdapter.addData(currentWeatherDataModel);
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
    public void setIsSearchEmpty(boolean isSearchEmpty) {
        binding.setIsSearchEmpty(isSearchEmpty);
    }

    @Override
    public void stopSearchLoadingAndCleanData() {
        binding.setIsSearchLoading(false);
        searchAdapter.clearData();
    }

    @Override
    public void setSelectedItemsCount(SelectedState selectedState, int selectedItemsCount) {
        setSelectedState(selectedState);
        binding.setSelectedItemsCount(selectedItemsCount);
    }

    @Override
    public void updateView() {
        favoriteWeatherAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFavoriteItemDeleted(CurrentWeatherDataModel currentWeatherDataModel) {
        //TODO
        favoriteWeatherPresenter.deleteFavoriteDataFromDatabase(currentWeatherDataModel.id);
    }

    @Override
    public void onFavoriteItemMoved(int fromPosition, int toPosition) {
        favoriteWeatherPresenter.onFavoriteItemMoved(favoriteWeatherAdapter.getCurrentWeatherDataModels(), fromPosition, toPosition);
    }

    @Override
    public void onFavoriteItemSelectedStateChanged(CurrentWeatherDataModel currentWeatherDataModel, int itemsSize, int position, Boolean isSelected) {
        favoriteWeatherPresenter.itemSelectedStateChanged(currentWeatherDataModel, itemsSize, position, isSelected);
    }

}
