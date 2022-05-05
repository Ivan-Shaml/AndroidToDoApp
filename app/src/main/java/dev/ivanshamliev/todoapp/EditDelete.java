package dev.ivanshamliev.todoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.time.LocalDateTime;

public class EditDelete extends DatabaseActivity {

    protected TaskModel task;

    protected Button saveTaskBtn;
    protected Button changeStatusBtn;
    protected Button deleteTaskBtn;
    protected EditText taskText;
    protected TextView createdDate;
    protected TextView modifiedDate;
    protected TextView taskStatus;

    protected void back(){
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        task = (TaskModel) (getIntent().getSerializableExtra("task"));

        saveTaskBtn = findViewById(R.id.saveTaskBtn);
        changeStatusBtn = findViewById(R.id.changeStatusBtn);
        deleteTaskBtn = findViewById(R.id.deleteTaskBtn);
        taskText = findViewById(R.id.taskText);
        createdDate = findViewById(R.id.createdDate);
        modifiedDate= findViewById(R.id.modifiedDate);
        taskStatus = findViewById(R.id.taskStatus);

        LoadDataIntoView(task);

        saveTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.setText(taskText.getText().toString());
                task.setDateModifiled(LocalDateTime.now());
                update(task);
                LoadDataIntoView(task);
            }
        });

        changeStatusBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                task.setDone(!task.isDone());
                task.setDateModifiled(LocalDateTime.now());
                updateStatus(task);
                LoadDataIntoView(task);
            }
        });

        deleteTaskBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                delete(task.getId());
                back();
            }
        });
    }

    protected void LoadDataIntoView(TaskModel task) {
        taskText.setText(task.getText());
        createdDate.setText(task.getDateCreated().format(dateTimeFormatter));
        modifiedDate.setText(task.getDateModifiled().format(dateTimeFormatter));

        if(task.isDone()) {
            changeStatusBtn.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.set_pending));
            taskStatus.setText(R.string.taskStatusTVTextDone);
            taskStatus.setTextColor(getApplicationContext().getResources().getColorStateList(R.color.dark_green));
            changeStatusBtn.setText(R.string.tStatusButtonPending);
        } else {
            changeStatusBtn.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.set_done));
            taskStatus.setText(R.string.taskStatusTVTextPending);
            taskStatus.setTextColor(getApplicationContext().getResources().getColorStateList(R.color.set_pending));
            changeStatusBtn.setText(R.string.tStatusButtonDone);
        }
    }
}