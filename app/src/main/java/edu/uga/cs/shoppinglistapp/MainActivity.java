package edu.uga.cs.shoppinglistapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    EditText txtTitle;
    private Button mFirebasebtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUtil.openFbReference("shoppinglists", this);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        txtTitle = (EditText) findViewById(R.id.listName);
        /**
         * Getting already made lists from the database...
         */
        mFirebasebtn = (Button) findViewById(R.id.newList);


        final ListAdapter myAdapter = new FirebaseListAdapter<ShoppingList>(this, ShoppingList.class,
                android.R.layout.simple_list_item_1, mDatabaseReference) {
            @Override
            protected void populateView(View v, ShoppingList list, int position) {
                ((TextView)v.findViewById(android.R.id.text1)).setText(list.getTitle());
            }
        };
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(myAdapter);
        mFirebasebtn.setOnClickListener(this);


        // upon clicking an item, shows the contents of
        // the grocery list, and passes grocery list name to
        // the next activity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),ViewListActivity.class);
                ShoppingList lTitle = new ShoppingList();
                lTitle = (ShoppingList) listView.getItemAtPosition(position);
                String gName = lTitle.getTitle();
                intent.putExtra("listName",gName);
                startActivity(intent);
            }
        });
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
            case R.id.roommates:
                Intent i = new Intent(this,RoommatesActivity.class);
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


