package com.mayankgoyal.clonegoogletasks;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {
    Task task;
    private ArrayList<Task> mTaskArrayList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView task, date;
        public CardView taskcard;
        public RadioButton Completed;

        public MyViewHolder(View view) {
            super(view);
            taskcard = view.findViewById(R.id.taskcard);
            task = view.findViewById(R.id.textView4);
            date = view.findViewById(R.id.calendarView2);
            Completed = view.findViewById(R.id.checkBox);

        }
    }

    public TaskAdapter(ArrayList<Task> taskArrayList, Context context) {
        this.mTaskArrayList = taskArrayList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.to_do_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        task = mTaskArrayList.get(position);
        holder.task.setText(task.getTaskName());
        holder.date.setText(task.getDate());

        holder.taskcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UpdateTaskActivity.class);
                intent.putExtra("Task", task.getTaskName());
                intent.putExtra("Date", task.getDate());
                intent.putExtra("Key", task.getKey());
                mContext.startActivity(intent);
            }
        });
//        holder.taskcard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(),UpdateTaskActivity.class);
//                intent.putExtra("Task",task.getTaskName());
//                intent.putExtra("Date",task.getDate());
//                intent.putExtra("Key",task.getKey());
//                v.getContext().startActivity(intent);
//
//            }
//        });
        holder.Completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Todo Complete That TASK
                Toast.makeText(v.getContext(), "Well Done!", Toast.LENGTH_SHORT).show();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid())
                        .child("Tasks").child(task.getKey());
                reference.setValue(null);
                mTaskArrayList.remove(position);
                notifyDataSetChanged();
                notifyItemRemoved(position);
                notifyItemRangeChanged(0,mTaskArrayList.size());

            }
        });
    }

    @Override
    public int getItemCount() {
        return mTaskArrayList.size();
    }
}
