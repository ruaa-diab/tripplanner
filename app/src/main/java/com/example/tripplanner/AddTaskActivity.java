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
    private EditText editTitle, editNotes;
    private Spinner spinnerCat, spinnerPriority;
    private TextView textDate;
    private Button btnSave, btnDate;
    private List<Task> tasks;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initViews();
        setupSpinners();
        loadTasks();
        setupButtons();
    }

    private void initViews() {
        editTitle = findViewById(R.id.editTextTitle);
        editNotes = findViewById(R.id.editTextNotes);
        spinnerCat = findViewById(R.id.spinnerCategory);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        textDate = findViewById(R.id.textViewDate);
        btnSave = findViewById(R.id.buttonSave);
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

    private void setupButtons() {
        btnDate.setOnClickListener(v -> showDateDialog());
        btnSave.setOnClickListener(v -> saveNewTask());
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

    private void saveNewTask() {
        String title = editTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "Enter task title", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setTitle(title);
        task.setCategory(spinnerCat.getSelectedItem().toString());
        task.setPriority(spinnerPriority.getSelectedItem().toString());
        task.setDueDate(textDate.getText().toString().equals("Not set") ? "" : textDate.getText().toString());
        task.setNotes(editNotes.getText().toString());
        task.setCompleted(false);

        tasks.add(task);
        saveToStorage();

        Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void saveToStorage() {
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String tasksJson = gson.toJson(tasks);
        editor.putString("tasks", tasksJson);
        editor.apply();
    }
}