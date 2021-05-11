package controller.profile;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class JFXProduct extends RecursiveTreeObject<JFXProduct> {

    StringProperty name;
    StringProperty ingredients;
    StringProperty expirationDate;
    StringProperty price;

    public JFXProduct(String name, String ingredients, String expirationDate, String price) {
        this.name = new SimpleStringProperty(name);
        this.ingredients = new SimpleStringProperty(ingredients);
        this.expirationDate = new SimpleStringProperty(expirationDate);
        this.price = new SimpleStringProperty(price);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getIngredients() {
        return ingredients.get();
    }

    public StringProperty ingredientsProperty() {
        return ingredients;
    }

    public String getExpirationDate() {
        return expirationDate.get();
    }

    public StringProperty expirationDateProperty() {
        return expirationDate;
    }

    @Override
    public String toString() {
        return "JFXProduct{" +
                "name=" + name +
                ", ingredients=" + ingredients +
                ", expirationDate=" + expirationDate +
                ", price=" + price +
                '}';
    }
}
