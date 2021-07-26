package com.example.todolistapp.Adapt;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.AddingTask;
import com.example.todolistapp.HomeActivity;
import com.example.todolistapp.Mod.ToDoMod;
import com.example.todolistapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ToDoAdapt extends RecyclerView.Adapter<ToDoAdapt.MyViewHolder> {

    private List<ToDoMod> todoList;
    private HomeActivity activity;
    private FirebaseFirestore firestore;

    public ToDoAdapt(HomeActivity homeActivity, List<ToDoMod> todoList){
        this.todoList = todoList;
        activity = homeActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.task, parent, false);
        firestore = FirebaseFirestore.getInstance();
        return new MyViewHolder(view);
    }

    public void deleteTask(int position){
        ToDoMod toDoMod = todoList.get(position);
        firestore.collection("task").document(toDoMod.TaskId).delete();
        todoList.remove(position);
        notifyItemRemoved(position);
    }
    public Context getContext(){
        return activity;
    }

    public void editTask(int position){
        ToDoMod toDoMod = todoList.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("task", toDoMod.getTask());
        bundle.putString("due", toDoMod.getDue());
        bundle.putString("id", toDoMod.TaskId);

        AddingTask addingTask = new AddingTask();
        addingTask.setArguments(bundle);
        addingTask.show(activity.getSupportFragmentManager(), addingTask.getTag());
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoAdapt.MyViewHolder holder, int position) {

        ToDoMod toDoModel = todoList.get(position);
        holder.CheckBox.setText(toDoModel.getTask());
        holder.DueDate.setText("Due On " + toDoModel.getDue());

        holder.CheckBox.setChecked(toBool(toDoModel.getStatus()));

        holder.CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    firestore.collection("task").document(toDoModel.TaskId).update("status", 1);

                }else{
                    firestore.collection("task").document(toDoModel.TaskId).update("status", 0);
                }
            }
        });
    }

    private boolean toBool(int status){
        return status != 0;
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView DueDate;
        CheckBox CheckBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            DueDate = itemView.findViewById(R.id.due_date);
            CheckBox = itemView.findViewById(R.id.mcheckbox);
        }
    }
}
