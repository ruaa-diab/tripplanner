package com.example.tripplanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> taskList;
    private SharedPreferences sharedPreferences;
    private RadioGroup radioGroupFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();
        setupRecyclerView();
        loadData();
        setupClickListeners();
        addSampleTasks();
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.recyclerViewTasks);
        radioGroupFilter = findViewById(R.id.radioGroupFilter);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskList = new ArrayList<>();
        adapter = new TaskAdapter(taskList, this::handleTaskAction);
        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        radioGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            updateTaskList();
        });
    }

    private void loadData() {
        sharedPreferences = getSharedPreferences("TripAppData", MODE_PRIVATE);
        String savedTasks = sharedPreferences.getString("tasks", "");

        if (!savedTasks.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Task>>(){}.getType();
            List<Task> loadedTasks = gson.fromJson(savedTasks, type);
            if (loadedTasks != null) {
                taskList.clear();
                taskList.addAll(loadedTasks);
            }
        }
        updateTaskList();
    }
    private void addSampleTasks() {
        if (taskList.isEmpty()) {
            taskList.add(new Task("1", "Book flights", "Transport", "High", "", "Book round trip", false));
            taskList.add(new Task("2", "Pack bags", "Packing", "Medium", "", "Pack clothes", false));
            taskList.add(new Task("3", "Find hotels", "Accommodation", "High", "", "Research hotels", false));
            saveData();
        }
    }

    private void updateTaskList() {
        boolean showCompleted = radioGroupFilter.getCheckedRadioButtonId() == R.id.radioCompleted;
        List<Task> filtered = new ArrayList<>();

        for (Task task : taskList) {
            if (task.isCompleted() == showCompleted) {
                filtered.add(task);
            }
        }
        adapter.updateList(filtered);
    }

    private void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String tasksJson = gson.toJson(taskList);
        editor.putString("tasks", tasksJson);
        editor.apply();
        updateTaskList();
    }

    private void handleTaskAction(Task task, String action) {
        switch (action) {
            case "edit":
                Intent intent = new Intent(this, EditTaskActivity.class);
                intent.putExtra("taskId", task.getId());
                intent.putExtra("taskTitle", task.getTitle());
                intent.putExtra("taskCategory", task.getCategory());
                intent.putExtra("taskPriority", task.getPriority());
                intent.putExtra("taskDueDate", task.getDueDate());
                intent.putExtra("taskNotes", task.getNotes());
                startActivity(intent);
                break;
            case "delete":
                for (int i = 0; i < taskList.size(); i++) {
                    if (taskList.get(i).getId().equals(task.getId())) {
                        taskList.remove(i);
                        break;
                    }
                }
                saveData();
                break;
            case "toggle":
                for (int i = 0; i < taskList.size(); i++) {
                    if (taskList.get(i).getId().equals(task.getId())) {
                        taskList.get(i).setCompleted(!taskList.get(i).isCompleted());
                        break;
                    }
                }
                saveData();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}