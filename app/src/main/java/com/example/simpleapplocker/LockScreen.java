package com.example.simpleapplocker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LockScreen extends AppCompatActivity {



    Button btn;
    EditText t1;
    TextView status;

    String v1 , v2;
    public LockScreen(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        btn = (Button)findViewById(R.id.unlock_btn);
        t1 = (EditText)findViewById(R.id.unlock_pass);
        status = (TextView)findViewById(R.id.App_name);


        // v1 and v2 get data from background services
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            v1 = extras.getString("name");
            v2 = extras.getString("pack");

            //The key argument here must match that used in the other activity
        }


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (v1.equals(t1.getText().toString())) {

                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(v2);
                    launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(launchIntent);
                    finish();
                    Toast.makeText(LockScreen.this,   "Password Correct" , Toast.LENGTH_LONG).show();


                }
                else{
                    Toast.makeText(LockScreen.this,   "wrong password", Toast.LENGTH_LONG).show();
                    t1.setText("");
                }

        }

        });



    }
}
