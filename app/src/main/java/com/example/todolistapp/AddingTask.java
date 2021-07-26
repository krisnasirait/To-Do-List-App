package com.example.todolistapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddingTask extends BottomSheetDialogFragment {

    private TextView DueDate;
    private EditText TaskEdit;
    private Button savebtn;
    private FirebaseFirestore firestore;
    private Context context;
    private String dueDate = "";
    private String id = "";
    private String dueDateUpd = "";

    public static final String tag = "AddNewTask";

    public static AddingTask newInstance(){
        return new AddingTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable  ViewGroup container, @Nullable  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.adding_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DueDate = view.findViewById(R.id.setDue_date);
        TaskEdit = view.findViewById(R.id.edit_tasktext);
        savebtn = view.findViewById(R.id.save_btn);

        firestore = FirebaseFirestore.getInstance();


        boolean isUpd = false;
        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpd = true;
            String task = bundle.getString("task");
            id = bundle.getString("id");
            dueDateUpd = bundle.getString("due");

            TaskEdit.setText(task);
            DueDate.setText(dueDate);

            if (task.length() > 0){
                savebtn.setEnabled(false);
                savebtn.setBackgroundColor(Color.GRAY);
            }
        }


        TaskEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    savebtn.setEnabled(false);
                    savebtn.setBackgroundColor(getResources().getColor(R.color.abu_abu));
                }else{
                    savebtn.setEnabled(true);
                    savebtn.setBackgroundColor(getResources().getColor(R.color.pink_pastel));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        DueDate.setOnClickListener(v -> {
            Calendar calen = Calendar.getInstance();

            int Month = calen.get(Calendar.MONTH);
            int Year = calen.get(Calendar.YEAR);
            int Day = calen.get(Calendar.DATE);

            DatePickerDialog datepickerdlg = new DatePickerDialog(context, (view1, year, month, dayOfMonth) -> {
                month = month + 1;
                DueDate.setText(dayOfMonth + "/" + month + "/" + year);
                dueDate = dayOfMonth + "/" + month + "/" + year;
            }, Year, Month, Day);

            datepickerdlg.show();

        });

        boolean finalIsUpd = isUpd;
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        boolean finalIsUpd1 = isUpd;
        savebtn.setOnClickListener(v -> {
            String tasked = TaskEdit.getText().toString();

            if(finalIsUpd1){
                firestore.collection("task").document(id).update("task", tasked, "due", dueDate);
                Toast.makeText(context, "Empty task is not allowed !!!", Toast.LENGTH_SHORT).show();

            }else {


                if (tasked.isEmpty()) {
                    Toast.makeText(context, "Empty task is not allowed!!!", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> taskMap = new HashMap<>();

                    taskMap.put("task", tasked);
                    taskMap.put("due", dueDate);
                    taskMap.put("status", 0);
                    taskMap.put("time", FieldValue.serverTimestamp());

                    firestore.collection("task").add(taskMap).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Task Saved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
            dismiss();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if(activity instanceof OnDialogCloseList){
            ((OnDialogCloseList)activity).onDialogClose(dialog);
        }
    }
}