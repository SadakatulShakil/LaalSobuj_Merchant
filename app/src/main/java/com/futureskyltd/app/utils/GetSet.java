package com.futureskyltd.app.utils;

/****************
 *
 * @author 'Hitasoft Technologies'
 *
 * Description:
 * This class is used for get and set logged user data
 *
 * Revision History:
 * Version 1.0 - Initial Version
 *
 *****************/
public class GetSet {
    private static boolean isLogged = false;
    private static String userId = null;
    private static String userName = null;
    private static String Password = null;
    private static String token = null;


    public static String getSellerCurrencyName() {
        return sellerCurrencyName;
    }

    public static void setSellerCurrencyName(String sellerCurrencyName) {
        GetSet.sellerCurrencyName = sellerCurrencyName;
    }

    public static String getSellerCurrencyId() {
        return sellerCurrencyId;
    }

    public static void setSellerCurrencyId(String sellerCurrencyId) {
        GetSet.sellerCurrencyId = sellerCurrencyId;
    }

    public static String getSellerCountryName() {
        return sellerCountryName;
    }

    public static void setSellerCountryName(String sellerCountryName) {
        GetSet.sellerCountryName = sellerCountryName;
    }

    public static String getSellerCountryId() {
        return sellerCountryId;
    }

    public static void setSellerCountryId(String sellerCountryId) {
        GetSet.sellerCountryId = sellerCountryId;
    }

    private static String fullName = null;
    private static String imageUrl = null;
    private static String rating = null;
    private static String new_order_count=null;
    private static String delivered_order_count=null,listed_merchandize_value=null;
    private static String total_revenue,complete_transaction,total_items,completed_order_amount,incomplete_order_amount;
    private static  String sellerCurrencySymbol,sellerCurrencyName,sellerCurrencyId,sellerCountryName,sellerCountryId;
    public static String getListedMerchandizeValue() {
        return listed_merchandize_value;
    }

    public static void setListedMerchandizeValue(String listed_merchandize_value) {
        GetSet.listed_merchandize_value = listed_merchandize_value;
    }

    public static String getNewOrderCount() {
        return new_order_count;
    }

    public static void setNewOrderCount(String new_order_count) {
        GetSet.new_order_count = new_order_count;
    }

    public static String getDeliveredOrderCount() {
        return delivered_order_count;
    }

    public static void setDeliveredOrderCount(String delivered_order_count) {
        GetSet.delivered_order_count = delivered_order_count;
    }

    public static String getsellerCurrencySymbol() {
        return sellerCurrencySymbol;
    }

    public static void setsellerCurrencySymbol(String sellerCurrencySymbol) {
        GetSet.sellerCurrencySymbol = sellerCurrencySymbol;
    }

    public static String getTotalRevenue() {
        return total_revenue;
    }

    public static void setTotalRevenue(String total_revenue) {
        GetSet.total_revenue = total_revenue;
    }

    public static String getCompleteTransaction() {
        return complete_transaction;
    }

    public static void setCompleteTransaction(String complete_transaction) {
        GetSet.complete_transaction = complete_transaction;
    }

    public static String getTotalItems() {
        return total_items;
    }

    public static void setTotalItems(String total_items) {
        GetSet.total_items = total_items;
    }

    public static String getCompletedOrderAmount() {
        return completed_order_amount;
    }

    public static void setCompletedOrderAmount(String completed_order_amount) {
        GetSet.completed_order_amount = completed_order_amount;
    }

    public static String getIncompleteOrderAmount() {
        return incomplete_order_amount;
    }

    public static void setIncompleteOrderAmount(String incomplete_order_amount) {
        GetSet.incomplete_order_amount = incomplete_order_amount;
    }

    public static boolean isLogged() {
        return isLogged;
    }

    public static void setLogged(boolean isLogged) {
        GetSet.isLogged = isLogged;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        GetSet.userId = userId;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        GetSet.userName = userName;
    }

    public static String getFullName() {
        return fullName;
    }

    public static void setFullName(String fullName) {
        GetSet.fullName = fullName;
    }

    public static String getImageUrl() {
        return imageUrl;
    }

    public static void setImageUrl(String imageUrl) {
        GetSet.imageUrl = imageUrl;
    }

    public static String getPassword() {
        return Password;
    }

    public static void setPassword(String password) {
        Password = password;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        GetSet.token = token;
    }

    public static void reset() {
        GetSet.setLogged(false);
        GetSet.setPassword(null);
        GetSet.setUserId(null);
        GetSet.setUserName(null);
        GetSet.setToken(null);
    }

    public static void setRating(String rating) {
        GetSet.rating = rating;
    }
    public static String getRating() {
        return rating;
    }
}
