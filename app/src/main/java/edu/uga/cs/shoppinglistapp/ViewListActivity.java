package edu.uga.cs.shoppinglistapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class ViewListActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    TextView listName;
    String listKey;
    String data;
    int itemPosition;
    ArrayList<GroceryItem> groceryItems;
    ArrayList<String> itemKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);
        Bundle extras = getIntent().getExtras();
        data = extras.getString("listName");
        listKey = extras.getString("id");
        Log.e("listkey", listKey);
        FirebaseUtil.openFbReference("shoppinglists", this);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        listName = (TextView)findViewById(R.id.listName);
        listName.setText(data);
        itemKeys = new ArrayList<>();
        final ListView listView = (ListView) findViewById(R.id.itemLV);
        Button btn = (Button)findViewById(R.id.addItemBtn);
        mDatabaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        groceryItems = new ArrayList<>();
                        //collectListNames((Map<String, GroceryItem>) dataSnapshot.getValue());
                        if (dataSnapshot.exists()) {
                            int i = 0;
                        }
                        GroceryItem test = dataSnapshot.child(listKey).child("items").getValue(GroceryItem.class);
                        String key = dataSnapshot.child(listKey).child("items").toString();

                        for (DataSnapshot snapshot : dataSnapshot.child(listKey).child("items").getChildren()) {
                            key = snapshot.getKey();
                            itemKeys.add(key);
                        }
                        groceryItems.add(test);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        listView.setLongClickable(true);
         final ListAdapter myAdapter = new FirebaseListAdapter<GroceryItem>(this, GroceryItem.class,
                R.layout.item_list, mDatabaseReference.child(listKey).child("items")) {

            @Override
            protected void populateView(View v, GroceryItem item, int position) {
                Log.e("items", item.getItemName());
                ((TextView) v.findViewById(R.id.iName)).setText(item.getItemName());
                ((TextView) v.findViewById(R.id.item_cost)).setText(item.getItemCost());
                ((TextView) v.findViewById(R.id.purchasedBy)).setText(item.getPurchasedBy());

            }
        };

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ViewListActivity.this);
                dialogBuilder.setTitle("Delete grocery item?");
                dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Item deleted", Toast.LENGTH_LONG).show();
                        mDatabaseReference.child(listKey).child("items").child(itemKeys.get(position)).removeValue();
                        ((FirebaseListAdapter<GroceryItem>) myAdapter).notifyDataSetChanged();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Item not deleted."
                                , Toast.LENGTH_SHORT).show();
                    }
                });
                dialogBuilder.create().show();
                return true;
            }
        });
        listView.setAdapter(myAdapter);
        registerForContextMenu(listView);
        btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), NewItemActivity.class);
        String name = listName.getText().toString();
        intent.putExtra("listName",name);
        intent.putExtra("id", listKey);
        v.getContext().startActivity(intent);
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
                roommate.putExtra("listName",data);
                roommate.putExtra("id",listKey);
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
