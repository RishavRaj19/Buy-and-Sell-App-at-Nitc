package olditemse_marketplace.com;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BuyActivity extends AppCompatActivity {
    private static final String TAG = "BuyActivity";
    DatabaseReference myRef;
    RecyclerView myRecyclerView;
    ArrayList<Products> list;
    MyAdapter myAdapter;
    ProgressDialog mProgressDialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        Toolbar toolbar = findViewById(R.id.toolbarBuyActivity);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //onBackPressed();
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading! Please Wait.");

        if(!checkInternetConn()){
            Log.d(TAG, "onCreate: internet conn not there");
            Toast.makeText(this, "Check your internet Connection", Toast.LENGTH_SHORT).show();
        }else{
            mProgressDialog.show();
        }
        try {
            Log.d(TAG, "onCreate: Inside Try");
            myRef = FirebaseDatabase.getInstance().getReference().child("Products");
            myRecyclerView = findViewById(R.id.recyclerViewBuy);
            myRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));


            list = new ArrayList<>();
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange: called");
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: Inside DataSnapShot");

                        String proNameFirebase = (String) dataSnapshot1.child("ProductName").getValue();
                        String proPriceFirebase = (String) dataSnapshot1.child("ProductPrice").getValue();
                        String proContactNumberFirebase = (String) dataSnapshot1.child("ProductContactNumber").getValue();
                        String proDescFirebase = (String) dataSnapshot1.child("ProductDesc").getValue();
                        String proImageUrlFirebase = (String) dataSnapshot1.child("ProductImageUrl").getValue();

                        list.add(new Products(proNameFirebase, proPriceFirebase, proContactNumberFirebase, proDescFirebase, proImageUrlFirebase, null));
                    }
                    if(list.size()==0){
                        callAlertBox();
                    }
                    if(mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    myAdapter = new MyAdapter(BuyActivity.this, list);
                    myRecyclerView.setAdapter(myAdapter);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    if(mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                    Toast.makeText(BuyActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }catch(Exception e){
            if(mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            e.printStackTrace();
        }
        Log.d(TAG, "onCreate: end");
    }
    void callAlertBox(){
        Log.d(TAG, "callAlertBox: called");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Currently there is no item to display. Sorry for the inconvenience.")
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
    boolean checkInternetConn(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo!=null){
            return netInfo.isConnectedOrConnecting();
        }
        else{
            return false;
        }
    }
}