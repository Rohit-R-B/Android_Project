package com.rohit.fantasycricketapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login_Activity extends AppCompatActivity {
    EditText username, password;
    Button signin;
    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.etUsername1);
        password = findViewById(R.id.etPassword1);
        signin = findViewById(R.id.btnSignin1);
        db = new DBAdapter(this);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();
                if(user.equals("")||pass.equals(""))
                {
                    Toast.makeText(Login_Activity.this,"Please enter all the fields",Toast.LENGTH_SHORT).show();
                }
                else{
                    // check whether entered credentials match the ones existing in database for a particular user
                    Boolean userExists = db.checkUsernamepassword(user,pass);
                    if(userExists==true)
                    {
                        // take the user to the game activity
                        Toast.makeText(Login_Activity.this,"Sign in successful",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),GameActivity.class);
                        intent.putExtra("team_name",username.getText().toString());
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(Login_Activity.this,"Invalid Credentials",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}