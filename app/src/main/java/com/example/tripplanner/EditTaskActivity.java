package com.example.tripplanner;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class EditTaskActivity extends AppCompatActivity {
    private EditText editTextTitle, editTextNotes;
    private Spinner spinnerCategory, spinnerPriority;
    private TextView textViewDate;
    private Button buttonUpdate, buttonDate;
    private Task currentTask;
    private List<Task> taskList;
    private SharedPreferences sharedPreferences;
    private String taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        initializeViews();
        setupSpinners();
        loadTasks();
        getTaskDataFromIntent();
        populateData();
        setupEventListeners();
    }

    private void getTaskDataFromIntent() {
        String id = getIntent().getStringExtra("taskId");
        String title = getIntent().getStringExtra("taskTitle");
        String category = getIntent().getStringExtra("taskCategory");
        String priority = getIntent().getStringExtra("taskPriority");
        String dueDate = getIntent().getStringExtra("taskDueDate");
        String notes = getIntent().getStringExtra("taskNotes");
        boolean completed = getIntent().getBooleanExtra("taskCompleted", false);
        boolean isCustom = getIntent().getBooleanExtra("taskCustom", false);

        currentTask = new Task(id, title, category, priority, dueDate, notes, completed, isCustom);
        taskId = id;
    }

    private void initializeViews() {
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextNotes = findViewById(R.id.editTextNotes);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        textViewDate = findViewById(R.id.textViewDate);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonDate = findViewById(R.id.buttonDate);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(this,
                R.array.priorities_array, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);
    }

    private void loadTasks() {
        sharedPreferences = getSharedPreferences("TripPlanner", MODE_PRIVATE);
        String tasksJson = sharedPreferences.getString("tasks", "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Task>>(){}.getType();
        taskList = gson.fromJson(tasksJson, type);
        if (taskList == null) {
            taskList = new java.util.ArrayList<>();
        }
    }

    private void populateData() {
        if (currentTask != null) {
            editTextTitle.setText(currentTask.getTitle());
            editTextNotes.setText(currentTask.getNotes());
            textViewDate.setText(currentTask.getDueDate().isEmpty() ? "Not set" : currentTask.getDueDate());

            setSpinnerSelection(spinnerCategory, currentTask.getCategory());
            setSpinnerSelection(spinnerPriority, currentTask.getPriority());
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void setupEventListeners() {
        buttonDate.setOnClickListener(v -> showDatePicker());
        buttonUpdate.setOnClickListener(v -> updateTask());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                    textViewDate.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void updateTask() {
        String title = editTextTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a task title", Toast.LENGTH_SHORT).show();
            return;
        }

        currentTask.setTitle(title);
        currentTask.setCategory(spinnerCategory.getSelectedItem().toString());
        currentTask.setPriority(spinnerPriority.getSelectedItem().toString());
        currentTask.setDueDate(textViewDate.getText().toString().equals("Not set") ? "" : textViewDate.getText().toString());
        currentTask.setNotes(editTextNotes.getText().toString());

        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).getId().equals(taskId)) {
                taskList.set(i, currentTask);
                break;
            }
        }

        saveTasksToSharedPreferences();
        Toast.makeText(this, "Task updated successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void saveTasksToSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String tasksJson = gson.toJson(taskList);
        editor.putString("tasks", tasksJson);
        editor.apply();
    }
}