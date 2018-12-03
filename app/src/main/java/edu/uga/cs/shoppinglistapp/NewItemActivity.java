package edu.uga.cs.shoppinglistapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class NewItemActivity extends AppCompatActivity  {

    public static final String DEBUG_TAG = "NewItemActivity";
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private EditText itemName;
    private EditText itemCost;
    private EditText paidBy;
    private Button saveButton;
    private Button backButton;

    String listName = null;
    String listKey = null;
    ArrayList<String> names;
    ArrayList<String> listNames;
    ArrayList<String> listKeys;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        itemName = (EditText) findViewById(R.id.item);
        itemCost = (EditText) findViewById(R.id.item_cost);
        paidBy = (EditText) findViewById(R.id.paidName);
        saveButton = (Button) findViewById(R.id.button);
        backButton = (Button) findViewById(R.id.back);

        Bundle extras = getIntent().getExtras();

        listName = extras.getString("listName");
        listKeys = new ArrayList<>();

        FirebaseUtil.openFbReference("shoppinglists", this);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        names = new ArrayList<>();
        mDatabaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            collectListNames((Map<String, ShoppingList>) dataSnapshot.getValue());
                            if (dataSnapshot.exists()) {
                                int i = 0;
                                for (DataSnapshot d: dataSnapshot.getChildren()) {
                                    listKeys.add(d.getKey());
                                    i++;
                                    Log.e("Key",d.getKey());
                                    Log.e("Title", d.child("title").toString());

                                    if (d.child("title").getValue().toString().equals(listName)) {
                                        listKey = d.getKey();
                                    }
                                }
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        saveButton.setOnClickListener(new ButtonClickListener());



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void collectListNames(Map<String,ShoppingList> lists) {
        listNames = new ArrayList<>();

        for (Map.Entry<String, ShoppingList> entry : lists.entrySet()) {
            Map singleList = (Map) entry.getValue();

            listNames.add(singleList.get("title").toString());

        }
        Log.e("list",listNames.toString());
    }



    private class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            String item = itemName.getText().toString();
            String cost = itemCost.getText().toString();
            String name = paidBy.getText().toString();

            GroceryItem newItem = new GroceryItem(item, cost, name);
          //  groupId = mDatabaseReference.push().getKey();
            Log.e("list",listNames.toString());
            //Log.e("key", listKey.toString());
            mDatabaseReference.child(listKey).child("items").push().setValue(newItem);
            itemName.setText("");
            itemCost.setText("");
            paidBy.setText("");
        }
    }
}
