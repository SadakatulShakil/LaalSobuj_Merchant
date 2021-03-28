
package com.futureskyltd.app.utils.EditMerchant;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PhoneNo implements Serializable {

    @SerializedName("unique")
    @Expose
    private String unique;

    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }

    @Override
    public String toString() {
        return "PhoneNo{" +
                "unique='" + unique + '\'' +
                '}';
    }
}
