package com.gmail.plai2.ying.HealthTap.ui.workout.search_exercise_tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    // Input fields
    private LocalDate mCurrentDateInput;
    private ExerciseType mExerciseTypeInput;

    // UI fields
    private WorkoutViewModel mViewModel;
    private AvailableExerciseAdapter mAdapter;
    private TextView mFavoriteInstructionsTV;
    private ImageView mFavoriteInstructionsIV;
    private RecyclerView mAvailableExerciseRV;

    // Empty Constructor
    public FavoriteFragment() {
        // To enable menu for this fragment
        setHasOptionsMenu(true);
    }

    // New instance constructor
    static FavoriteFragment newInstance(LocalDate currentDateInput, ExerciseType exerciseTypeInput) {
        FavoriteFragment fragment = new FavoriteFragment();
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
            if (exerciseInfo != null)
                mExerciseTypeInput = TypeConverters.intToExerciseType(Integer.parseInt(exerciseInfo.get(0)));
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Initialize fields and variables
        View root = inflater.inflate(R.layout.fragment_favorite, container, false);
        mViewModel = ViewModelProviders.of(this).get(WorkoutViewModel.class);
        mAvailableExerciseRV = root.findViewById(R.id.favorite_exercise_list_rv);
        mFavoriteInstructionsTV = root.findViewById(R.id.favorite_instructions_tv);
        mFavoriteInstructionsIV = root.findViewById(R.id.favorite_instructions_iv);
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

            @Override
            public boolean onLongClick(View view, int position) {
                return false;
            }
        });
        mAvailableExerciseRV.setAdapter(mAdapter);

        // Observe live data
        mViewModel.getAllAvailableFavoritedExercise(true, mExerciseTypeInput).observe(getViewLifecycleOwner(), (List<AvailableExerciseItem> availableFavoritedExercises) -> {
            mAdapter.setFullList(availableFavoritedExercises);
            mAdapter.submitList(availableFavoritedExercises);
            // Hide instructions if the list is not empty
            if (availableFavoritedExercises.size() != 0) {
                mFavoriteInstructionsIV.setVisibility(View.GONE);
                mFavoriteInstructionsTV.setVisibility(View.GONE);
            } else {
                mFavoriteInstructionsIV.setVisibility(View.VISIBLE);
                mFavoriteInstructionsTV.setVisibility(View.VISIBLE);
            }
        });
    }

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
