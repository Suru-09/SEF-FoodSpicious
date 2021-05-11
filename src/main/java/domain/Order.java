package domain;

import java.util.ArrayList;
import java.util.List;

public class Order extends BaseEntity<Long>{

    public enum Status {
        CANCELLED,
        ACCEPTED,
        PENDING,
        REJECTED,
        NA
    }

    private List<Product> productList;
    private Customer customer;
    private Status status;

    public Order(Product product, Customer customer, Status status) {
        productList = new ArrayList<>();
        productList.add(product);
        this.customer = customer;
        this.status = status;
    }

    public Order(Customer customer, Status status) {
        productList = new ArrayList<>();
        this.customer = customer;
        this.status = status;
    }

    public void addProduct(Product p) {
        if(!productList.contains(p))
            productList.add(p);
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProduct(List<Product> productList) {
        this.productList = productList;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "product=" + productList +
                ", customer=" + customer +
                ", status='" + status + '\'' +
                '}';
    }
}
