package edu.uga.cs.shoppinglistapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RoommatesActivity extends AppCompatActivity {
    FirebaseAuth auth;
    EditText emailId;
    Button checkEmailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roommates);
        emailId = (EditText)findViewById(R.id.roommate_text);
        checkEmailId = (Button)findViewById(R.id.button);
        auth = FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        emailId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                ref.child("users").child("username").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.getValue().equals(usrname)){
//                            usrnm.setError("Username Unavailable");
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//
//                });

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void checkEmail(View v) {
        auth.fetchSignInMethodsForEmail(emailId.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        boolean check = !task.getResult().getSignInMethods().isEmpty();
                        
                        if(!check) {
                            Toast.makeText(RoommatesActivity.this, "Email not found", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(RoommatesActivity.this, "Email already present", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }


}
