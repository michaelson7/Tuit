package com.nkwazi_tech.tuit_app.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorisation:key=AAAAKLQBN_Y:APA91bG7w04FFB1b2ng59uWipuyxkTGGXd-7ox-nD3gBusoz_hTvhcXoYrd1jaF3p5z8vN9u_xmlgBJFO1RnbgAvEhkcDAJKfWoYB6EYfnFo-lJ2CsbVlWr1gAWyU9e3heBQIa4PA10v"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification (@Body Sender body);
}
