package dev.ivanshamliev.todoapp;

import java.time.LocalDateTime;

interface OnSelectSuccess{
    void OnElementSelected(Integer Id, String Text, LocalDateTime DateCreated, LocalDateTime DateModified, int isDone);
}