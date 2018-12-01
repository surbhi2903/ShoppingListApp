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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewListActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private TextView listName;
    private TextView itemCost;
    private EditText newItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);
        Bundle extras = getIntent().getExtras();
        String data = extras.getString("listName");
        FirebaseUtil.openFbReference("shoppinglists", this);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        listName = (TextView)findViewById(R.id.listName);
        listName.setText(data);
        newItem = (EditText) findViewById(R.id.itemName);
        ListView listView = (ListView) findViewById(R.id.itemLV);
        Button btn = (Button)findViewById(R.id.addItemBtn);
        ListAdapter myAdapter = new FirebaseListAdapter<GroceryItem>(this, GroceryItem.class,
                R.layout.item_list, mDatabaseReference.child(data).child("items")) {
            @Override
            protected void populateView(View v, GroceryItem item, int position) {
                 ((TextView) v.findViewById(R.id.iName)).setText(item.getItemName());
                 ((TextView) v.findViewById(R.id.itemCost)).setText(item.getItemCost());
            }
        };
        listView.setAdapter(myAdapter);

   //     btn.setOnClickListener(this);
    }

    //@Override
    //public void onClick(View v) {
     //   saveItem();
     //   clean();
    //}

    private void clean() {
        newItem.setText("New Item");
    }

    private void saveItem() {
        String lName = listName.getText().toString();
        String nItem = newItem.getText().toString();
        mDatabaseReference.child(lName).child("items").setValue(nItem);
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
