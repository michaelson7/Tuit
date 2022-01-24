package com.nkwazi_tech.tuit_app.Classes;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface MediaHandler {
    //the base URL for our API
    //make sure you are not using localhost
    //find the ip usinc ipconfig command

    String BASE_URL = "https://myhost.nkwazitech.com/";
//    String BASE_URL = "http://nawa777.000webhostapp.com/";

    @Multipart
    @POST("Api.php?apicall=uploadvideo")
    Call<ServerResponse> uploadvideo(
            @Part("title") RequestBody title,
            @Part("description") RequestBody shortdesc,
            @Part("tags") RequestBody tags,
            @Part("lecturerid") RequestBody lecturerid,
            @Part("courseid") RequestBody courseid,
            @Part("file_Duration") RequestBody file_Duration,
            @Part("file_Size") RequestBody file_Size,
            @Part MultipartBody.Part file,
            @Part MultipartBody.Part file2
    );

    @Multipart
    @POST("Api.php?apicall=updateaccount")
    Call<ServerResponse> updateaccount(
            @Part MultipartBody.Part file,
            @Part MultipartBody.Part file2,
            @Part("id") RequestBody username);

    @Multipart
    @POST("Api.php?apicall=uploadgroup")
    Call<ServerResponse> uploadgroup(
            @Part("groupname") RequestBody groupname,
            @Part("groupdescription") RequestBody groupdescription,
            @Part("admin") RequestBody admin,
            @Part MultipartBody.Part file2);

    @Multipart
    @POST("Api.php?apicall=updategroup")
    Call<ServerResponse> updategroup(
            @Part("groupnamex") RequestBody groupnameedit,
            @Part("groupname") RequestBody groupname,
            @Part("groupdescription") RequestBody groupdescription,
            @Part MultipartBody.Part file2,
            @Part("state") RequestBody state);

    @Multipart
    @POST("Api.php?apicall=updategroup")
    Call<ServerResponse> updategroupNI(
            @Part("groupnamex") RequestBody groupnameedit,
            @Part("groupname") RequestBody groupname,
            @Part("groupdescription") RequestBody groupdescription,
            @Part("state") RequestBody state);

    @Multipart
    @POST("Api.php?apicall=newsupload")
    Call<ServerResponse> updatenews(
            @Part("audience") RequestBody audience,
            @Part("subject") RequestBody subject,
            @Part("description") RequestBody description,
            @Part("username") RequestBody username,
            @Part("name") RequestBody name,
            @Part("time") RequestBody time,
            @Part MultipartBody.Part file );

    @Multipart
    @POST("Api.php?apicall=uploadqualification")
    Call<ServerResponse> uploadqalification(
            @Part("id") RequestBody id,
            @Part MultipartBody.Part file
    );

    @Multipart
    @POST("Api.php?apicall=setadmincareplans")
    Call<ServerResponse> addCarePlans(
            @Part("header") RequestBody rheader,
            @Part("topic") RequestBody rtopics,
            @Part("notes") RequestBody rnotes,
            @Part MultipartBody.Part file,
            @Part("subheader") RequestBody sheader,
            @Part("orderCP") RequestBody order
    );

    @Multipart
    @POST("Api.php?apicall=setcareplans")
    Call<ServerResponse> setcareplans(
            @Part("lecturerid") RequestBody lecturerid,
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file,
            @Part("topic") RequestBody filetopic,
            @Part("courseid") RequestBody courseid);
//test
    @Multipart
    @POST("Api.php?apicall=setResearchUpload")
    Call<ServerResponse> setResearchUpload(
            @Part("lecturerid") RequestBody lecturerid,
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file,
            @Part("topic") RequestBody filetopic);

    @Multipart
    @POST("Api.php?apicall=updatecourseimg")
    Call<ServerResponse> updatecourseimg(
            @Part("courseid") RequestBody lecturerid,
            @Part MultipartBody.Part file);

    @Multipart
    @POST("Api.php?apicall=updatecmebers")
    Call<ServerResponse> updatecmebers(
            @Part("name") RequestBody name,
            @Part MultipartBody.Part file);
//


    @Multipart
    @POST("Api.php?apicall=UpdateCarePlans")
    Call<ServerResponse> UpdateCarePlans(
            @Part("id") RequestBody rid,
            @Part("notes") RequestBody rnotes,
            @Part MultipartBody.Part file,
            @Part("imgdelete") RequestBody imgdelete);

    @Multipart
    @POST("Api.php?apicall=sendimg")
    Call<ServerResponse> sendimg(
            @Part("username") RequestBody username,
            @Part MultipartBody.Part file);

    @Multipart
    @POST("Api.php?apicall=addGeneralResearch")
    Call<ServerResponse> addGeneralResearch(
            @Part("header") RequestBody getheader,
            @Part("subheading") RequestBody getsub,
            @Part("notes") RequestBody getnotes,
            @Part MultipartBody.Part file);

    @Multipart
    @POST("Api.php?apicall=update_Student_courseimg")
    Call<ServerResponse> update_Student_courseimg(
            @Part("name") RequestBody id,
            @Part MultipartBody.Part file);

//    updatecourseimg
}