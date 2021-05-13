package com.gmail.plai2.ying.HealthTap.ui.workout.search_exercise_tabs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.plai2.ying.HealthTap.MainActivity;
import com.gmail.plai2.ying.HealthTap.R;
import com.gmail.plai2.ying.HealthTap.room.AvailableExerciseItem;
import com.gmail.plai2.ying.HealthTap.room.ExerciseType;
import com.gmail.plai2.ying.HealthTap.room.TypeConverters;
import com.gmail.plai2.ying.HealthTap.ui.workout.WorkoutViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class CustomFragment extends Fragment {

    // Static fields
    private static final String TAG = "CustomFragment";

    // Input fields
    private LocalDate mCurrentDateInput;
    private ExerciseType mExerciseTypeInput;

    // UI fields
    private WorkoutViewModel mViewModel;
    private AvailableExerciseAdapter mAdapter;
    private RecyclerView mAvailableExerciseRV;
    private TextView mCustomInstructionsTV;
    private ImageView mCustomInstructionsIV;
    private FloatingActionButton mAddCustomFAB;

    // Action  mode fields
    private ActionMode mActionMode;
    private boolean mDeleteUsed;

    // Empty constructor
    public CustomFragment() {
        // To enable menu for this fragment
        setHasOptionsMenu(true);
    }

    // New instance constructor
    static CustomFragment newInstance(LocalDate currentDateInput, ExerciseType exerciseTypeInput) {
        CustomFragment fragment = new CustomFragment();
        Bundle bundle = new Bundle();
        String dateInfo = TypeConverters.dateToString(currentDateInput);
        bundle.putString(MainActivity.DATE_INFO, dateInfo);
        ArrayList<String> exerciseInfo = new ArrayList<>();
        exerciseInfo.add(Integer.toString(TypeConverters.exerciseTypeToInt(exerciseTypeInput)));
        bundle.putStringArrayList(MainActivity.EXERCISE_INFO, exerciseInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // To enable menu for this fragment
        setHasOptionsMenu(true);
        // Parse through bundle
        if (getArguments() != null) {
            String dateInfo = getArguments().getString(MainActivity.DATE_INFO);
            mCurrentDateInput = TypeConverters.stringToDate(dateInfo);
            ArrayList<String> exerciseInfo = getArguments().getStringArrayList(MainActivity.EXERCISE_INFO);
            if (exerciseInfo != null) mExerciseTypeInput = TypeConverters.intToExerciseType(Integer.parseInt(exerciseInfo.get(0)));
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Initialize fields and variables
        View root = inflater.inflate(R.layout.fragment_custom, container, false);
        mAvailableExerciseRV = root.findViewById(R.id.custom_exercise_list_rv);
        mCustomInstructionsTV = root.findViewById(R.id.custom_instruction_tv);
        mCustomInstructionsIV = root.findViewById(R.id.custom_instructions_iv);
        mAddCustomFAB = root.findViewById(R.id.add_custom_fab);

        // On click listeners
        mAddCustomFAB.setOnClickListener((View view) -> {
            AddCustomDialogFragment addCustomFragment = AddCustomDialogFragment.newInstance(mExerciseTypeInput);
            if (getFragmentManager() != null) {
                addCustomFragment.show(getFragmentManager(), "add_custom");
            } else {
                Log.e(TAG, "startDialogFragment: Could not get reference to fragment manager");
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Setup adaptor
        mAvailableExerciseRV.setLayoutManager(new LinearLayoutManager(getContext()));
        mAvailableExerciseRV.setHasFixedSize(true);
        mAdapter = new AvailableExerciseAdapter(new AvailableExerciseAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (mActionMode == null) {
                    AvailableExerciseItem currentAvailableExercise = mAdapter.getExerciseItem(position);
                    if (view.getId() == R.id.available_exercise_name_tv) {
                        Bundle bundle = new Bundle();
                        String dateInfo = TypeConverters.dateToString(mCurrentDateInput);
                        bundle.putString(MainActivity.DATE_INFO, dateInfo);
                        ArrayList<String> exerciseInfo = new ArrayList<>();
                        exerciseInfo.add(Integer.toString(TypeConverters.exerciseTypeToInt(mExerciseTypeInput)));
                        exerciseInfo.add(currentAvailableExercise.getMExerciseName());
                        bundle.putStringArrayList(MainActivity.EXERCISE_INFO, exerciseInfo);
                        NavDestination currentDestination = Navigation.findNavController(view).getCurrentDestination();
                        if (currentDestination != null && currentDestination.getId() == R.id.navigation_search_exercise) {
                            Navigation.findNavController(view).navigate(R.id.to_session, bundle);
                        }
                    } else if (view.getId() == R.id.available_exercise_favorited_iv) {
                        if (currentAvailableExercise.isFavorited()) {
                            currentAvailableExercise.setFavorited(false);
                        } else {
                            currentAvailableExercise.setFavorited(true);
                        }
                        mViewModel.update(currentAvailableExercise);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            // Checkable cards ui
            @Override
            public boolean onLongClick(View view, int position) {
                if (mActionMode == null) {
                    if (getActivity() != null) {
                        mActionMode = ((MainActivity) getActivity()).startSupportActionMode(mActionModeCallBack);
                        mDeleteUsed = false;
                    } else {
                        Log.e(TAG, "onLongClick: Could not get reference to activity");
                    }

                    // Hide FAB in delete action mode
                    mAddCustomFAB.hide();

                    // Disable swiping and clicking other tabs in delete action mode
                    SearchExerciseFragment searchFragment = (SearchExerciseFragment)getParentFragment();
                    if (searchFragment != null) {
                        TabLayout tabLayout = searchFragment.getTabLayout();
                        LinearLayout tabStrip = (LinearLayout) tabLayout.getChildAt(0);
                        tabStrip.setEnabled(false);
                        for(int i = 0; i < tabStrip.getChildCount() ; i++) {
                            tabStrip.getChildAt(i).setClickable(false);
                        }
                        searchFragment.getViewPager().disableSwipe(true);
                    }
                }
                // Pass list of checked items to list adapter
                AvailableExerciseItem currentAvailableItem = mAdapter.getExerciseItem(position);
                List<AvailableExerciseItem > checkedItemList = new ArrayList<>();
                for (AvailableExerciseItem  availableItem : mAdapter.getCurrentList()) {
                    checkedItemList.add(new AvailableExerciseItem(availableItem));
                }
                if (currentAvailableItem.isChecked()) {
                    checkedItemList.get(position).setChecked(false);
                } else {
                    checkedItemList.get(position).setChecked(true);
                }
                mAdapter.submitList(checkedItemList);
                return true;
            }
        });
        mAvailableExerciseRV.setAdapter(mAdapter);

        // Observe live data
        mViewModel = ViewModelProviders.of(this).get(WorkoutViewModel.class);
        mViewModel.getAllCustomAvailableExercise(true, mExerciseTypeInput).observe(getViewLifecycleOwner(), (List<AvailableExerciseItem> availableCustomExerciseItems) -> {
            mAdapter.setFullList(availableCustomExerciseItems);
            mAdapter.submitList(availableCustomExerciseItems);
            // Hide instructions if the list is not empty
            if (availableCustomExerciseItems.size() != 0) {
                mCustomInstructionsIV.setVisibility(View.GONE);
                mCustomInstructionsTV.setVisibility(View.GONE);
            } else {
                mCustomInstructionsIV.setVisibility(View.VISIBLE);
                mCustomInstructionsTV.setVisibility(View.VISIBLE);
            }
        });
    }

    // Setup action mode
    private androidx.appcompat.view.ActionMode.Callback mActionModeCallBack = new androidx.appcompat.view.ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(androidx.appcompat.view.ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.delete_menu, menu);
            mode.setTitle(getResources().getString(R.string.delete));
            return true;
        }

        @Override
        public boolean onPrepareActionMode(androidx.appcompat.view.ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(androidx.appcompat.view.ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.delete_menu_item) {
                List<AvailableExerciseItem > uncheckedItemList = new ArrayList<>();
                for (int i = 0; i < mAdapter.getCurrentList().size(); i++) {
                    if (!mAdapter.getCurrentList().get(i).isChecked()) {
                        AvailableExerciseItem unCheckedItem = new AvailableExerciseItem(mAdapter.getCurrentList().get(i)) ;
                        uncheckedItemList.add(unCheckedItem);
                    } else {
                        mViewModel.delete(mAdapter.getCurrentList().get(i));
                    }
                }
                mAdapter.submitList(uncheckedItemList);
                mDeleteUsed = true;
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(androidx.appcompat.view.ActionMode mode) {
            mActionMode = null;
            // If delete icon was not used then uncheck the list before leaving delete action mode
            if (!mDeleteUsed) {
                List<AvailableExerciseItem> uncheckedItemList = new ArrayList<>();
                for (AvailableExerciseItem availableItem : mAdapter.getCurrentList()) {
                    uncheckedItemList.add(new AvailableExerciseItem(availableItem));
                }
                for (int i=0; i<uncheckedItemList.size(); i++) {
                    if (uncheckedItemList.get(i).isChecked()) {
                        uncheckedItemList.get(i).setChecked(false);
                    }
                }
                mAdapter.submitList(uncheckedItemList);
            }
            // Show hidden FAB when leaving delete action mode
            mAddCustomFAB.show();
            // Enable tabs
            SearchExerciseFragment searchFragment = (SearchExerciseFragment)getParentFragment();
            if (searchFragment != null) {
                LinearLayout tabStrip = (LinearLayout) searchFragment.getTabLayout().getChildAt(0);
                tabStrip.setEnabled(true);
                for(int i = 0; i < tabStrip.getChildCount() ; i++) {
                    tabStrip.getChildAt(i).setClickable(true);
                }
                searchFragment.getViewPager().disableSwipe(false);
            }
        }
    };

    // Setup menu options
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_exercise_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_menu_item);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}
