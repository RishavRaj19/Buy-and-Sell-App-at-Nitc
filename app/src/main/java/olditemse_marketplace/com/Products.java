package olditemse_marketplace.com;

import android.util.Log;

public class Products {
    private static final String TAG = "Products";
    private String ProductName;
    private String ProductPrice;
    private String ProductContactNumber;
    private String ProductDesc;
    private String ProductImageUrl;
    private String ProductUid;

    public Products(String ProductName, String ProductPrice, String ProductContactNumber, String ProductDesc, String ProductImageUrl, String ProductUid) {
        this.ProductName = ProductName;
        this.ProductPrice = ProductPrice;
        this.ProductContactNumber = ProductContactNumber;
        this.ProductDesc = ProductDesc;
        this.ProductImageUrl = ProductImageUrl;
        this.ProductUid = ProductUid;
    }

    public String getProductName() {
        Log.d(TAG, "getProductName: called");
        return ProductName;
    }

    public void setProductName(String productName) {
        this.ProductName = productName;
    }

    public String getProductPrice() {
        Log.d(TAG, "getProductPrice: called");
        return ProductPrice;
    }

    public void setProductPrice(String productPrice) {
        this.ProductPrice = productPrice;
    }

    public String getProductDesc() {
        Log.d(TAG, "getProductDesc: called");
        return ProductDesc;
    }

    public void setProductDesc(String productDesc) {
        this.ProductDesc = productDesc;
    }

    public String getProductImageUrl() {
        Log.d(TAG, "getProductImageUrl: called");
        return ProductImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.ProductImageUrl = productImageUrl;
    }

    public String getProductContactNumber() {
        Log.d(TAG, "getProductContactNumber: called");
        return ProductContactNumber;
    }

    public void setProductContactNumber(String productContactNumber) {
        ProductContactNumber = productContactNumber;
    }

    public String getProductUid() {
        Log.d(TAG, "getProductUid: called");
        return ProductUid;
    }

    public void setProductUid(String productUid) {
        ProductUid = productUid;
    }
}
