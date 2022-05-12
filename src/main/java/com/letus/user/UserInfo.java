package com.letus.user;

public class UserInfo {
    private String userId;
    private String email;
    private boolean isEmailVerified;
    private String name;
    private String pictureUrl;



    public UserInfo(String userId, String email, boolean isEmailVerified, String name, String pictureUrl) {
        this.userId = userId;
        this.email = email;
        this.isEmailVerified = isEmailVerified;
        this.name = name;
        this.pictureUrl = pictureUrl;
    }
}
