package dev.ivanshamliev.todoapp;

import java.time.LocalDateTime;

interface OnSelectSuccess {
    void onElementSelected(Integer Id, String Text, LocalDateTime DateCreated, LocalDateTime DateModified, int isDone);
}