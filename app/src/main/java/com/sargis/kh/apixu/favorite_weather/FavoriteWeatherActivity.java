package com.sargis.kh.apixu.favorite_weather;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import com.sargis.kh.apixu.R;
import com.sargis.kh.apixu.databinding.ActivityFavoriteWeatherBinding;
import com.sargis.kh.apixu.di.component.DaggerDeleteModeComponent;
import com.sargis.kh.apixu.di.component.DaggerEditModeComponent;
import com.sargis.kh.apixu.di.component.DaggerFavoriteWeatherComponent;
import com.sargis.kh.apixu.di.component.DaggerFavoriteWeatherDatabaseComponent;
import com.sargis.kh.apixu.di.component.DaggerSearchComponent;
import com.sargis.kh.apixu.di.component.DeleteModeComponent;
import com.sargis.kh.apixu.di.module.DeleteModePresenterModule;
import com.sargis.kh.apixu.di.module.EditModePresenterModule;
import com.sargis.kh.apixu.di.module.FavoriteWeatherDatabasePresenterModule;
import com.sargis.kh.apixu.di.module.FavoriteWeatherPresenterModule;
import com.sargis.kh.apixu.di.module.SearchPresenterModule;
import com.sargis.kh.apixu.favorite_weather.adapters.FavoriteWeatherAdapter;
import com.sargis.kh.apixu.favorite_weather.adapters.SearchAdapter;
import com.sargis.kh.apixu.favorite_weather.adapters.SimpleItemTouchHelperCallback;
import com.sargis.kh.apixu.favorite_weather.contracts.DeleteModeContract;
import com.sargis.kh.apixu.favorite_weather.contracts.EditModeContract;
import com.sargis.kh.apixu.favorite_weather.contracts.FavoriteWeatherContract;
import com.sargis.kh.apixu.favorite_weather.contracts.FavoriteWeatherDatabaseContract;
import com.sargis.kh.apixu.favorite_weather.contracts.SearchContract;
import com.sargis.kh.apixu.favorite_weather.enums.DeleteModeSelectedState;
import com.sargis.kh.apixu.favorite_weather.enums.SearchStateMode;
import com.sargis.kh.apixu.favorite_weather.enums.StateMode;
import com.sargis.kh.apixu.favorite_weather.helpers.EnumHelper;
import com.sargis.kh.apixu.favorite_weather.models.favorite.CurrentWeatherDataModel;
import com.sargis.kh.apixu.favorite_weather.models.search.SearchDataModel;
import com.sargis.kh.apixu.favorite_weather.presenters.DeleteModePresenter;
import com.sargis.kh.apixu.favorite_weather.presenters.EditModePresenter;
import com.sargis.kh.apixu.favorite_weather.presenters.FavoriteWeatherDatabasePresenter;
import com.sargis.kh.apixu.favorite_weather.presenters.FavoriteWeatherPresenter;
import com.sargis.kh.apixu.favorite_weather.presenters.SearchPresenter;
import com.sargis.kh.apixu.favorite_weather.utils.Constants;

import java.util.List;

import javax.inject.Inject;

