package olditemse_marketplace.com;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class MyAdapterRemoveProducts extends RecyclerView.Adapter<MyAdapterRemoveProducts.MyViewHolderRemoveProducts>{
    private static final String TAG = "MyAdapterRemoveProducts";
    private Context context;
    private ArrayList<Products> products;

    MyAdapterRemoveProducts(Context context, ArrayList<Products> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public MyViewHolderRemoveProducts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called");
        return new MyViewHolderRemoveProducts(LayoutInflater.from(context).inflate(R.layout.cardview_remove_products, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolderRemoveProducts holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");
        holder.nameRemoveP.setText(products.get(position).getProductName());
        holder.priceRemoveP.setText(products.get(position).getProductPrice());
        holder.contactRemoveP.setText(products.get(position).getProductContactNumber());

        Picasso.get().load(products.get(position).getProductImageUrl()).placeholder(R.drawable.default_pic).resize(500, 500).into(holder.productPicRemoveP, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: pic loaded");
            }
            @Override
            public void onError(Exception e) {
                Log.e(TAG, "onError: " + e.getMessage());
                holder.productPicRemoveP.setImageResource(R.drawable.error);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: called");
        return products.size();
    }

    class MyViewHolderRemoveProducts extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView nameRemoveP, priceRemoveP, contactRemoveP;
        ImageView productPicRemoveP;
        Button btnRemoveP;


        MyViewHolderRemoveProducts(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "MyViewHolderRemoveProducts: called");
            nameRemoveP = itemView.findViewById(R.id.tvNameRemoveProducts);
            priceRemoveP = itemView.findViewById(R.id.tvPriceRemoveProducts);
            contactRemoveP = itemView.findViewById(R.id.tvContactNumberRemoveProducts);

            productPicRemoveP = itemView.findViewById(R.id.ivRemoveProducts);

            btnRemoveP = itemView.findViewById(R.id.btnRemoveProducts);
            btnRemoveP.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            String uid = products.get(position).getProductUid();
            final String url = products.get(position).getProductImageUrl();

            products.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, products.size());

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            final DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference(user.getUid()).child("UserProducts");
            myRef1.child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: removed from user products");
                    Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: removal failed");
                    Toast.makeText(context, "Failed Removal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            final DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("Products");
            myRef2.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange: inside");
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        if(url.equals(dataSnapshot1.child("ProductImageUrl").getValue())){
                            myRef2.child(Objects.requireNonNull(dataSnapshot1.getKey())).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: removed from Products");
                                    //Toast.makeText(context, "Removed from Products", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.getMessage());
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                            break;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: " + databaseError);
                    Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            StorageReference myRef3 = FirebaseStorage.getInstance().getReferenceFromUrl(url);
            myRef3.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: Removed from Storage");
                    //Toast.makeText(context, "Removed from Storage", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.getMessage());
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}