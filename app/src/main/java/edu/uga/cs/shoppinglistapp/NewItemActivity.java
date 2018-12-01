package edu.uga.cs.shoppinglistapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewItemActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private EditText itemName;
    private EditText itemCost;
    private EditText paidBy;
    private Button saveButton;

    private GroceryItem newItem = null;
    String listName = null;
    int listKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        Bundle extras = getIntent().getExtras();
        listName = extras.getString("listName");
        listKey = extras.getInt("id");

        FirebaseUtil.openFbReference("shoppinglists", this);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        itemName = (EditText) findViewById(R.id.name);
        itemCost = (EditText) findViewById(R.id.iCost);
        paidBy = (EditText) findViewById(R.id.paidName);
        saveButton = (Button) findViewById(R.id.button);
    }

    private void saveItem() {
        String item = itemName.getText().toString();
        String cost = itemCost.getText().toString();
        String name = paidBy.getText().toString();
        newItem = new GroceryItem(item, cost, name);
      //  mDatabaseReference.child(lName).child("items").setValue(item);
    }

}
