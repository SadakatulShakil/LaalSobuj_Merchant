
package com.futureskyltd.app.utils.OrderDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ExpectedDelivery implements Serializable {

    @SerializedName("from")
    @Expose
    private Integer from;
    @SerializedName("to")
    @Expose
    private Integer to;

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

}
