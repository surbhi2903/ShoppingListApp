package edu.uga.cs.shoppinglistapp;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    EditText txtTitle;
    TextView textView;
    TextView textView1;
    private Button mFirebasebtn;
    private static Activity caller;
    Button bt;
    FirebaseUser firebaseUser;
    private String userID;
    ArrayList<String> listItems = new ArrayList<>();
    ArrayList<String> listKeys = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        user.sendEmailVerification()
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()) {
//                  //          Log.d(TAG, "Email sent.");
//                        }
//                    }
//                });
    }

    @Override
    public void onClick(View v) {
        //Create child in root object
        //Assign some value to child object
        //mDatabaseReference.child("shoppinglists");
        saveList();
        clean();
    }

    private void saveList() {
        String title = txtTitle.getText().toString();
        /*
        List<GroceryItem> gList = new ArrayList<GroceryItem>();
        GroceryItem item1 = new GroceryItem("Bread", "$2.00");
        GroceryItem item2 = new GroceryItem("Milk","$1.00");
        gList.add(item1);
        gList.add(item2);
        */
        ShoppingList list = new ShoppingList(title);
        Map<String,String> users = new HashMap<>();
        users.put(firebaseUser.getUid(),firebaseUser.getEmail());
        list.setUsers(users);
        mDatabaseReference.push().setValue(list);

    }
    private void clean() {
        txtTitle.setText("");
        txtTitle.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu, menu);
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


        FirebaseUtil.openFbReference("shoppinglists", this);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        txtTitle = (EditText) findViewById(R.id.listName);
        /**
         * Getting already made lists from the database...
         */
        mFirebasebtn = (Button) findViewById(R.id.newList);
        caller = this;
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

        userID = firebaseUser.getUid();
        //userID = "p.surbhi2903@gmail.com";

        //Query query = mDatabaseReference.orderByChild("users/l6uhx6wunqZrhItRrToPDsjVNu73").equalTo(userID);
        Query query = mDatabaseReference.orderByChild("users/" + firebaseUser.getUid()).equalTo(firebaseUser.getEmail());

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);

        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        mFirebasebtn.setOnClickListener(this);

        addChildEventListener(query,adapter,listKeys,listItems);


        // upon clicking an item, shows the contents of
        // the grocery list, and passes grocery list name to
        // the next activity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),ViewListActivity.class);
                intent.putExtra("listName",listItems.get(position));
                intent.putExtra("id",listKeys.get(position));
                startActivity(intent);
            }
        });

        FirebaseUtil.attachListener();
        adapter.setNotifyOnChange(true);
        adapter.notifyDataSetChanged();

    }

    private void addChildEventListener(final Query dbRef, final ArrayAdapter<String> adapter,final List<String> listKeys, final List<String> listItems) {
        ChildEventListener childListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ShoppingList sl = new ShoppingList();
                sl.setTitle((String) dataSnapshot.child("title").getValue());

                adapter.add(
                        (String) dataSnapshot.child("title").getValue());

                listKeys.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                int index = listKeys.indexOf(key);

                if (index != -1) {
                    listItems.remove(index);
                    listKeys.remove(index);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbRef.addChildEventListener(childListener);
    }

}
