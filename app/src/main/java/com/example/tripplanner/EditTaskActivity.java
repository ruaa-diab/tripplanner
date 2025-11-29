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
    private EditText editTitle, editNotes;
    private Spinner spinnerCat, spinnerPriority;
    private TextView textDate;
    private Button btnUpdate, btnDate;
    private Task currentTask;
    private List<Task> tasks;
    private SharedPreferences prefs;
    private String taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        setupViews();
        setupSpinners();
        loadTasks();
        getTaskFromIntent();
        fillData();
        setupButtons();
    }

    private void getTaskFromIntent() {
        String id = getIntent().getStringExtra("taskId");
        String title = getIntent().getStringExtra("taskTitle");
        String category = getIntent().getStringExtra("taskCategory");
        String priority = getIntent().getStringExtra("taskPriority");
        String dueDate = getIntent().getStringExtra("taskDueDate");
        String notes = getIntent().getStringExtra("taskNotes");
        boolean completed = getIntent().getBooleanExtra("taskCompleted", false);

        // Use 7 parameters instead of 8 (removed isCustom)
        currentTask = new Task(id, title, category, priority, dueDate, notes, completed);
        taskId = id;
    }

    private void setupViews() {
        editTitle = findViewById(R.id.editTextTitle);
        editNotes = findViewById(R.id.editTextNotes);
        spinnerCat = findViewById(R.id.spinnerCategory);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        textDate = findViewById(R.id.textViewDate);
        btnUpdate = findViewById(R.id.buttonUpdate);
        btnDate = findViewById(R.id.buttonDate);
    }

    private void setupSpinners() {
        String[] categories = {"Packing", "Transport", "Accommodation", "Food", "Other"};
        String[] priorities = {"High", "Medium", "Low"};

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCat.setAdapter(catAdapter);

        ArrayAdapter<String> priAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        priAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priAdapter);
    }

    private void loadTasks() {
        prefs = getSharedPreferences("TripAppData", MODE_PRIVATE);
        String tasksJson = prefs.getString("tasks", "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Task>>(){}.getType();
        tasks = gson.fromJson(tasksJson, type);
        if (tasks == null) {
            tasks = new java.util.ArrayList<>();
        }
    }

    private void fillData() {
        if (currentTask != null) {
            editTitle.setText(currentTask.getTitle());
            editNotes.setText(currentTask.getNotes());

            String dateText = currentTask.getDueDate();
            if (dateText == null || dateText.isEmpty()) {
                textDate.setText("Not set");
            } else {
                textDate.setText(dateText);
            }

            setSpinnerSelection(spinnerCat, currentTask.getCategory());
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

    private void setupButtons() {
        btnDate.setOnClickListener(v -> showDateDialog());
        btnUpdate.setOnClickListener(v -> updateTaskData());
    }

    private void showDateDialog() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dateDialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    String date = day + "/" + (month + 1) + "/" + year;
                    textDate.setText(date);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
    }

    private void updateTaskData() {
        String title = editTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "Enter task title", Toast.LENGTH_SHORT).show();
            return;
        }

        currentTask.setTitle(title);
        currentTask.setCategory(spinnerCat.getSelectedItem().toString());
        currentTask.setPriority(spinnerPriority.getSelectedItem().toString());

        String dateValue = textDate.getText().toString();
        currentTask.setDueDate(dateValue.equals("Not set") ? "" : dateValue);

        currentTask.setNotes(editNotes.getText().toString());


        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(taskId)) {
                tasks.set(i, currentTask);
                break;
            }
        }

        saveData();
        Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void saveData() {
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String tasksJson = gson.toJson(tasks);
        editor.putString("tasks", tasksJson);
        editor.apply();
    }
}