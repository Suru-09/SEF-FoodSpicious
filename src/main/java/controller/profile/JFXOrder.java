package controller.profile;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class JFXOrder extends RecursiveTreeObject<JFXOrder> {

    StringProperty orderID;
    StringProperty customerId;
    StringProperty status;

    public JFXOrder(Long orderID, Long customerId, String status) {
        this.orderID = new SimpleStringProperty(orderID.toString());
        this.customerId = new SimpleStringProperty(customerId.toString());
        this.status = new SimpleStringProperty(status);
    }

    public String getCustomerId() {
        return customerId.get();
    }

    public StringProperty customerIdProperty() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId.set(customerId);
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    @Override
    public String toString() {
        return "JFXOrder{" +
                "customerId=" + customerId +
                ", status=" + status +
                '}';
    }
}
