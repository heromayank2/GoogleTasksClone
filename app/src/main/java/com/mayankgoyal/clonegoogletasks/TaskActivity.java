package com.mayankgoyal.clonegoogletasks;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TaskActivity extends AppCompatActivity {
    TextView mText, email;
    TextView name;
    TextView date;
    User user;
    Task task = new Task();
    ImageView profilePic;
    Task taskObject = new Task();
    String emailId, nameId, photoURLId, inputDate;
    private FirebaseDatabase database;
    RecyclerView mRecyclerView;
    CalendarView calender;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mFirebaseUser;
    ArrayList<Task> taskArray = new ArrayList<>();
    private TaskAdapter mTaskAdapter;
    Button signOut,openSource;
    SharedPreferences sharedPreferences;
    GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setContentView(R.layout.logo);
        //display the logo during 5 seconds,
//        new CountDownTimer(5000,1000){
//            @Override
//            public void onTick(long millisUntilFinished){}
//
//            @Override
//            public void onFinish(){
//                //set the new Content of your activity
//                TaskActivity.this.setContentView(R.layout.activity_task);
//            }
//        }.start();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        mRecyclerView = findViewById(R.id.taskRecyclerView);
        database = FirebaseDatabase.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        Intent intent = getIntent();

        sharedPreferences=getSharedPreferences(getPackageName()+"tasks", Context.MODE_PRIVATE);
        emailId=sharedPreferences.getString("Email","N/A");
        nameId=sharedPreferences.getString("Name","N/A");
        photoURLId=sharedPreferences.getString("Photo","N/A");

        user = new User();
        user.setEmail(emailId);
        user.setImageURL(photoURLId);
        user.setName(nameId);

        taskArray = new ArrayList<Task>();
        mTaskAdapter = new TaskAdapter(taskArray,TaskActivity.this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(TaskActivity.this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mTaskAdapter);

        final DatabaseReference ref = database.getReference("Users");
        final Query query = ref.orderByChild("email").equalTo(emailId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    ref.child(mFirebaseUser.getUid()).setValue(user);
                }
                query.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        DatabaseReference refer = FirebaseDatabase.getInstance().getReference("Users");
        refer.child(mFirebaseUser.getUid()).child("Tasks").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    Task obj = dataSnapshot.getValue(Task.class);
                    obj.setKey(dataSnapshot.getKey());
                    taskArray.add(obj);
                    mTaskAdapter.notifyDataSetChanged();
                    ImageView firstImg=findViewById(R.id.firstImage);
                    firstImg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    public void showBottomSheetDialog(View v) {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_layout1, null);
        name = view.findViewById(R.id.name21);
        email = view.findViewById(R.id.email);
        date = view.findViewById(R.id.calendarView2);
        profilePic = view.findViewById(R.id.pic);
        Picasso.get()
                .load(photoURLId)
                .into(profilePic);
        name.setText(nameId);
        email.setText(emailId);

        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
//        Button newList = view.findViewById(R.id.newList);
//        newList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(v.getContext(), "In Development", Toast.LENGTH_SHORT).show();
//                dialog.dismiss();
//            }
//        });
        Button feedback = view.findViewById(R.id.feedback);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent feedbackintent = new Intent(v.getContext(),FeedbackActivity.class);
                feedbackintent.putExtra("Email",emailId);
                startActivity(feedbackintent);
            }
        });
        Button privacy = view.findViewById(R.id.privacy);
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://policies.google.com/privacy?hl=en-US");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        Button terms = view.findViewById(R.id.terms);
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://policies.google.com/terms?hl=en-US");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        openSource = view.findViewById(R.id.opensource);
        openSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
                Toast.makeText(v.getContext(),"Open Source Licenses",Toast.LENGTH_SHORT).show();
            }
        });
        signOut= view.findViewById(R.id.signOut);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(getPackageName()+"tasks", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.clear();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(TaskActivity.this,MainActivity.class));
                finish();

            }
        });

        dialog.show();
    }

    public void showBottomSheetDialog2(View v) {

        //Setting and Displaying Bottom Sheet Dialog View
        Date d1 = new Date();
        String date1 = d1.toString();
        String[] all = date1.split(" ");
        inputDate = all[0] + "," + all[2] + " " + all[1];
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_layout3, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();

        mText = (EditText) view.findViewById(R.id.textTask);
        final String[] monthArray = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        final String daysWeek[] = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        Button addButton = view.findViewById(R.id.addtask);
        final ImageButton delAll = view.findViewById(R.id.buttondel);
        delAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mText.setText("");
            }
        });
        //Extracting Data From Calender View

        calender = view.findViewById(R.id.calendarView);
        calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                inputDate = daysWeek[dayOfWeek].substring(0, 3) + ", " + dayOfMonth + " " + monthArray[month].substring(0, 3);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textTask = mText.getText().toString();
                if (TextUtils.isEmpty(textTask)) {
                    Toast.makeText(TaskActivity.this, "Add task!", Toast.LENGTH_LONG).show();
                    mText.requestFocus();
                    return;
                }
                if (textTask.length() < 6) {
                    Toast.makeText(TaskActivity.this, "Task count must be more than 6", Toast.LENGTH_LONG).show();
                    mText.requestFocus();
                    return;
                }

                //Creating Task Object to Add Data to FIREBASE
                taskObject.setTaskName(textTask);
                taskObject.setDate(inputDate);

                database.getReference("Users").child(mFirebaseUser.getUid()).child("Tasks").push().setValue(taskObject);
                dialog.dismiss();
            }
        });


    }

    public void showBottomSheetDialog3(View v) {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_layout2, null);
        Button vct = view.findViewById(R.id.viewCompletedTasks);
        vct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Under Development",Toast.LENGTH_SHORT).show();
            }
        });
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();
    }


}
