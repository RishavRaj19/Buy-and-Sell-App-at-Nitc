package olditemse_marketplace.com;

public class UserProduct{
    public String ProductName;
    public String ProductPrice;
    public String ProductContactNumber;
    public String ProductDesc;
    public String ProductImageUrl;

    public UserProduct(String productName, String productPrice, String productContactNumber, String productDesc, String productImageUrl) {
        this.ProductName = productName;
        this.ProductPrice = productPrice;
        this.ProductContactNumber = productContactNumber;
        this.ProductDesc = productDesc;
        this.ProductImageUrl = productImageUrl;
    }
}
