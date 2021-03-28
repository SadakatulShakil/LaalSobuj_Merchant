
package com.futureskyltd.app.utils.OrderDetails;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result implements Serializable {

    @SerializedName("order_id")
    @Expose
    private Integer orderId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("grand_total")
    @Expose
    private Integer grandTotal;
    @SerializedName("item_total")
    @Expose
    private String itemTotal;
    @SerializedName("shipping_price")
    @Expose
    private String shippingPrice;
    @SerializedName("tax")
    @Expose
    private String tax;
    @SerializedName("coupon_discount")
    @Expose
    private Integer couponDiscount;
    @SerializedName("credit_used")
    @Expose
    private Integer creditUsed;
    @SerializedName("gift_amount")
    @Expose
    private Integer giftAmount;
    @SerializedName("sale_date")
    @Expose
    private String saleDate;
    @SerializedName("expected_delivery")
    @Expose
    private ExpectedDelivery expectedDelivery;
    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;
    @SerializedName("delivery_type")
    @Expose
    private String deliveryType;
    @SerializedName("store_id")
    @Expose
    private Integer storeId;
    @SerializedName("store_name")
    @Expose
    private String storeName;
    @SerializedName("store_image")
    @Expose
    private String storeImage;
    @SerializedName("store_address")
    @Expose
    private String storeAddress;
    @SerializedName("barcode")
    @Expose
    private String barcode;
    @SerializedName("barcode_img")
    @Expose
    private String barcodeImg;
    @SerializedName("review_id")
    @Expose
    private Integer reviewId;
    @SerializedName("rating")
    @Expose
    private Integer rating;
    @SerializedName("review_title")
    @Expose
    private String reviewTitle;
    @SerializedName("review_des")
    @Expose
    private String reviewDes;
    @SerializedName("dispute_created")
    @Expose
    private String disputeCreated;
    @SerializedName("dispute_id")
    @Expose
    private Integer disputeId;
    @SerializedName("shipping")
    @Expose
    private Shipping shipping;
    @SerializedName("tracking_details")
    @Expose
    private TrackingDetails trackingDetails;
    @SerializedName("items")
    @Expose
    private List<Item> items = null;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Integer grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(String itemTotal) {
        this.itemTotal = itemTotal;
    }

    public String getShippingPrice() {
        return shippingPrice;
    }

    public void setShippingPrice(String shippingPrice) {
        this.shippingPrice = shippingPrice;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public Integer getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(Integer couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    public Integer getCreditUsed() {
        return creditUsed;
    }

    public void setCreditUsed(Integer creditUsed) {
        this.creditUsed = creditUsed;
    }

    public Integer getGiftAmount() {
        return giftAmount;
    }

    public void setGiftAmount(Integer giftAmount) {
        this.giftAmount = giftAmount;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public ExpectedDelivery getExpectedDelivery() {
        return expectedDelivery;
    }

    public void setExpectedDelivery(ExpectedDelivery expectedDelivery) {
        this.expectedDelivery = expectedDelivery;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public void setStoreImage(String storeImage) {
        this.storeImage = storeImage;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBarcodeImg() {
        return barcodeImg;
    }

    public void setBarcodeImg(String barcodeImg) {
        this.barcodeImg = barcodeImg;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReviewTitle() {
        return reviewTitle;
    }

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public String getReviewDes() {
        return reviewDes;
    }

    public void setReviewDes(String reviewDes) {
        this.reviewDes = reviewDes;
    }

    public String getDisputeCreated() {
        return disputeCreated;
    }

    public void setDisputeCreated(String disputeCreated) {
        this.disputeCreated = disputeCreated;
    }

    public Integer getDisputeId() {
        return disputeId;
    }

    public void setDisputeId(Integer disputeId) {
        this.disputeId = disputeId;
    }

    public Shipping getShipping() {
        return shipping;
    }

    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    public TrackingDetails getTrackingDetails() {
        return trackingDetails;
    }

    public void setTrackingDetails(TrackingDetails trackingDetails) {
        this.trackingDetails = trackingDetails;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Result{" +
                "orderId=" + orderId +
                ", status='" + status + '\'' +
                ", currency='" + currency + '\'' +
                ", grandTotal=" + grandTotal +
                ", itemTotal='" + itemTotal + '\'' +
                ", shippingPrice='" + shippingPrice + '\'' +
                ", tax='" + tax + '\'' +
                ", couponDiscount=" + couponDiscount +
                ", creditUsed=" + creditUsed +
                ", giftAmount=" + giftAmount +
                ", saleDate='" + saleDate + '\'' +
                ", expectedDelivery=" + expectedDelivery +
                ", paymentMode='" + paymentMode + '\'' +
                ", deliveryType='" + deliveryType + '\'' +
                ", storeId=" + storeId +
                ", storeName='" + storeName + '\'' +
                ", storeImage='" + storeImage + '\'' +
                ", storeAddress='" + storeAddress + '\'' +
                ", barcode='" + barcode + '\'' +
                ", barcodeImg='" + barcodeImg + '\'' +
                ", reviewId=" + reviewId +
                ", rating=" + rating +
                ", reviewTitle='" + reviewTitle + '\'' +
                ", reviewDes='" + reviewDes + '\'' +
                ", disputeCreated='" + disputeCreated + '\'' +
                ", disputeId=" + disputeId +
                ", shipping=" + shipping +
                ", trackingDetails=" + trackingDetails +
                ", items=" + items +
                '}';
    }
}