public class FavoriteWeatherActivity extends AppCompatActivity implements SearchAdapter.SearchItemSelectedInterface,
        FavoriteWeatherContract.View,
        FavoriteWeatherDatabaseContract.View,
        SearchContract.View,
        EditModeContract.View,
        DeleteModeContract.View,
        FavoriteWeatherAdapter.ItemInteractionInterface {


    @Inject
    protected FavoriteWeatherPresenter favoriteWeatherPresenter;

    @Inject
    protected FavoriteWeatherDatabasePresenter favoriteWeatherDatabasePresenter;

    @Inject
    protected SearchPresenter searchPresenter;

    @Inject
    protected EditModePresenter editModePresenter;

    @Inject
    protected DeleteModePresenter deleteModePresenter;



    private ActivityFavoriteWeatherBinding binding;

    private SearchAdapter searchAdapter;
    private FavoriteWeatherAdapter favoriteWeatherAdapter;

    private ItemTouchHelper touchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_favorite_weather);

        setSupportActionBar(binding.toolbar);

        // Creates presenter
        DeleteModeComponent deleteModeComponent = DaggerDeleteModeComponent.builder()
                .deleteModePresenterModule(new DeleteModePresenterModule(this))
                .build();

        deleteModeComponent.inject(this);

        Log.e("LOG_TAG", "deleteModeComponent: " + deleteModeComponent);

        DaggerEditModeComponent.builder()
                .editModePresenterModule(new EditModePresenterModule(this))
                .build()
                .inject(this);

        DaggerFavoriteWeatherDatabaseComponent.builder()
                .favoriteWeatherDatabasePresenterModule(new FavoriteWeatherDatabasePresenterModule(this))
                .build()
                .inject(this);

        DaggerFavoriteWeatherComponent.builder()
                .favoriteWeatherPresenterModule(new FavoriteWeatherPresenterModule(this))
                .build()
                .inject(this);

        DaggerSearchComponent.builder()
                .searchPresenterModule(new SearchPresenterModule(this))
                .build()
                .inject(this);

        Log.e("LOG_TAG", "favoriteWeatherPresenter: " + favoriteWeatherPresenter);
        Log.e("LOG_TAG", "favoriteWeatherDatabasePresenter: " + favoriteWeatherDatabasePresenter);
        Log.e("LOG_TAG", "searchPresenter: " + searchPresenter);
        Log.e("LOG_TAG", "editModePresenter: " + editModePresenter);
        Log.e("LOG_TAG", "deleteModePresenter: " + deleteModePresenter);

