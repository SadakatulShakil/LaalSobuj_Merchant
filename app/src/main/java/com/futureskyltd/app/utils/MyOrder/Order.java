
package com.futureskyltd.app.utils.MyOrder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Order implements Serializable {

    @SerializedName("order_id")
    @Expose
    private Integer orderId;
    @SerializedName("totalcostshipp")
    @Expose
    private String totalcostshipp;
    @SerializedName("totalcost")
    @Expose
    private String totalcost;
    @SerializedName("grand_total")
    @Expose
    private Integer grandTotal;
    @SerializedName("order_date")
    @Expose
    private Integer orderDate;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("item_name")
    @Expose
    private String itemName;
    @SerializedName("item_image")
    @Expose
    private String itemImage;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getTotalcostshipp() {
        return totalcostshipp;
    }

    public void setTotalcostshipp(String totalcostshipp) {
        this.totalcostshipp = totalcostshipp;
    }

    public String getTotalcost() {
        return totalcost;
    }

    public void setTotalcost(String totalcost) {
        this.totalcost = totalcost;
    }

    public Integer getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Integer grandTotal) {
        this.grandTotal = grandTotal;
    }

    public Integer getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Integer orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", totalcostshipp='" + totalcostshipp + '\'' +
                ", totalcost='" + totalcost + '\'' +
                ", grandTotal=" + grandTotal +
                ", orderDate=" + orderDate +
                ", status='" + status + '\'' +
                ", itemName='" + itemName + '\'' +
                ", itemImage='" + itemImage + '\'' +
                '}';
    }
}
