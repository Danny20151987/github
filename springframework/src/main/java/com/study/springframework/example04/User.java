package com.study.springframework.example04;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/22 11:27 AM
 */
public class User {

    private String userId;
    private String userName;
    private String email;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
