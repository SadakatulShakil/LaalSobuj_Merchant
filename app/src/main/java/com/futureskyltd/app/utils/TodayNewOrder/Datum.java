
package com.futureskyltd.app.utils.TodayNewOrder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Datum implements Serializable {

    @SerializedName("orderid")
    @Expose
    private Integer orderid;
    @SerializedName("totalcost")
    @Expose
    private Integer totalcost;
    @SerializedName("orderdate")
    @Expose
    private String orderdate;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("deliverytype")
    @Expose
    private String deliverytype;

    public Integer getOrderid() {
        return orderid;
    }

    public void setOrderid(Integer orderid) {
        this.orderid = orderid;
    }

    public Integer getTotalcost() {
        return totalcost;
    }

    public void setTotalcost(Integer totalcost) {
        this.totalcost = totalcost;
    }

    public String getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(String orderdate) {
        this.orderdate = orderdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliverytype() {
        return deliverytype;
    }

    public void setDeliverytype(String deliverytype) {
        this.deliverytype = deliverytype;
    }

    @Override
    public String toString() {
        return "Datum{" +
                "orderid=" + orderid +
                ", totalcost=" + totalcost +
                ", orderdate='" + orderdate + '\'' +
                ", status='" + status + '\'' +
                ", deliverytype='" + deliverytype + '\'' +
                '}';
    }
}
