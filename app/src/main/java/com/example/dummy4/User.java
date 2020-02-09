package com.example.dummy4;

public class User {

    public  String email;
    public  String token;
    public  float azimuth;

    public User(){

    }

    public User(String email, String token, float azimuth)
    {
        this.email= email;
        this.token= token;
        this.azimuth= azimuth;
    }

}
