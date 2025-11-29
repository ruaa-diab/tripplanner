package com.example.tripplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TaskViewHolder extends RecyclerView.ViewHolder {
    private CheckBox checkBoxCompleted;
    private TextView textViewTitle, textViewCategory, textViewDate, textViewNotes;
    private View viewPriority;
    private Button buttonEdit, buttonDelete;
    private List<Task> taskList;

    private TaskViewHolder(@NonNull View itemView, List<Task> taskList) {
        super(itemView);
        this.taskList = taskList;
        checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
        textViewTitle = itemView.findViewById(R.id.textViewTitle);
        textViewCategory = itemView.findViewById(R.id.textViewCategory);
        textViewDate = itemView.findViewById(R.id.textViewDate);
        textViewNotes = itemView.findViewById(R.id.textViewNotes);
        viewPriority = itemView.findViewById(R.id.viewPriority);
        buttonEdit = itemView.findViewById(R.id.buttonEdit);
        buttonDelete = itemView.findViewById(R.id.buttonDelete);
    }

    public static TaskViewHolder create(ViewGroup parent, List<Task> taskList) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view, taskList);
    }

    public void bind(Task task, TaskAdapter.OnTaskActionListener listener, int position) {
        textViewTitle.setText(task.getTitle());
        textViewCategory.setText(task.getCategory());
        textViewDate.setText(task.getDueDate().isEmpty() ? "No date set" : task.getDueDate());
        textViewNotes.setText(task.getNotes());
        textViewNotes.setVisibility(task.getNotes().isEmpty() ? View.GONE : View.VISIBLE);
        checkBoxCompleted.setChecked(task.isCompleted());

        int priorityColor = getPriorityColor(task.getPriority());
        viewPriority.setBackgroundColor(priorityColor);

        checkBoxCompleted.setOnCheckedChangeListener(null);
        checkBoxCompleted.setChecked(task.isCompleted());
        checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                Task currentTask = taskList.get(position);
                listener.onTaskAction(currentTask, "toggle");
            }
        });

        buttonEdit.setOnClickListener(v -> {
            Task currentTask = taskList.get(position);
            listener.onTaskAction(currentTask, "edit");
        });

        buttonDelete.setOnClickListener(v -> {
            Task currentTask = taskList.get(position);
            listener.onTaskAction(currentTask, "delete");
        });
    }

    private int getPriorityColor(String priority) {
        switch (priority) {
            case "High": return itemView.getContext().getResources().getColor(R.color.high_priority);
            case "Medium": return itemView.getContext().getResources().getColor(R.color.medium_priority);
            case "Low": return itemView.getContext().getResources().getColor(R.color.low_priority);
            default: return itemView.getContext().getResources().getColor(R.color.low_priority);
        }
    }
}