//        favoriteWeatherPresenter = new FavoriteWeatherPresenter(this);
//        favoriteWeatherDatabasePresenter = new FavoriteWeatherDatabasePresenter(this);
//        searchPresenter = new SearchPresenter(this);
//        editModePresenter = new EditModePresenter(this);
//        deleteModePresenter = new DeleteModePresenter(this);

        setupRecyclerViewSearch();
        setupRecyclerViewFavoriteWeather();

        if (savedInstanceState == null) {
            setStateMode(StateMode.Normal);
            setSearchStateMode(SearchStateMode.Non);
            setDeleteModeSelectedState(DeleteModeSelectedState.Unselected);
            setSelectedItemsCount(DeleteModeSelectedState.Unselected, 0);
            setupSavedFavoriteWeatherDataFromDatabase();
        } else {
            List<CurrentWeatherDataModel> savedCurrentWeatherDataModels = favoriteWeatherDatabasePresenter.getSavedFavoriteWeatherDataFromDatabase();

            setStateMode(EnumHelper.getStateMode(savedInstanceState.getInt(Constants.BundleConstants.STATE_MODE)));
            setDeleteModeSelectedState(EnumHelper.getSelectedState(savedInstanceState.getInt(Constants.BundleConstants.DELETE_MODE_SELECTED_STATE)));
            setSearchStateMode(EnumHelper.getSearchStateMode(savedInstanceState.getInt(Constants.BundleConstants.SEARCH_STATE_MODE)));

            setSelectedItemsCount(getDeleteModeSelectedState(), savedInstanceState.getInt(Constants.BundleConstants.SELECTED_ITEMS_ORDER_INDEXES));

            binding.setIsErrorVisible(savedInstanceState.getBoolean(Constants.BundleConstants.IS_ERROR_VISIBLE));
            binding.setErrorMessage(savedInstanceState.getString(Constants.BundleConstants.ERROR_MESSAGE));

            binding.setIsFavoriteWeatherLoading(savedInstanceState.getBoolean(Constants.BundleConstants.IS_FAVORITE_WEATHER_LOADING));

            binding.searchView.setQuery(savedInstanceState.getString(Constants.BundleConstants.QUERY_SEARCH), false);

            switch (getStateMode()) {
                case Normal:
                    setupSavedFavoriteWeatherDataFromDatabase();
                    if (savedInstanceState.getBoolean(Constants.BundleConstants.IS_FAVORITE_WEATHER_LOADING)) {
                        searchSearchDataModelAndUpdateData((SearchDataModel) savedInstanceState.getSerializable(Constants.BundleConstants.SEARCH_DATA_MODEL));
                    }

                    if (binding.getSearchStateMode() == SearchStateMode.Loading ||
                            binding.getSearchStateMode() == SearchStateMode.Normal) {
                        searchPresenter.getSearchData(binding.searchView.getQuery().toString());
                    }
                    break;
                case Edit:
                    setupSavedFavoriteWeatherDataFromDatabase();
                    break;
                case Delete:
                    deleteModePresenter.setSelectedItemsOrderIndexes(savedCurrentWeatherDataModels, savedInstanceState.getIntegerArrayList(Constants.BundleConstants.SELECTED_ITEMS_ORDER_INDEXES));
                    favoriteWeatherAdapter.setData(savedCurrentWeatherDataModels);

                    setSelectedItemsCount(binding.getDeleteModeSelectedState(), deleteModePresenter.getSelectedItemsOrderIndexes().size());

                    break;
            }
        }

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
                if (getSearchStateMode() == SearchStateMode.Non) {
                    menu.findItem(R.id.edit).setVisible(true);
                    menu.findItem(R.id.delete).setVisible(true);
                } else {
                    menu.findItem(R.id.edit).setVisible(false);
                    menu.findItem(R.id.delete).setVisible(false);
                }
                break;
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
        outState.putInt(Constants.BundleConstants.DELETE_MODE_SELECTED_STATE, getDeleteModeSelectedState().getIndex());
        outState.putInt(Constants.BundleConstants.SEARCH_STATE_MODE, getSearchStateMode().getIndex());

        outState.putBoolean(Constants.BundleConstants.IS_ERROR_VISIBLE, binding.getIsErrorVisible());
        outState.putString(Constants.BundleConstants.ERROR_MESSAGE, binding.getErrorMessage());

        outState.putBoolean(Constants.BundleConstants.IS_FAVORITE_WEATHER_LOADING, binding.getIsFavoriteWeatherLoading());

        outState.putString(Constants.BundleConstants.QUERY_SEARCH, binding.searchView.getQuery().toString());
        outState.putSerializable(Constants.BundleConstants.SEARCH_DATA_MODEL, favoriteWeatherPresenter.getSearchDataModel());

        outState.putIntegerArrayList(Constants.BundleConstants.SELECTED_ITEMS_ORDER_INDEXES, deleteModePresenter.getSelectedItemsOrderIndexes());

        super.onSaveInstanceState(outState);
    }

    private StateMode getStateMode() {
        return binding.getStateMode();
    }

    private void setStateMode(StateMode stateMode) {
        binding.setStateMode(stateMode);
        favoriteWeatherAdapter.setStateMode(stateMode);
    }


    private SearchStateMode getSearchStateMode() {
        return binding.getSearchStateMode();
    }

    private void setSearchStateMode(SearchStateMode searchStateMode) {
        binding.setSearchStateMode(searchStateMode);
        searchAdapter.setSearchStateMode(searchStateMode);
    }

    private DeleteModeSelectedState getDeleteModeSelectedState() {
        return binding.getDeleteModeSelectedState();
    }

    private void setDeleteModeSelectedState(DeleteModeSelectedState deleteModeSelectedState) {
        binding.setDeleteModeSelectedState(deleteModeSelectedState);
    }

    private void setupRecyclerViewSearch() {
        searchAdapter = new SearchAdapter(this);
        binding.recyclerViewSearch.setHasFixedSize(false);
        binding.recyclerViewSearch.setAdapter(searchAdapter);
    }

    private void setupRecyclerViewFavoriteWeather() {
        favoriteWeatherAdapter = new FavoriteWeatherAdapter(this);
        binding.recyclerViewFavoriteWeather.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerViewFavoriteWeather.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.recyclerViewFavoriteWeather.getContext(), layoutManager.getOrientation());
        binding.recyclerViewFavoriteWeather.addItemDecoration(dividerItemDecoration);
        binding.recyclerViewFavoriteWeather.setAdapter(favoriteWeatherAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(favoriteWeatherAdapter);
        touchHelper = new ItemTouchHelper(callback);
    }

    private void setRecyclerViewAnimationState() {
        if (getStateMode() == null) {
            setStateMode(StateMode.Normal);
        }

        switch (getStateMode()) {
            case Edit:
                touchHelper.attachToRecyclerView(binding.recyclerViewFavoriteWeather);
                break;
            case Normal:
            case Delete:
                touchHelper.attachToRecyclerView(null);
                break;
        }
    }

    private void setupSavedFavoriteWeatherDataFromDatabase(){
        favoriteWeatherAdapter.setData(favoriteWeatherDatabasePresenter.getSavedFavoriteWeatherDataFromDatabase());
    }

    private void setListeners() {

        binding.setOnRefreshListener(() -> favoriteWeatherDatabasePresenter.updateFavoriteWeatherDataList(favoriteWeatherAdapter.getFavoriteWeatherDataModels()));

        binding.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                binding.searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                hideErrorMessage();
                searchPresenter.getSearchData(newText);
                return false;
            }
        });

        binding.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            invalidateOptionsMenu();

            if (hasFocus) {
                setSearchStateMode(String.valueOf(((SearchView)v).getQuery()).isEmpty() ? SearchStateMode.Empty : SearchStateMode.Normal);
            } else if (String.valueOf(((SearchView)v).getQuery()).isEmpty()){
                setSearchStateMode(SearchStateMode.Non);
                binding.searchView.setIconified(true);
            } else {
                setSearchStateMode(SearchStateMode.Normal);
            }
        });

        binding.setOnCloseEditModeClickListener(v -> {

            if (getStateMode() == StateMode.Delete) {
                deleteModePresenter.resetSelectedItems(favoriteWeatherAdapter.getFavoriteWeatherDataModels());
            }

            setStateMode(StateMode.Normal);
            favoriteWeatherAdapter.setStateMode(StateMode.Normal);
            invalidateOptionsMenu();
        });

        binding.setOnSelectAllClickListener(v -> {
            setDeleteModeSelectedState(getDeleteModeSelectedState() == DeleteModeSelectedState.AllSelected ? DeleteModeSelectedState.Unselected : DeleteModeSelectedState.AllSelected);
            deleteModePresenter.setAllItemsStateSelected(favoriteWeatherAdapter.getFavoriteWeatherDataModels(), getDeleteModeSelectedState());
        });

        binding.setOnDeleteClickListener(v -> deleteModePresenter.deleteAllSelectedFavoriteWeatherDataFromDatabase(favoriteWeatherAdapter.getFavoriteWeatherDataModels()));
    }

    @Override
    public void onSearchItemClicked(SearchDataModel searchDataModel) {
        setStateMode(StateMode.Normal);
        if (favoriteWeatherDatabasePresenter.isFavoriteWeatherDataExistInDatabase(searchDataModel)) {
            onFavoriteWeatherDataFoundInDatabase();
        } else {
            favoriteWeatherPresenter.getFavoriteWeatherData(searchDataModel, Integer.valueOf(favoriteWeatherAdapter.getItemCount()));
        }
    }


    public void searchSearchDataModelAndUpdateData(SearchDataModel searchDataModel) {
        setStateMode(StateMode.Normal);
        if (favoriteWeatherDatabasePresenter.isFavoriteWeatherDataExistInDatabase(searchDataModel)) {
            favoriteWeatherAdapter.addData(favoriteWeatherDatabasePresenter.getSavedFavoriteWeatherDataFromDatabase(searchDataModel));
        } else {
            favoriteWeatherPresenter.getFavoriteWeatherData(searchDataModel, Integer.valueOf(favoriteWeatherAdapter.getItemCount()));
        }
    }

    /**
     * Favorite Weather Presenter - start
     */

    @Override
    public void onFavoriteWeatherDataLoadingStarted() {
        binding.searchView.setQuery("", false);
        binding.searchView.clearFocus();
        binding.setIsFavoriteWeatherLoading(true);
    }

    @Override
    public void onFavoriteWeatherDataLoaded(CurrentWeatherDataModel currentWeatherDataModel, Integer orderIndex) {

        if (!favoriteWeatherDatabasePresenter.isFavoriteWeatherDataExistInDatabase(currentWeatherDataModel)) {
            currentWeatherDataModel.orderIndex = orderIndex;
            Long id = favoriteWeatherDatabasePresenter.saveFavoriteDataInDatabase(currentWeatherDataModel);
            currentWeatherDataModel.id = id;
        }

        binding.setIsFavoriteWeatherLoading(false);
        favoriteWeatherAdapter.addData(currentWeatherDataModel);

        setStateMode(StateMode.Normal);
        invalidateOptionsMenu();
    }

    @Override
    public void onFavoriteWeatherDataLoadedWithError(String errorMessage) {
        binding.setIsFavoriteWeatherLoading(false);
        showErrorMessage(errorMessage);
    }

    @Override
    public void onFavoriteWeatherDataFoundInDatabase() {
        binding.setIsFavoriteWeatherLoading(false);
        hideErrorMessage();
    }

    @Override
    public void onFavoriteWeatherDataWasAddedInDatabaseInBackground(CurrentWeatherDataModel currentWeatherDataModel) {
        binding.setIsFavoriteWeatherLoading(false);
        hideErrorMessage();
        favoriteWeatherAdapter.addData(currentWeatherDataModel);
    }

    // Favorite Weather Presenter - Saved Data callbacks

    @Override
    public void onSavedFavoriteWeatherDataUpdatingStarted() {
        if (!binding.swipeRefreshLayout.isRefreshing()) {
            binding.swipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public void onSavedFavoriteWeatherDataUpdated(CurrentWeatherDataModel currentWeatherDataModel) {
        favoriteWeatherAdapter.updateDataAtPosition(currentWeatherDataModel, favoriteWeatherAdapter.getItemCount() - currentWeatherDataModel.orderIndex.intValue() - 1);
    }

    @Override
    public void onSavedFavoriteWeatherDataUpdated() {
        if (binding.swipeRefreshLayout.isRefreshing()) {
            binding.swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onSavedFavoriteWeatherDataUpdatingFinishedWithError(String errorMessage) {
        if (binding.swipeRefreshLayout.isRefreshing()) {
            binding.swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * Favorite Weather Presenter - end
     */


    /**
     * Search view callbacks - start
    */

    @Override
    public void onSearchDataLoadingStarted() {
        searchAdapter.clearData();
        setSearchStateMode(SearchStateMode.Loading);
        hideErrorMessage();
    }

    @Override
    public void onSearchDataLoaded(List<SearchDataModel> searchDataModels) {
        hideErrorMessage();
        setSearchStateMode(SearchStateMode.Normal);
        searchAdapter.updateData(searchDataModels);
    }

    @Override
    public void onSearchDataLoadedWithError(String errorMessage) {
        showErrorMessage(errorMessage);
        setSearchStateMode(SearchStateMode.Normal);
    }

    @Override
    public void setSearchEmpty() {
        setSearchStateMode(SearchStateMode.Empty);
        searchAdapter.clearData();
    }

    @Override
    public void stopSearchLoadingAndCleanData() {
        setSearchStateMode(SearchStateMode.Normal);
        searchAdapter.clearData();
    }

    /**
     * Search view callbacks - end
     */


    /**
     * Delete/Edit Mode callbacks - start
     */

    @Override
    public void updateView() {
        favoriteWeatherAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFavoriteWeatherItemDeleted(int position) {
        favoriteWeatherAdapter.onItemRemoved(position);

        if (favoriteWeatherAdapter.getFavoriteWeatherDataModels().isEmpty() && (getStateMode() == StateMode.Edit || getStateMode() == StateMode.Delete)) {
            setStateMode(StateMode.Normal);
            invalidateOptionsMenu();
        }
    }

    @Override
    public void setSelectedItemsCount(DeleteModeSelectedState deleteModeSelectedState, int selectedItemsCount) {
        setDeleteModeSelectedState(deleteModeSelectedState);
        binding.setSelectedItemsCount(selectedItemsCount);
    }

    /**
     * Delete/Edit Mode callbacks - end
     */



    @Override
    public void onFavoriteWeatherItemDismiss(List<CurrentWeatherDataModel> currentWeatherDataModels, CurrentWeatherDataModel deletedCurrentWeatherDataModel, int position) {
        editModePresenter.deleteFavoriteWeatherDataFromDatabase(currentWeatherDataModels, deletedCurrentWeatherDataModel, position);
    }

    @Override
    public void onFavoriteWeatherItemMoved(int fromPosition, int toPosition) {
        editModePresenter.onFavoriteWeatherItemMoved(favoriteWeatherAdapter.getFavoriteWeatherDataModels(), fromPosition, toPosition);
    }

    @Override
    public void onFavoriteItemSelectedStateChanged(CurrentWeatherDataModel currentWeatherDataModel, int itemsSize, int position, Boolean isSelected) {
        deleteModePresenter.itemSelectedStateChanged(currentWeatherDataModel, itemsSize, isSelected);
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
