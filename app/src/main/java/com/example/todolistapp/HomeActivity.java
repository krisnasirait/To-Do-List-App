package com.example.todolistapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.Adapt.ToDoAdapt;
import com.example.todolistapp.Mod.ToDoMod;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements OnDialogCloseList {

    TextView date, greetings;
    private RecyclerView recView;
    private FloatingActionButton fActionbtn;
    private FirebaseFirestore firestore;
    private ToDoAdapt adapter;
    private List<ToDoMod> List;
    private Query query;
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        recView = findViewById(R.id.recyclerlview);
        fActionbtn = findViewById(R.id.floatingAcbtn);
        firestore = FirebaseFirestore.getInstance();

        recView.setHasFixedSize(true);
        recView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));

        fActionbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddingTask.newInstance().show(getSupportFragmentManager(), AddingTask.tag);
            }
        });

        date = findViewById(R.id.text_date);

        greetings = (TextView)findViewById(R.id.text_greetings);

        Calendar cal = Calendar.getInstance();
        int clock = cal.get(Calendar.HOUR_OF_DAY);

        if(clock >= 0 && clock < 12){
            greetings.setText("Good Morning");
        }
        else if (clock >= 12 && clock < 16){
            greetings.setText("Good Afternoon");
        }
        else if (clock >= 16 && clock < 21){
            greetings.setText("Good Evening");
        }
        else if (clock >= 21 && clock < 24){
            greetings.setText("Good Night");
        }
        else{
            greetings.setText("Hello");
        }

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String time = format.format(cal.getTime());

        TextView jam = findViewById(R.id.clock);
        jam.setText(time);

        Date currTime = Calendar.getInstance().getTime();

        String formDate = DateFormat.getDateInstance(DateFormat.FULL).format(currTime);

        String[] datesplit = formDate.split(",");

        date.setText(datesplit[0] + ", " + datesplit[1]);


        List = new ArrayList<>();
        adapter = new ToDoAdapt(HomeActivity.this, List);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ToHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recView);

        showData();
        recView.setAdapter(adapter);
    }

    private void showData(){
        query = firestore.collection("task").orderBy("time", Query.Direction.DESCENDING);
               listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                 @Override
                public void onEvent(@Nullable  QuerySnapshot value, @Nullable  FirebaseFirestoreException error) {
                     for (DocumentChange documentChange : value.getDocumentChanges()) {
                         if (documentChange.getType() == DocumentChange.Type.ADDED) {
                             String id = documentChange.getDocument().getId();

                             ToDoMod toDoMod = documentChange.getDocument().toObject(ToDoMod.class).withId(id);

                             List.add(toDoMod);
                             adapter.notifyDataSetChanged();
                         }

                     }
                     listenerRegistration.remove();
                 }

        });
    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        List.clear();
        showData();
        adapter.notifyDataSetChanged();
    }
}