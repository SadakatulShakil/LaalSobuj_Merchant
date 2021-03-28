
package com.futureskyltd.app.utils.OrderDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderDetails implements Serializable {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("result")
    @Expose
    private Result result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "OrderDetails{" +
                "status='" + status + '\'' +
                ", result=" + result +
                '}';
    }
}
