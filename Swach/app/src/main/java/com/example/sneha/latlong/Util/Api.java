package com.example.sneha.latlong.Util;


import com.example.sneha.latlong.Activity.Users_new;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

import static com.example.sneha.latlong.Activity.Sign_Up.userID;

public interface Api {
    /*@GET("https://latlong-c835f.firebaseio.com/users/8IKeXKr5HeXJEpuj2yO6DbuWjVB3.json")
    Call<Users_new> getData();*/
    @GET
    public Call<Users_new> getData(@Url String url);
}
