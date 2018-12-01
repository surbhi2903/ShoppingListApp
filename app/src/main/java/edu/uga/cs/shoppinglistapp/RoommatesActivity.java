package edu.uga.cs.shoppinglistapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
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
//        mFirebaseDatabase = FirebaseDatabase.getInstance();
//        mDatabaseReference = mFirebaseDatabase.getReference().child("users");

        FirebaseUtil.openFbReference("shoppinglists", this);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        emailId = (EditText)findViewById(R.id.roommate_text);
        checkEmailId = (Button)findViewById(R.id.button);
        auth = FirebaseAuth.getInstance();

        final ListAdapter myAdapter1 = new FirebaseListAdapter<User>(this, User.class, android.R.layout.simple_list_item_2, mDatabaseReference) {
            @Override
            protected void populateView(View v, User user, int position) {
                ((TextView)v.findViewById(android.R.id.text2)).setText(user.getEmail());
            }
        };
        final ListView listView = (ListView) findViewById(R.id.roommate_list);
        listView.setAdapter(myAdapter1);
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
        /*
        List<GroceryItem> gList = new ArrayList<GroceryItem>();
        GroceryItem item1 = new GroceryItem("Bread", "$2.00");
        GroceryItem item2 = new GroceryItem("Milk","$1.00");
        gList.add(item1);
        gList.add(item2);
        */
        User userName = new User(email);
        mDatabaseReference.push().setValue(userName);

    }
    private void clean() {
        emailId.setText("");
        emailId.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.items_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Logout", "User Logged Out");
                                FirebaseUtil.attachListener();
                            }
                        });
                FirebaseUtil.detachListener();
                return true;
            case R.id.roommates:
                Intent roommate = new Intent(this,RoommatesActivity.class);
                this.startActivity(roommate);
                return true;
            case R.id.shopping_list:
                Intent i = new Intent(this,MainActivity.class);
                this.startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.attachListener();
    }


}
