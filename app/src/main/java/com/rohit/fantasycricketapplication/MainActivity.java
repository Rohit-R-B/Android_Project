package com.rohit.fantasycricketapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText username, password, rePassword;
    Button signUp, signIn;
    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.etUsername);
        password = findViewById(R.id.etPassword);
        rePassword = findViewById(R.id.etRetypePassword);
        signUp = findViewById(R.id.btnSignup);
        signIn = findViewById(R.id.btnSignin);
        db = new DBAdapter(this);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();
                String repass = rePassword.getText().toString(); //value of the re-enter password field

                if(user.equals("") || pass.equals("") || repass.equals(""))
                {
                    Toast.makeText(MainActivity.this,"Please enter all the fields",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // check whether the "enter password" and "re-enter password" fields are matching
                    if(pass.equals(repass)){
                        //check whether user exists in the database
                        Boolean checkuser = db.checkUsername(user);
                        // if user doesn't exist, insert new user into database
                        if(checkuser==false){
                            Boolean insert = db.insertData(user,pass);
                            // if the user's credentials are successfully entered into the user table , he is routed to the game page
                            if(insert==true){
                                Toast.makeText(MainActivity.this,"Registered successfully",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),GameActivity.class);
                                intent.putExtra("team_name",username.getText().toString());
                                startActivity(intent);
                            }
                            else
                                Toast.makeText(MainActivity.this,"Registration Failed",Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(MainActivity.this,"User already exists",Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(MainActivity.this,"Passwords not matching",Toast.LENGTH_SHORT).show();
                }

            }
        });

        // take the user to the sign-in page
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
                startActivity(intent);
            }
        });
    }
}