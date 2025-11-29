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
    private List<Task> filteredList;
    private SharedPreferences sharedPreferences;
    private RadioGroup radioGroupFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupRecyclerView();
        loadTasks();
        setupEventListeners();
        loadSuggestedTasks();
    }

    private void initializeViews() {
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
        filteredList = new ArrayList<>();
        adapter = new TaskAdapter(filteredList, this::onTaskAction);
        recyclerView.setAdapter(adapter);
    }

    private void setupEventListeners() {
        radioGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            filterTasks();
        });
    }

    private void loadSuggestedTasks() {
        if (taskList.isEmpty()) {
            taskList.add(new Task("1", "Book flights ", "Transportation", "High", "", "Book round-trip flights", false, false));
            taskList.add(new Task("2", "Pack swimsuit", "Packing", "Medium", "", "Don't forget swimwear!", false, false));
            taskList.add(new Task("3", "Research restaurants ", "Food", "Low", "", "Find local cuisine spots", false, false));
            taskList.add(new Task("4", "Buy travel insurance", "Documents", "High", "", "Get travel insurance", false, false));
            taskList.add(new Task("5", "Download maps", "Preparation", "Medium", "", "Download offline maps", false, false));
            saveTasks();
        }
    }

    private void loadTasks() {
        sharedPreferences = getSharedPreferences("TripPlanner", MODE_PRIVATE);
        String tasksJson = sharedPreferences.getString("tasks", "");

        if (!tasksJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Task>>(){}.getType();
            List<Task> savedTasks = gson.fromJson(tasksJson, type);
            if (savedTasks != null) {
                taskList.clear();
                taskList.addAll(savedTasks);
            }
        }
        filterTasks();
    }

    private void saveTasks() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String tasksJson = gson.toJson(taskList);
        editor.putString("tasks", tasksJson);
        editor.apply();
        filterTasks();
    }

    private void filterTasks() {
        boolean showCompleted = radioGroupFilter.getCheckedRadioButtonId() == R.id.radioCompleted;

        filteredList.clear();
        for (Task task : taskList) {
            if (task.isCompleted() == showCompleted) {
                filteredList.add(task);
            }
        }
        adapter.updateList(filteredList);
    }

    private void onTaskAction(Task task, String action) {
        switch (action) {
            case "edit":
                Intent intent = new Intent(this, EditTaskActivity.class);
                intent.putExtra("taskId", task.getId());
                intent.putExtra("taskTitle", task.getTitle());
                intent.putExtra("taskCategory", task.getCategory());
                intent.putExtra("taskPriority", task.getPriority());
                intent.putExtra("taskDueDate", task.getDueDate());
                intent.putExtra("taskNotes", task.getNotes());
                intent.putExtra("taskCompleted", task.isCompleted());
                intent.putExtra("taskCustom", task.isCustom());
                startActivity(intent);
                break;
            case "delete":
                for (int i = 0; i < taskList.size(); i++) {
                    if (taskList.get(i).getId().equals(task.getId())) {
                        taskList.remove(i);
                        break;
                    }
                }
                saveTasks();
                break;
            case "toggle":
                for (int i = 0; i < taskList.size(); i++) {
                    if (taskList.get(i).getId().equals(task.getId())) {
                        taskList.get(i).setCompleted(!taskList.get(i).isCompleted());
                        break;
                    }
                }
                saveTasks();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }
}