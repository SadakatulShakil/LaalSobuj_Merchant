
package com.futureskyltd.app.utils.EditMerchant;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Errors implements Serializable {

    @SerializedName("phone_no")
    @Expose
    private PhoneNo phoneNo;
    @SerializedName("email")
    @Expose
    private Email email;

    public PhoneNo getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(PhoneNo phoneNo) {
        this.phoneNo = phoneNo;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Errors{" +
                "phoneNo=" + phoneNo +
                ", email=" + email +
                '}';
    }
}
