package com.mayankgoyal.clonegoogletasks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class FeedbackActivity extends AppCompatActivity {
    ImageButton send,back;
    EditText feed;
    TextView mail;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ((TextView) findViewById(R.id.textView6)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) findViewById(R.id.textView6)).setText(Html.fromHtml(getResources().getString(R.string.feedback)));

        sharedPreferences=getSharedPreferences(getPackageName()+"tasks", Context.MODE_PRIVATE);
        String emailId=sharedPreferences.getString("Email","N/A");

        send = findViewById(R.id.imageButton3);
        back = findViewById(R.id.imageButton4);
        mail = findViewById(R.id.textView7);
        mail.setText("From: "+emailId);
        feed = findViewById(R.id.editTextNew);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedbackActivity.this,TaskActivity.class);
                startActivity(intent);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"heromayank2@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Regarding Google Tasks");
                i.putExtra(Intent.EXTRA_TEXT   , feed.getText().toString());
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(FeedbackActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
