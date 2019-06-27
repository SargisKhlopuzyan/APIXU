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
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import com.sargis.kh.apixu.adapters.FavoriteWeatherAdapter;
import com.sargis.kh.apixu.adapters.SearchAdapter;
import com.sargis.kh.apixu.adapters.SimpleItemTouchHelperCallback;
import com.sargis.kh.apixu.databinding.ActivityMainBinding;
import com.sargis.kh.apixu.enums.SelectedState;
import com.sargis.kh.apixu.enums.StateMode;
import com.sargis.kh.apixu.helpers.EnumHelper;
import com.sargis.kh.apixu.models.favorite.CurrentWeatherDataModel;
import com.sargis.kh.apixu.models.search.SearchDataModel;
import com.sargis.kh.apixu.utils.Constants;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchAdapter.SearchItemSelectedInterface, WeatherContract.View, FavoriteWeatherAdapter.ItemInteractionInterface {

    private ActivityMainBinding binding;

    private SearchAdapter searchAdapter;
    private FavoriteWeatherAdapter favoriteWeatherAdapter;

    private WeatherContract.Presenter favoriteWeatherPresenter;

    private ItemTouchHelper touchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        favoriteWeatherPresenter = new FavoriteWeatherPresenter(this);

        setSupportActionBar(binding.toolbar);

        setupRecyclerViewSearch();
        setupRecyclerViewFavorite();

        if (savedInstanceState == null) {
            setStateMode(StateMode.Normal);
            setSelectedState(SelectedState.Unselected);
            setSelectedItemsCount(SelectedState.Unselected, 0);
        } else {
            setStateMode(EnumHelper.getStateMode(savedInstanceState.getInt(Constants.BundleConstants.STATE_MODE)));
            setSelectedState(EnumHelper.getSelectedState(savedInstanceState.getInt(Constants.BundleConstants.SELECTED_STATE)));
            setSelectedItemsCount(getSelectedState(), savedInstanceState.getInt(Constants.BundleConstants.SELECTED_ITEMS_COUNT));

            if (getStateMode() == StateMode.Search) {
                binding.searchView.setIconified(true);
                binding.searchView.clearFocus();
                binding.searchView.onActionViewCollapsed();
                favoriteWeatherPresenter.getSearchData(binding.searchView.getQuery().toString());
            } else if (getStateMode() == StateMode.Empty) {
                binding.searchView.setIconified(true);
                binding.searchView.clearFocus();
                binding.searchView.onActionViewCollapsed();
            }
        }

        setupData();

        setListeners();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        int options = binding.searchView.getImeOptions();
        binding.searchView.setImeOptions(options|EditorInfo.IME_FLAG_NO_EXTRACT_UI|EditorInfo.IME_FLAG_NO_FULLSCREEN);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        setRecyclerViewAnimationState();

        switch (getStateMode()) {
            case Normal:
                menu.findItem(R.id.edit).setVisible(true);
                menu.findItem(R.id.delete).setVisible(true);
                break;
            case Empty:
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constants.BundleConstants.STATE_MODE, getStateMode().getIndex());
        outState.putInt(Constants.BundleConstants.SELECTED_STATE, getSelectedState().getIndex());
        outState.putInt(Constants.BundleConstants.SELECTED_ITEMS_COUNT, favoriteWeatherPresenter.getSelectedItemsCount());
        super.onSaveInstanceState(outState);
    }

    private StateMode getStateMode() {
        return binding.getStateMode();
    }

    private void setStateMode(StateMode stateMode) {
        binding.setStateMode(stateMode);
        favoriteWeatherAdapter.setStateMode(stateMode);
    }

    private SelectedState getSelectedState() {
        return binding.getSelectedState();
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
    }

    private void setRecyclerViewAnimationState() {
        StateMode stateMode = getStateMode();
        if (stateMode == null) {
            setStateMode(StateMode.Normal);
            stateMode = StateMode.Normal;
        }

        switch (stateMode) {
            case Edit:
                touchHelper.attachToRecyclerView(binding.recyclerViewFavorite);
                break;
            case Normal:
            case Empty:
            case Search:
            case Delete:
                touchHelper.attachToRecyclerView(null);
                break;
        }
    }

    private void setupData(){
        favoriteWeatherPresenter.getFavoriteSavedDataFromDatabase();
    }

    private void setListeners() {
        binding.setOnRefreshListener(() -> favoriteWeatherPresenter.updateFavoritesData(favoriteWeatherAdapter.getCurrentWeatherDataModels()));

        binding.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                binding.searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                hideErrorMessage();
                favoriteWeatherPresenter.getSearchData(newText);
                return false;
            }
        });

        binding.setOnQueryTextFocusChangeListener((v, hasFocus) -> {

            invalidateOptionsMenu();

            if (hasFocus) {
                setStateMode(String.valueOf(((SearchView)v).getQuery()).isEmpty() ? StateMode.Empty : StateMode.Search);
            } else if (String.valueOf(((SearchView)v).getQuery()).isEmpty()){
                setStateMode(favoriteWeatherAdapter.getCurrentWeatherDataModels().isEmpty() ? StateMode.Empty : StateMode.Normal);
                binding.searchView.setIconified(true);
            } else {
                setStateMode(StateMode.Search);
            }
        });

        binding.setOnCloseEditModeClickListener(v -> {

            if (getStateMode() == StateMode.Delete) {
                favoriteWeatherPresenter.resetSelectedItems(favoriteWeatherAdapter.getCurrentWeatherDataModels());
            }

            setStateMode(favoriteWeatherAdapter.getCurrentWeatherDataModels().isEmpty() ? StateMode.Empty : StateMode.Normal);
            favoriteWeatherAdapter.setStateMode(StateMode.Normal);
            invalidateOptionsMenu();
        });

        binding.setOnSelectAllClickListener(v -> {
            setSelectedState(getSelectedState() == SelectedState.AllSelected ? SelectedState.Unselected : SelectedState.AllSelected);
            favoriteWeatherPresenter.setAllItemsStateSelected(favoriteWeatherAdapter.getCurrentWeatherDataModels(), getSelectedState());
        });

        binding.setOnDeleteClickListener(v -> favoriteWeatherPresenter.deleteAllSelectedFavoriteDatesFromDatabase(favoriteWeatherAdapter.getCurrentWeatherDataModels()));
    }

    @Override
    public void onSearchItemClicked(SearchDataModel searchDataModel) {
        StateMode stateMode = favoriteWeatherAdapter.getCurrentWeatherDataModels().isEmpty() ? StateMode.Empty : StateMode.Normal;
        if (getStateMode() != stateMode)
            setStateMode(stateMode);
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
    }

    @Override
    public void onFavoriteDataLoadingStarted() {
        stopSearchLoadingAndCleanData();
        binding.searchView.setQuery("", false);
        binding.searchView.clearFocus();
        binding.setIsFavoriteLoading(true);
    }

    @Override
    public void onFavoriteDataLoaded(CurrentWeatherDataModel currentWeatherDataModel) {

        if (getStateMode() == StateMode.Empty) {
            setStateMode(StateMode.Normal);
            invalidateOptionsMenu();
        }

        binding.setIsFavoriteLoading(false);
        favoriteWeatherAdapter.addData(currentWeatherDataModel);
    }

    @Override
    public void onFavoriteDataLoaded() {
        binding.setIsFavoriteLoading(false);
        hideErrorMessage();
    }

    @Override
    public void onFavoriteDataLoadedWithError(String errorMessage) {
        binding.setIsFavoriteLoading(false);
        showErrorMessage(errorMessage);
    }

    @Override
    public void onSearchDataLoadingStarted() {
        hideErrorMessage();
        binding.setIsSearchLoading(true);
        searchAdapter.clearData();
    }

    @Override
    public void onSearchDataLoaded(List<SearchDataModel> searchDataModels) {
        hideErrorMessage();
        binding.setIsSearchLoading(false);
        searchAdapter.updateData(searchDataModels);
    }

    @Override
    public void onSearchDataLoadedWithError(String errorMessage) {
        showErrorMessage(errorMessage);
        binding.setIsSearchLoading(false);
    }

    @Override
    public void setIsSearchEmpty(boolean isSearchEmpty) {
        if (getStateMode() == StateMode.Empty || getStateMode() == StateMode.Search)
            setStateMode(isSearchEmpty ? StateMode.Empty : StateMode.Search);
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
    public void onFavoriteItemDeleted(int position) {
        favoriteWeatherAdapter.onItemRemoved(position);

        if (favoriteWeatherAdapter.getCurrentWeatherDataModels().isEmpty() && (getStateMode() == StateMode.Normal || getStateMode() == StateMode.Edit || getStateMode() == StateMode.Delete)) {
            setStateMode(StateMode.Empty);
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onFavoriteItemRemoved(List<CurrentWeatherDataModel> currentWeatherDataModels, CurrentWeatherDataModel deletedCurrentWeatherDataModel, int position) {
        favoriteWeatherPresenter.deleteFavoriteDataFromDatabase(currentWeatherDataModels, deletedCurrentWeatherDataModel, position);
    }

    @Override
    public void onFavoriteItemMoved(int fromPosition, int toPosition) {
        favoriteWeatherPresenter.onFavoriteItemMoved(favoriteWeatherAdapter.getCurrentWeatherDataModels(), fromPosition, toPosition);
    }

    @Override
    public void onFavoriteItemSelectedStateChanged(CurrentWeatherDataModel currentWeatherDataModel, int itemsSize, int position, Boolean isSelected) {
        favoriteWeatherPresenter.itemSelectedStateChanged(itemsSize, isSelected);
    }

    private void showErrorMessage(String errorMessage) {
        binding.setIsErrorVisible(true);
        binding.setErrorMessage(errorMessage);
    }

    private void hideErrorMessage() {
        binding.setIsErrorVisible(false);
        binding.setErrorMessage("");
    }

    @Override
    public void onBackPressed() {
        if (!binding.searchView.isIconified()) {
            binding.searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

}
