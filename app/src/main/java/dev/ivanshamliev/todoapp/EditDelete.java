package dev.ivanshamliev.todoapp;

import androidx.core.app.NavUtils;

import android.content.DialogInterface;
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

    protected void back() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onBackPressed() {
        back();
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
        modifiedDate = findViewById(R.id.modifiedDate);
        taskStatus = findViewById(R.id.taskStatus);

        loadDataIntoView(task);

        saveTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = taskText.getText().toString();
                if (text.isEmpty() || text.trim().isEmpty()) {
                    showToastNotification("Please input item description !");
                    return;
                }
                task.setText(text);
                task.setDateModifiled(LocalDateTime.now());
                update(task);
                loadDataIntoView(task);
            }
        });

        changeStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.setDone(!task.isDone());
                task.setDateModifiled(LocalDateTime.now());
                updateStatus(task);
                if (!task.getText().equals(taskText.getText().toString())) {
                    task.setText(taskText.getText().toString());
                }
                loadDataIntoView(task);
            }
        });

        deleteTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                delete(task.getId());
                                back();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                String dialogTitle = getString(R.string.deleteSingleItemDialogTitle);
                String dialogMessage = getString(R.string.deleteSingleItemDialogMessage);

                showConfirmationDialog(dialogTitle, dialogMessage, dialogClickListener);
            }
        });
    }

    protected void loadDataIntoView(TaskModel task) {
        taskText.setText(task.getText());
        createdDate.setText(task.getDateCreated().format(dateTimeFormatter));
        modifiedDate.setText(task.getDateModifiled().format(dateTimeFormatter));

        if (task.isDone()) {
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