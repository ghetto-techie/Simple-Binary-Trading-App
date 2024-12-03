package com.big.shamba.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class User {
    private String userId;
    private String firstName;
    private String middleName;  // Middle name for KYC
    private String lastName;
    private String email;
    private String code; // User's referral Code
    private String userReferreeId; //The referrer's userID
    private String imageUrl;
    private String phoneNumber;
    private long walletBalance; // Wallet information
    private String idNumber; // ID Number for KYC
    private String idType; // ID Type for KYC
    @ServerTimestamp
    private Date dateJoined;
    private Date dob;  // Date of Birth for KYC



    // Constructor
    public User(
            String userId,
            String firstName,
            String middleName,
            String lastName,
            String email,
            String code,
            String userReferreeId,
            String imageUrl,
            String phoneNumber,
            long walletBalance,
            String idNumber,
            String idType,
            Date dob,
            Date dateJoined
    ) {
        this.userId = userId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.code = code;
        this.userReferreeId = userReferreeId;
        this.imageUrl = imageUrl;
        this.phoneNumber = phoneNumber;
        this.walletBalance = walletBalance;
        this.idNumber = idNumber;
        this.idType = idType;
        this.dob = dob;
        this.dateJoined = dateJoined;
    }

    // Default constructor (required by Firestore)
    public User() {
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUserReferreeId() {
        return userReferreeId;
    }

    public void setUserReferreeId(String userReferreeId) {
        this.userReferreeId = userReferreeId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(long walletBalance) {
        this.walletBalance = walletBalance;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Date getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(Date dateJoined) {
        this.dateJoined = dateJoined;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", code='" + code + '\'' +
                ", userReferreeId='" + userReferreeId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", walletBalance=" + walletBalance +
                ", idNumber='" + idNumber + '\'' +
                ", idType='" + idType + '\'' +
                ", dob=" + dob +
                ", dateJoined=" + dateJoined +
                '}';
    }
}
