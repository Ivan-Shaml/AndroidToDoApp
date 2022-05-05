package dev.ivanshamliev.todoapp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("serial")
public class TaskModel implements Serializable {
    private int Id;
    private String Text;
    private LocalDateTime DateCreated;
    private LocalDateTime DateModifiled;
    private boolean isDone;

    public TaskModel()
    {

    }
    public TaskModel(int id, String text, LocalDateTime dateCreated, LocalDateTime dateModifiled, boolean isDone) {
        Id = id;
        Text = text;
        DateCreated = dateCreated;
        DateModifiled = dateModifiled;
        this.isDone = isDone;
    }

    @Override
    public String toString() {
        return Text + "\n";
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public LocalDateTime getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        DateCreated = dateCreated;
    }

    public LocalDateTime getDateModifiled() {
        return DateModifiled;
    }

    public void setDateModifiled(LocalDateTime dateModifiled) {
        DateModifiled = dateModifiled;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
