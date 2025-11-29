package com.example.tripplanner;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> {
    private List<Task> taskList;
    private TaskAdapter.OnTaskActionListener listener;

    public interface OnTaskActionListener {
        void onTaskAction(Task task, String action);
    }

    public TaskAdapter(List<Task> taskList, TaskAdapter.OnTaskActionListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return TaskViewHolder.create(parent, taskList);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task, listener, position);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void updateList(List<Task> newList) {
        taskList = newList;
        notifyDataSetChanged();
    }
}