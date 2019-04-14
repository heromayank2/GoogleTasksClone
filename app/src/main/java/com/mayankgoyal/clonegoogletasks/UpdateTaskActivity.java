package com.mayankgoyal.clonegoogletasks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class UpdateTaskActivity extends AppCompatActivity {
    ImageButton backBtn;
    String inputDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);

        Intent parentIntent = getIntent();
        final TextView inputDt = findViewById(R.id.inputDt);
        final String taskName = parentIntent.getStringExtra("Task");
        final String taskKey = parentIntent.getStringExtra("Key");
        final String taskDate = parentIntent.getStringExtra("Date");
        inputDate=taskDate;
        inputDt.setText(taskName);

        backBtn = findViewById(R.id.imageButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateTaskActivity.this, TaskActivity.class);
                startActivity(intent);
            }
        });

        final String[] monthArray = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        final String daysWeek[] = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        CalendarView calendarView = findViewById(R.id.calendarView3);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                inputDate = daysWeek[dayOfWeek].substring(0, 3) + ", " + dayOfMonth + " " + monthArray[month].substring(0, 3);

            }
        });

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskName1 = inputDt.getText().toString();
                Task task;
                task = new Task();
//                task.setTaskName(taskName);
                if (TextUtils.isEmpty(taskName1)) {
                    Toast.makeText(UpdateTaskActivity.this, "Add task!", Toast.LENGTH_LONG).show();
                    inputDt.requestFocus();
                    return;
                }
                if (taskName1.length() < 6) {
                        Toast.makeText(UpdateTaskActivity.this, "Task count must be more than 6", Toast.LENGTH_LONG).show();
                    inputDt.requestFocus();
                    return;
                }
                task.setTaskName(taskName1);
                task.setDate(inputDate);
                FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                database.getReference("Users").child(mFirebaseUser.getUid()).child("Tasks").child(taskKey).setValue(task);
                Intent iph = new Intent(UpdateTaskActivity.this, TaskActivity.class);
                startActivity(iph);
                finish();
            }
        });
        ImageButton delete = findViewById(R.id.imageButton2);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                database.getReference("Users").child(mFirebaseUser.getUid()).child("Tasks").child(taskKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateTaskActivity.this, "Task deleted!!!!", Toast.LENGTH_SHORT).show();
                            Intent iph = new Intent(UpdateTaskActivity.this, TaskActivity.class);
                            startActivity(iph);
                            finish();
                        }
                    }
                });
            }
        });
    }
}
