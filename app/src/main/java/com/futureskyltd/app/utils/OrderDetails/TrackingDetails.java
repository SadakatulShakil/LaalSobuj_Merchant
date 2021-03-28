
package com.futureskyltd.app.utils.OrderDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TrackingDetails implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("shipping_date")
    @Expose
    private String shippingDate;
    @SerializedName("courier_name")
    @Expose
    private String courierName;
    @SerializedName("courier_service")
    @Expose
    private String courierService;
    @SerializedName("tracking_id")
    @Expose
    private String trackingId;
    @SerializedName("notes")
    @Expose
    private String notes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(String shippingDate) {
        this.shippingDate = shippingDate;
    }

    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public String getCourierService() {
        return courierService;
    }

    public void setCourierService(String courierService) {
        this.courierService = courierService;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "TrackingDetails{" +
                "id='" + id + '\'' +
                ", shippingDate='" + shippingDate + '\'' +
                ", courierName='" + courierName + '\'' +
                ", courierService='" + courierService + '\'' +
                ", trackingId='" + trackingId + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
