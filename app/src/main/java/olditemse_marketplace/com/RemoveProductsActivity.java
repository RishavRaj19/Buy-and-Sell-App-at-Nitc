package olditemse_marketplace.com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RemoveProductsActivity extends AppCompatActivity {
    private static final String TAG = "RemoveProductsActivity";
    private static ArrayList<Products> list;
    private RecyclerView myRecyclerView;
    private MyAdapterRemoveProducts myAdapterRemoveProducts;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_products);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading!");
        Toolbar toolbar = findViewById(R.id.toolbarRemoveProductsActivity);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: called");
                onBackPressed();
            }
        });

        if(!isInternetConn()){
            Log.d(TAG, "onCreate: check your internet conn");
            Toast.makeText(this, "Check Your Internet Connection!", Toast.LENGTH_LONG).show();
        }else{
            mProgressDialog.show();
        }
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(user.getUid()).child("UserProducts");
            list = new ArrayList<>();
            myRecyclerView = findViewById(R.id.recyclerViewRemoveProducts);
            myRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange: called");
                    list.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: inside dataSnapshot");
                        String userProNameFirebase = (String) dataSnapshot1.child("ProductName").getValue();
                        String userProPriceFirebase = (String) dataSnapshot1.child("ProductPrice").getValue();
                        String userProContactNumberFirebase = (String) dataSnapshot1.child("ProductContactNumber").getValue();
                        String userProDescFirebase = (String) dataSnapshot1.child("ProductDesc").getValue();
                        String userProImageUrlFirebase = (String) dataSnapshot1.child("ProductImageUrl").getValue();
                        String userProUidFirebase = dataSnapshot1.getKey();

                        Log.d(TAG, "onDataChange: " + userProUidFirebase);
                        list.add(new Products(userProNameFirebase, userProPriceFirebase, userProContactNumberFirebase, userProDescFirebase, userProImageUrlFirebase, userProUidFirebase));
                    }
                    if(mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    if(list.size()==0)
                        callAlertBox();
                    Log.d(TAG, "onDataChange: setting Adapter");
                    myAdapterRemoveProducts = new MyAdapterRemoveProducts(RemoveProductsActivity.this, list);
                    myRecyclerView.setAdapter(myAdapterRemoveProducts);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    if(mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                    Toast.makeText(RemoveProductsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            if(mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            Log.e(TAG, "onCreate: " + e.getMessage());
        }
        Log.d(TAG, "onCreate: end");
    }
    void callAlertBox(){
        Log.d(TAG, "callAlertBox: called");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You don't have any item to remove.")
                .setPositiveButton(R.string.go_back, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
    boolean isInternetConn(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo != null)
            return networkInfo.isConnectedOrConnecting();
        else
            return false;
    }
}
