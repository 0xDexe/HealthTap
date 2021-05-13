package com.gmail.plai2.ying.HealthTap.ui.workout;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.plai2.ying.HealthTap.R;
import com.gmail.plai2.ying.HealthTap.room.CompletedExerciseItem;
import com.gmail.plai2.ying.HealthTap.room.ExerciseType;
import com.gmail.plai2.ying.HealthTap.room.Session;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;


public class CompletedExerciseAdapter extends ListAdapter<CompletedExerciseItem, CompletedExerciseAdapter.CompletedExerciseHolder> {

    // Adaptor fields
    private OnItemClickListener mOnClickListener;


    // Adaptor constructor
    public CompletedExerciseAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        mOnClickListener = listener;
    }

    private static final DiffUtil.ItemCallback<CompletedExerciseItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<CompletedExerciseItem>() {

        @Override
        public boolean areItemsTheSame(@NonNull CompletedExerciseItem oldItem, @NonNull CompletedExerciseItem newItem) {
            return oldItem.getMId() == newItem.getMId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull CompletedExerciseItem oldItem, @NonNull CompletedExerciseItem newItem) {
            return oldItem.compareListOfSessions(newItem.getMListOfSessions()) && oldItem.isChecked() == newItem.isChecked();
        }
    };

    class CompletedExerciseHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        // View holder fields
        private ImageView mCompletedExerciseTypeIcon;
        private MaterialTextView mCompletedExerciseName;
        private MaterialTextView mCompletedExerciseDescription;
        private MaterialTextView mCompletedExerciseHasNote;
        private MaterialCardView mCompletedExerciseCardView;
        private View mCompletedExerciseDivider;

        // View holder constructor
        CompletedExerciseHolder(View itemView) {
             super(itemView);
             mCompletedExerciseTypeIcon = itemView.findViewById(R.id.completed_exercise_type_icon_civ);
             mCompletedExerciseName = itemView.findViewById(R.id.completed_exercise_name_tv);
             mCompletedExerciseDescription = itemView.findViewById(R.id.completed_exercise_description_tv);
             mCompletedExerciseHasNote = itemView.findViewById(R.id.has_note_tv);
             mCompletedExerciseDivider = itemView.findViewById(R.id.workout_divider);
             mCompletedExerciseCardView = itemView.findViewById(R.id.completed_exercise_cv);
             mCompletedExerciseCardView.setOnLongClickListener(this);
             itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mOnClickListener != null) mOnClickListener.onClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (mOnClickListener != null) {
                return mOnClickListener.onLongClick(view, getAdapterPosition());
            }
            return false;
        }
    }

    @NonNull
    @Override
    public CompletedExerciseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_completed_exercise, parent, false);
        return new CompletedExerciseHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedExerciseHolder holder, int position) {
        CompletedExerciseItem currentCompletedExerciseItem = getItem(position);
        StringBuilder description = new StringBuilder();
        String currentInfo;
        List<Session> listOfSessions = currentCompletedExerciseItem.getMListOfSessions();
        if (currentCompletedExerciseItem.getMExerciseType() == ExerciseType.CARDIO) {
            holder.mCompletedExerciseTypeIcon.setImageResource(R.drawable.ic_cardio_session);
            for (int i=0; i<listOfSessions.size(); i++) {
                if (i == listOfSessions.size() -1) {
                    currentInfo = listOfSessions.get(i).getDuration() + " min x " + listOfSessions.get(i).getIntensity()+"%";
                } else {
                    currentInfo = listOfSessions.get(i).getDuration() + " min x " + listOfSessions.get(i).getIntensity() + "%, ";
                }
                description.append(currentInfo);
            }
        } else if (currentCompletedExerciseItem.getMExerciseType() == ExerciseType.STRENGTH) {
            holder.mCompletedExerciseTypeIcon.setImageResource(R.drawable.ic_strength_session);
            for (int i=0; i<listOfSessions.size(); i++) {
                String repOrReps = (listOfSessions.get(i).getReps() == 1) ? " rep x " : " reps x ";
                if (i == listOfSessions.size() -1) {
                    currentInfo = listOfSessions.get(i).getReps() + repOrReps + listOfSessions.get(i).getWeight()+" lbs.";
                } else {
                    currentInfo = listOfSessions.get(i).getReps() + repOrReps + listOfSessions.get(i).getWeight() + " lbs, ";
                }
                description.append(currentInfo);
            }
        } else if (currentCompletedExerciseItem.getMExerciseType() == ExerciseType.CALISTHENICS) {
            holder.mCompletedExerciseTypeIcon.setImageResource(R.drawable.ic_calistenics_session);
            for (int i=0; i<listOfSessions.size(); i++) {
                String repOrReps = (listOfSessions.get(i).getReps() == 1) ? " rep" : " reps";
                if (i == listOfSessions.size() -1) {
                    currentInfo = listOfSessions.get(i).getReps() + repOrReps;
                } else {
                    currentInfo = listOfSessions.get(i).getReps() + repOrReps + ", ";
                }
                description.append(currentInfo);
            }
        }
        if (currentCompletedExerciseItem.getMNote() != null && !currentCompletedExerciseItem.getMNote().equals("")) {
            holder.mCompletedExerciseDivider.setVisibility(View.VISIBLE);
            holder.mCompletedExerciseHasNote.setVisibility(View.VISIBLE);
        } else {
            holder.mCompletedExerciseDivider.setVisibility(View.GONE);
            holder.mCompletedExerciseHasNote.setVisibility((View.GONE));
        }
        CharSequence note = "Note: " + currentCompletedExerciseItem.getMNote();
        SpannableStringBuilder formattedNote = new SpannableStringBuilder(note);
        formattedNote.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.mCompletedExerciseHasNote.setText(formattedNote);
        holder.mCompletedExerciseName.setText(currentCompletedExerciseItem.getMExerciseName());
        holder.mCompletedExerciseDescription.setText(description.toString());
        if (currentCompletedExerciseItem.isChecked()) {
            holder.mCompletedExerciseCardView.setChecked(true);
        } else {
            holder.mCompletedExerciseCardView.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return getCurrentList().size();
    }

    // Other adaptor methods
    public CompletedExerciseItem getExerciseItem(int position) {
        return getItem(position);
    }

    // On click interface
    public interface OnItemClickListener {
        void onClick(View view, int position);
        boolean onLongClick(View view, int position);
    }
}
