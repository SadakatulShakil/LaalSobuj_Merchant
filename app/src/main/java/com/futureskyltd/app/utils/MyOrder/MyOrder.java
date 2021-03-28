
package com.futureskyltd.app.utils.MyOrder;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyOrder implements Serializable {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("orders")
    @Expose
    private List<Order> orders = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "MyOrder{" +
                "status='" + status + '\'' +
                ", orders=" + orders +
                '}';
    }
}
