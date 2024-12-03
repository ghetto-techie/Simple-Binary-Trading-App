package com.big.shamba.data.pesapal.models;

import com.google.gson.annotations.SerializedName;

public class AuthRequestPesapal {
    @SerializedName("consumer_key")
    private String consumerKey;

    @SerializedName("consumer_secret")
    private String consumerSecret;

    public AuthRequestPesapal(String consumerKey, String consumerSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }
}
