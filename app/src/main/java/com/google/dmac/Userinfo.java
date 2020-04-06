package com.google.dmac;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class Userinfo extends AppCompatActivity {
    Button register1;
    ImageView logosymbol,joinus;
    TextView navmain;
    AutoCompleteTextView usernamer1, emailr1, phoner1, passwordr1;
    private FirebaseAuth auth;
    String username, email, phone, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_userinfo);
        logosymbol=(ImageView)findViewById(R.id.ivRegLogo) ;
        joinus=(ImageView) findViewById(R.id.ivJoinUs);
        register1 = (Button) findViewById(R.id.btnSignUp);
        usernamer1 = (AutoCompleteTextView) findViewById(R.id.atvUsernameReg);
        emailr1 = (AutoCompleteTextView) findViewById(R.id.atvEmailReg);
        phoner1 = (AutoCompleteTextView) findViewById(R.id.phoneno);
        passwordr1 = (AutoCompleteTextView) findViewById(R.id.atvPasswordReg);
        navmain=(TextView) findViewById(R.id.tvSignu);
        auth = FirebaseAuth.getInstance();
        register1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registeruser();
            }
        });
        navmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1= new Intent(Userinfo.this,MainActivity.class);
                startActivity(i1);
            }
        });

    }

    private void registeruser() {
        initialize();
        if (!validate()) {
            Toast.makeText(this, "Enter the correct details", Toast.LENGTH_LONG).show();
        } else

            onsuccess();

    }

    public void initialize() {
        username = usernamer1.getText().toString().trim();
        email = emailr1.getText().toString().trim();
        phone = phoner1.getText().toString().trim();
        password = passwordr1.getText().toString().trim();
    }

    public boolean validate() {
        boolean valid = true;
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailr1.setError(" Enter valid email ");
            valid = false;
        }
        if (phone.isEmpty() || !Patterns.PHONE.matcher(phone).matches() || phone.length() != 10) {
            phoner1.setError(" Enter valid phone no");
            valid = false;
        }
        if (password.isEmpty()) {
            passwordr1.setError("Enter valid password ");
            valid = false;
        }
        if (username.isEmpty()) {
            usernamer1.setError("Enter the username");
            valid = false;
        }
        if(password.length()<6&&!password.isEmpty()){
            passwordr1.setError("have atleast 6 characters ");
            valid=false;
        }

        return valid;
    }
    public void checkemailexists(){
        auth.fetchSignInMethodsForEmail(emailr1.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        boolean check =!task.getResult().getSignInMethods().isEmpty();
                        if(check)
                            Toast.makeText(getApplicationContext(),"Email Already Exists!",Toast.LENGTH_LONG).show();
                    }
                });
    }
    public void onsuccess() {
        if (Utils.isNetworkAvailable(Userinfo.this)) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.i("Userinfo",username);
                                Toast.makeText(getApplicationContext(), "Registered Successfully!", Toast.LENGTH_LONG).show();
                                Intent i2 = new Intent(Userinfo.this, Account.class);
                                i2.putExtra("Name",username);
                                startActivity(i2);
                            } else
                                checkemailexists();
                        }
                    }
            );
          }
        else{
            Toast.makeText(Userinfo.this,"Connect to the Internet",Toast.LENGTH_LONG).show();
        }
    }



}




