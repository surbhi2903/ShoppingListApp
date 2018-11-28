//package edu.uga.cs.shoppinglistapp;
//
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListAdapter;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.firebase.ui.database.FirebaseListAdapter;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class ListFragment extends Fragment {
//    private FirebaseDatabase mFirebaseDatabase;
//    private DatabaseReference mDatabaseReference;
//
//    TextView listName;
//    EditText itemName;
//    Button newItem;
//
//    private String items[];
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        View view = (RelativeLayout) inflater.inflate(R.layout.list_layout, container, false);
//
//        ListView lv = (ListView) view.findViewById(R.id.itemLV);
//        FirebaseUtil.openFbReference("shoppinglists", getActivity());
//        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
//        mDatabaseReference = FirebaseUtil.mDatabaseReference;
//        ListAdapter lvAdapter = new FirebaseListAdapter<GroceryItem>(getActivity(), GroceryItem.class, R.layout.list_layout, mDatabaseReference) {
//            @Override
//            protected void populateView(View v, GroceryItem item, int position) {
//                ((TextView) v.findViewById(R.id.itemName)).setText(item.getItemName());
//            }
//        };
//        lv.setAdapter(lvAdapter);
//        return view;
//
//    }
//
//}
