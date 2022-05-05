package dev.ivanshamliev.todoapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class MainActivity extends DatabaseActivity {

    protected Button addItemBtn;
    protected Button deleteAllDoneBtn;
    protected ListView itemsListView;
    protected EditText itemText;

    protected void loadDataIntoView() throws Exception {
        final ArrayList<TaskModel> tasksList =
                new ArrayList<>();
        itemsListView.clearChoices();
        selectSQL(
                "SELECT * FROM TODOS " +
                        "ORDER BY Id",
                null,
                new OnSelectSuccess() {
                    @Override
                    public void onElementSelected(Integer Id, String Text, LocalDateTime DateCreated, LocalDateTime DateModified, int isDone) {
                        tasksList.add(new TaskModel(Id, Text, DateCreated, DateModified, isDone >= 1));
                    }
                }

        );
        ArrayAdapter<TaskModel> arrayAdapter = new ArrayAdapter<TaskModel>(
                this,
                android.R.layout.simple_list_item_checked,
                tasksList
        );
        itemsListView.setAdapter(arrayAdapter);

        for (int i = 0; i < tasksList.size(); i++) {
            itemsListView.setItemChecked(i, tasksList.get(i).isDone());
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity(); //closes app removing it from memory
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addItemBtn = findViewById(R.id.addItemButton);
        itemsListView = findViewById(R.id.itemsListView);
        itemsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        itemsListView.setLongClickable(true);
        itemText = findViewById(R.id.addNoteTextView);
        deleteAllDoneBtn = findViewById(R.id.deleteAllDone);

        loadData();
        try {
            loadDataIntoView();
        } catch (Exception ex) {
            showToastNotification(ex.getMessage());
        }

        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckedTextView v = (CheckedTextView) view;
                boolean currentCheck = v.isChecked();
                TaskModel task = (TaskModel) itemsListView.getItemAtPosition(i);
                task.setDone(currentCheck);
                task.setDateModifiled(LocalDateTime.now());
                updateStatus(task);
            }
        });

        itemsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                TaskModel task = (TaskModel) itemsListView.getItemAtPosition(i);
                Intent intent = new Intent(
                        MainActivity.this,
                        EditDelete.class
                );

                intent.putExtra("task", task);
                startActivity(intent);
                return false;
            }
        });

        addItemBtn.setOnClickListener(view -> {
            try {
                String text = itemText.getText().toString();

                if (text.isEmpty() || text.trim().isEmpty()) {
                    throw new Exception("Please input item description !");
                }

                TaskModel newTask = new TaskModel();
                newTask.setDone(false);
                newTask.setDateCreated(LocalDateTime.now());
                newTask.setDateModifiled(LocalDateTime.now());
                newTask.setText(text);
                insert(newTask);
                loadDataIntoView();
                itemText.setText("");
                itemText.clearFocus();
            } catch (Exception ex) {
                showToastNotification(ex.getMessage());
            }
        });

        deleteAllDoneBtn.setOnClickListener(view -> {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            try {
                                deleteDone();
                                loadDataIntoView();
                            } catch (Exception ex) {
                                showToastNotification(ex.getMessage());
                            }
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            String dialogTitle = getString(R.string.deleteAllDoneItemsDialogTitle);
            String dialogMessage = getString(R.string.deleteAllDoneItemsDialogMessage);

            showConfirmationDialog(dialogTitle, dialogMessage, dialogClickListener);
        });
    }
}