package dev.ivanshamliev.todoapp;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public abstract class DatabaseActivity extends AppCompatActivity {

    protected final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    protected void deleteDone() {
        String sqlQuery = "DELETE FROM Todos WHERE isDone = 1";
        try {
            ExecSQL(sqlQuery, null, new OnQuerySuccess() {
                @Override
                public void OnSuccess() {
                    Toast.makeText(
                            getApplicationContext(),
                            "DELETE SUCCESSFUL",
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    ex.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    protected void updateStatus(TaskModel task) {
        String sqlQuery = "UPDATE Todos SET isDone = ? , DateModified = ? WHERE Id = ?";
        Object[] args = new Object[3];
        args[0] = task.isDone();
        args[1] = task.getDateModifiled().format(dateTimeFormatter);
        args[2] = task.getId();

        try {
            ExecSQL(sqlQuery, args, new OnQuerySuccess() {
                @Override
                public void OnSuccess() {
                    Toast.makeText(
                            getApplicationContext(),
                            "STATUS UPDATE SUCCESSFUL",
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    ex.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    protected void insert(TaskModel task) {

        try {
            ExecSQL(
                    "INSERT INTO Todos( " +
                            "Text, DateCreated, DateModified, isDone ) VALUES( ?, ?, ?, ? ) ",
                    new Object[]{
                            task.getText(),
                            task.getDateCreated().format(dateTimeFormatter),
                            task.getDateModifiled().format(dateTimeFormatter),
                            task.isDone()
                    },
                    () -> Toast.makeText(getApplicationContext(),
                            "Insert Successful",
                            Toast.LENGTH_LONG
                    ).show()
            );
        }catch (Exception ex) {
            Toast.makeText(getApplicationContext(),
                    ex.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    protected void update(TaskModel task) {

        try {
            ExecSQL(
                    "UPDATE Todos SET Text = ?, DateModified = ? WHERE Id = ?",
                    new Object[]{
                            task.getText(),
                            task.getDateModifiled().format(dateTimeFormatter),
                            task.getId()
                    },
                    () -> Toast.makeText(getApplicationContext(),
                            "Update Successful",
                            Toast.LENGTH_LONG
                    ).show()
            );
        }catch (Exception ex) {
            Toast.makeText(getApplicationContext(),
                    ex.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    protected void delete(int id) {
        try {
            ExecSQL(
                    "DELETE FROM Todos WHERE Id = " + id,
                    null,
                    () -> Toast.makeText(getApplicationContext(),
                            "Delete Successful",
                            Toast.LENGTH_LONG
                    ).show()
            );
        }catch (Exception ex) {
            Toast.makeText(getApplicationContext(),
                    ex.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    protected void SelectSQL(String SelectQ, String[] args, OnSelectSuccess
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
            success.OnElementSelected(id, text, dateCreated, dateModified, isDone);
        }
        db.close();

    }


    protected void ExecSQL(String SQL, Object[] args,
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
        ExecSQL(
                "CREATE TABLE if not exists Todos( " +
                        "Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "Text TEXT not null, " +
                        "DateCreated TEXT not null, " +
                        "DateModified TEXT not null, " +
                        "isDone BOOLEAN not null ); ",
                null,
                () -> {
                    Toast.makeText(getApplicationContext(),
                            R.string.DBInitMssg,
                            Toast.LENGTH_LONG
                    ).show();
                }
        );
    }

    protected void LoadData() {
        try {
            initDB();
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    ex.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    protected interface OnQuerySuccess {
        public void OnSuccess();
    }
}
