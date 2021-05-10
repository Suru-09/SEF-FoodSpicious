package domain;

import java.util.Objects;

public class Product extends BaseEntity<Long> {
    private String name;
    private String ingredients;
    private double price;
    private String expirationDate;

    public Product(String name, String ingredients, double price, String expirationDate) {
        this.name = name;
        this.ingredients = ingredients;
        this.price = price;
        this.expirationDate = expirationDate;
    }

    public Product() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public String toString() {
        return "Name: " + name +
                "  Ingriedents: " + ingredients +
                "  Price: " + price +
                "  Expiration Date: " + expirationDate;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return Double.compare(product.getPrice(), getPrice()) == 0 && getName().equals(product.getName()) && getIngredients().equals(product.getIngredients()) && getExpirationDate().equals(product.getExpirationDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getIngredients(), getPrice(), getExpirationDate());
    }
}
