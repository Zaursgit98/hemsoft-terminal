package az.hemsoft.terminaljx.business.restaurant.model;

import com.google.gson.annotations.SerializedName;

public class TokenResponse {
    private String accessToken;

    @SerializedName("userResponse")
    private User user;

    // Cash will be implemented later if needed
    // private Cash currentCash;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

