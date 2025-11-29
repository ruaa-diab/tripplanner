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

public class AddTaskActivity extends AppCompatActivity {
    private EditText editTextTitle, editTextNotes;
    private Spinner spinnerCategory, spinnerPriority;
    private TextView textViewDate;
    private Switch switchCustom;
    private Button buttonSave, buttonDate;
    private List<Task> taskList;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initializeViews();
        setupSpinners();
        loadTasks();
        setupEventListeners();
    }

    private void initializeViews() {
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextNotes = findViewById(R.id.editTextNotes);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        textViewDate = findViewById(R.id.textViewDate);
        switchCustom = findViewById(R.id.switchCustom);
        buttonSave = findViewById(R.id.buttonSave);
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

    private void setupEventListeners() {
        buttonDate.setOnClickListener(v -> showDatePicker());
        buttonSave.setOnClickListener(v -> saveTask());
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

    private void saveTask() {
        String title = editTextTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a task title", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setTitle(title);
        task.setCategory(spinnerCategory.getSelectedItem().toString());
        task.setPriority(spinnerPriority.getSelectedItem().toString());
        task.setDueDate(textViewDate.getText().toString().equals("Not set") ? "" : textViewDate.getText().toString());
        task.setNotes(editTextNotes.getText().toString());
        task.setCompleted(false);
        task.setCustom(switchCustom.isChecked());

        taskList.add(task);
        saveTasksToSharedPreferences();

        Toast.makeText(this, "Task added successfully!", Toast.LENGTH_SHORT).show();
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