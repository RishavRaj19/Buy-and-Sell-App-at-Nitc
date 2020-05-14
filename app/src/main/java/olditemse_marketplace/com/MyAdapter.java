package olditemse_marketplace.com;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private static final String TAG = "MyAdapter";
    private Context context;
    private ArrayList<Products> products;

    MyAdapter(Context context, ArrayList<Products> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called");
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.new_cardview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        try {
            holder.name.setText(products.get(position).getProductName());
            holder.price.setText(products.get(position).getProductPrice());
            holder.contactNumber.setText(products.get(position).getProductContactNumber());

            Picasso.get().load(products.get(position).getProductImageUrl()).placeholder(R.drawable.default_pic).resize(800, 800).into(holder.productPic, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess: image loaded");
                }
                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "onError: " + e.getMessage());
                    holder.productPic.setImageResource(R.drawable.error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: called");
        return ((products != null) && (products.size() != 0) ? products.size() : 0);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, price, contactNumber;
        ImageView productPic;

        MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.productNameBuy);
            price = itemView.findViewById(R.id.productPriceBuy);
            contactNumber = itemView.findViewById(R.id.productContactNumberBuy);
            productPic = itemView.findViewById(R.id.productPicBuy);

            contactNumber.setOnClickListener(this);
            productPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewProductActivity.class);
                    intent.putExtra("ImageUrl", products.get(getAdapterPosition()).getProductImageUrl());
                    intent.putExtra("ImageDesc", products.get(getAdapterPosition()).getProductDesc());
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: called");
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + products.get(getAdapterPosition()).getProductContactNumber()));
            context.startActivity(intent);
        }
    }
}