
package com.futureskyltd.app.utils.Status;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChangeStatus implements Serializable {

    @SerializedName("status")
    @Expose
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ChangeStatus{" +
                "status='" + status + '\'' +
                '}';
    }
}
