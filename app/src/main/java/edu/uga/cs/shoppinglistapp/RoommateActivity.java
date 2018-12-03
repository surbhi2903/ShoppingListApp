package edu.uga.cs.shoppinglistapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RoommateActivity extends AppCompatActivity {
    FirebaseAuth auth;
    EditText emailId;
    Button checkEmailId;
    FirebaseUser firebaseUser;
    ShoppingList shoppingList;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    ArrayAdapter<String> adapter;
    ArrayList<String> listItems = new ArrayList<>();
    ArrayList<String> listKeys = new ArrayList<>();
    String listKey;
    private static Activity caller;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roommate);
        emailId = (EditText)findViewById(R.id.roommate_text);
        checkEmailId = (Button)findViewById(R.id.button);

        caller = this;
        auth = FirebaseAuth.getInstance();

        checkEmailId.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        auth.fetchSignInMethodsForEmail(emailId.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                                        boolean check = !task.getResult().getSignInMethods().isEmpty();

                                        if(!check) {
                                            Toast.makeText(RoommateActivity.this, "Email not found", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            saveId();
                                            Toast.makeText(RoommateActivity.this, "Email already present", Toast.LENGTH_SHORT).show();
                                            clean();
                                        }

                                    }
                                });
                    }
                }
        );

    }

    private void saveId() {
        String email = emailId.getText().toString();
        List<String> users = new ArrayList<>();
        users.addAll(listItems);
        users.add(email);
        mDatabaseReference.child(listKey+"/users").setValue(users);
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
            case R.id.roommates:
                Intent roommate = new Intent(this,RoommateActivity.class);
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

        listItems.clear();
        listKeys.clear();



        Intent intent = getIntent();
        String data = intent.getStringExtra("listName");
        listKey = intent.getStringExtra("id");

        FirebaseUtil.openFbReference("shoppinglists", this);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if( null == firebaseUser){

            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseUtil.attachListener();
                        }
                    });
            FirebaseUtil.detachListener();
            return;
        }

        Query query = mDatabaseReference.orderByKey().equalTo(listKey);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_2, android.R.id.text1,
                listItems);

        final ListView listView = (ListView) findViewById(R.id.roommate_list);
        listView.setAdapter(adapter);
        //     checkEmailId.setOnClickListener(this);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RoommateActivity.this);
                dialogBuilder.setTitle("Delete roommate?");
                dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Pressed", Toast.LENGTH_LONG).show();
                        mDatabaseReference.child(listKey).child("users").child(Integer.toString(position)).removeValue();
                        listItems.remove(position);
                        adapter.notifyDataSetChanged();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "List not deleted."
                                , Toast.LENGTH_SHORT).show();
                    }
                });
                dialogBuilder.create().show();
                return true;
            }
        });

        addChildEventListener(query,adapter,listKeys,listItems);


        FirebaseUtil.attachListener();
        adapter.setNotifyOnChange(true);
        adapter.notifyDataSetChanged();
    }

    private void addChildEventListener(final Query dbRef, final ArrayAdapter<String> adapter, final List<String> listKeys, final List<String> listItems) {
        ChildEventListener childListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                List<String> users = (List<String>) dataSnapshot.child("users").getValue();
                int i = 0;
                for(String user: users) {
                    adapter.add(user);
                    listKeys.add(String.valueOf(i++));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                /*
                String key = dataSnapshot.getKey();
                int index = listKeys.indexOf(key);

                if (index != -1) {
                    listItems.remove(index);
                    listKeys.remove(index);
                    adapter.notifyDataSetChanged();
                }
                */
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbRef.addChildEventListener(childListener);
    }
}
