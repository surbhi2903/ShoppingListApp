package edu.uga.cs.shoppinglistapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RoommatesActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth auth;
    EditText emailId;
    Button checkEmailId;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roommates);
        emailId = (EditText)findViewById(R.id.roommate_text);
        checkEmailId = (Button)findViewById(R.id.button);
        FirebaseUtil.openFbReference("users", this);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
       auth = FirebaseAuth.getInstance();
       checkEmailId.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        //Create child in root object
        //Assign some value to child object
        //mDatabaseReference.child("shoppinglists");
        auth.fetchSignInMethodsForEmail(emailId.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        boolean check = !task.getResult().getSignInMethods().isEmpty();

                        if(!check) {
                            Toast.makeText(RoommatesActivity.this, "Email not found", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            saveId();
                            Toast.makeText(RoommatesActivity.this, "Email already present", Toast.LENGTH_SHORT).show();
                            clean();
                        }

                    }
                });
    }


    private void saveId() {
        String email = emailId.getText().toString();
        User userName = new User(email);
        mDatabaseReference.push().setValue(userName);

    }
    private void clean() {
        emailId.setText("");
        emailId.requestFocus();
    }


}
