package dev.ivanshamliev.todoapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public abstract class DatabaseActivity extends AppCompatActivity {

    protected final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    protected void showToastNotification(String message) {
        Toast.makeText(getApplicationContext(),
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    protected void showConfirmationDialog(String title, String message, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, onClickListener)
                .setNegativeButton(android.R.string.no, onClickListener)
                .show();
    }

    protected void deleteDone() {
        String sqlQuery = "DELETE FROM Todos WHERE isDone = 1";
        try {
            execSQL(sqlQuery, null, new OnQuerySuccess() {
                @Override
                public void OnSuccess() {
                    showToastNotification("DELETE SUCCESSFUL");
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            showToastNotification(ex.getMessage());
        }
    }

    protected void updateStatus(TaskModel task) {
        String sqlQuery = "UPDATE Todos SET isDone = ? , DateModified = ? WHERE Id = ?";
        Object[] args = new Object[3];
        args[0] = task.isDone();
        args[1] = task.getDateModifiled().format(dateTimeFormatter);
        args[2] = task.getId();

        try {
            execSQL(sqlQuery, args, new OnQuerySuccess() {
                @Override
                public void OnSuccess() {
                    showToastNotification("STATUS UPDATE SUCCESSFUL");
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            showToastNotification(ex.getMessage());
        }
    }

    protected void insert(TaskModel task) {

        try {
            execSQL(
                    "INSERT INTO Todos( " +
                            "Text, DateCreated, DateModified, isDone ) VALUES( ?, ?, ?, ? ) ",
                    new Object[]{
                            task.getText(),
                            task.getDateCreated().format(dateTimeFormatter),
                            task.getDateModifiled().format(dateTimeFormatter),
                            task.isDone()
                    },
                    () -> showToastNotification("INSERT SUCCESSFUL")
            );
        } catch (Exception ex) {
            showToastNotification(ex.getMessage());
        }
    }

    protected void update(TaskModel task) {

        try {
            execSQL(
                    "UPDATE Todos SET Text = ?, DateModified = ? WHERE Id = ?",
                    new Object[]{
                            task.getText(),
                            task.getDateModifiled().format(dateTimeFormatter),
                            task.getId()
                    },
                    () -> showToastNotification("UPDATE SUCCESSFUL")
            );
        } catch (Exception ex) {
            showToastNotification(ex.getMessage());
        }
    }

    protected void delete(int id) {
        try {
            execSQL(
                    "DELETE FROM Todos WHERE Id = " + id,
                    null,
                    () -> showToastNotification("DELETE SUCCESSFUL")
            );
        } catch (Exception ex) {
            showToastNotification(ex.getMessage());
        }
    }

    protected void selectSQL(String SelectQ, String[] args, OnSelectSuccess
            success)
            throws Exception {
        SQLiteDatabase db =
                SQLiteDatabase.openOrCreateDatabase(
                        getFilesDir().getPath() + getString(R.string.db),
                        null
                );
        Cursor cursor = db.rawQuery(SelectQ, args);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") Integer id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Id")));
            @SuppressLint("Range") String text = cursor.getString(cursor.getColumnIndex("Text"));
            @SuppressLint("Range") LocalDateTime dateCreated = LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("DateCreated")), dateTimeFormatter);
            @SuppressLint("Range") LocalDateTime dateModified = LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("DateModified")), dateTimeFormatter);
            @SuppressLint("Range") Integer isDone = Integer.parseInt(cursor.getString(cursor.getColumnIndex("isDone")));
            success.onElementSelected(id, text, dateCreated, dateModified, isDone);
        }
        db.close();

    }


    private void execSQL(String SQL, Object[] args,
                         OnQuerySuccess success)
            throws Exception {
        SQLiteDatabase db =
                SQLiteDatabase.openOrCreateDatabase(
                        getFilesDir().getPath() + getString(R.string.db),
                        null
                );

        if (args != null)
            db.execSQL(SQL, args);
        else
            db.execSQL(SQL);
        db.close();
        success.OnSuccess();
    }

    protected void initDB() throws Exception {
        execSQL(
                "CREATE TABLE if not exists Todos( " +
                        "Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "Text TEXT not null, " +
                        "DateCreated TEXT not null, " +
                        "DateModified TEXT not null, " +
                        "isDone BOOLEAN not null ); ",
                null,
                () -> showToastNotification("DB INITIALIZED SUCCESSFULLY")
        );
    }

    protected void loadData() {
        try {
            initDB();
        } catch (Exception ex) {
            showToastNotification(ex.getMessage());
        }
    }

    protected interface OnQuerySuccess {
        public void OnSuccess();
    }
}
