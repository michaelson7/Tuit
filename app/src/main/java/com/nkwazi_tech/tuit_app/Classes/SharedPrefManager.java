package com.nkwazi_tech.tuit_app.Classes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.nkwazi_tech.tuit_app.Activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SharedPrefManager {
    //the constants
    private static final String SHARED_PREF_NAME = "simplifiedcodingsharedpref";
    private static final String KEY_EMAIL = "keyemail";

    private static final String KEY_ID = "keyid";
    private static final String KEY_phone = "phone";
    private static final String KEY_adddress = "address";
    private static final String name = "name";
    private static final String dob = "dob";
    private static final String propic = "propic";
    private static final String coverpic = "coverpic";
    private static final String accounttype = "accounttype";
    private static final String code = "code";
    private static final String verified = "verified";
    private static final String courseid = "courseid";
    private static final String courseid1 = "courseid1";
    private static final String courseid2 = "courseid2";
    private static final String courseid3 = "courseid3";
    private static final String coursename1 = "coursename1";
    private static final String coursename2 = "coursename2";
    private static final String coursename3 = "coursename3";
    private static final String studentcourse = "studentcourse";
    private static final String learningmod = "learningmod";
    private static final String schoolname = "schoolname";
    private static final String practicenumber = "practicenumber";
    private static final String lecturcourse = "lecturcourse";
    private static final String theme = "theme";
    private static final String datasaving = "datasaving";
    private static final String notifications = "notifications";
    private static final String schoolId = "schoolId";

    private static final String dialog_sub = "dialog_sub";
    private static final String dialog_vid = "dialog_vid";

    private static SharedPrefManager mInstance;
    private static Context mCtx;


    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //method to let the dataHandlerUser login
    //this method will store the dataHandlerUser data in shared preferences
    public void userLogin(DataHandler_User dataHandlerUser) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, dataHandlerUser.getId());
        editor.putString(KEY_EMAIL, dataHandlerUser.getEmail());
        editor.putString(KEY_phone, dataHandlerUser.getPhone());
        editor.putString(name, dataHandlerUser.getName());
        editor.putString(propic, dataHandlerUser.getProfilepicture());
        editor.putString(coverpic, dataHandlerUser.getCoverpicture());
        editor.putString(accounttype, dataHandlerUser.getAccounttype());
        editor.putInt(courseid, dataHandlerUser.getCourseid());
        editor.putString(practicenumber, dataHandlerUser.getPractisingnumber());
        editor.putString(lecturcourse, dataHandlerUser.getLecturercourse());
        editor.putInt(schoolId, dataHandlerUser.getSchoolId());
        editor.apply();
    }

    public int getSchoolId() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(schoolId, 0);
    }

    public Boolean getDialog_sub() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(dialog_sub, false);
    }

    public Boolean getDialog_vid() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(dialog_vid, false);
    }

    public void setDialog_sub(Boolean State) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(dialog_sub, State);
        editor.apply();
    }

    public void setDialog_vid(Boolean State) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(dialog_vid, State);
        editor.apply();
    }


    public void setCourseid(int State) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(courseid1, State);
        editor.apply();
    }

    public int getCourseid() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(courseid1, 0);
    }



    public void setCourseid2(int State) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(courseid2, State);
        editor.apply();
    }

    public void setCourseid3(int State) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(courseid3, State);
        editor.apply();
    }

    public void setCoursename1(String State) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(coursename1, State);
        editor.apply();
    }

    public String getCoursename1() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(coursename1, null);
    }

    public void setCoursename2(String State) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(coursename2, State);
        editor.apply();
    }

    public String getCoursename2() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(coursename2, null);
    }

    public void setCoursename3(String State) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(coursename3, State);
        editor.apply();
    }

    public String getCoursename3() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(coursename3, null);
    }

    public String getPractisenumber() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(practicenumber, null);
    }

    public void setPractisenumber(String State) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(practicenumber, State);
        editor.apply();
    }

    public void setLecturcourse(String State) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(lecturcourse, State);
        editor.apply();
    }

    public String getLecturcourse() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(lecturcourse, null);
    }

    public String getStudentcourse() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(studentcourse, null);
    }

    public String getLearningmod() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(learningmod, null);
    }

    public String getSchoolname() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(schoolname, null);
    }

    //this method will fetch the device token from shared preferences
    public int getID() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_ID, 0);
    }

    public String getCode() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(code, null);
    }

    public void setVerified(String State) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(verified, State);
        editor.apply();
    }

    public void setProImg(String State) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(propic, State);
        editor.apply();
    }

    public void setCoverimg(String State) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(coverpic, State);
        editor.apply();
    }


    //this method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL, null) != null;
    }

    //this method will give the logged in user

    public String getDob() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(dob, null);

    }

    public String getPropic() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(propic, null);

    }

    public String getCoverpic() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(coverpic, null);

    }

    public String getAccounttype() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(accounttype, null);

    }

    public String getName() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(name, null);

    }

    public String getPhone() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_phone, null);

    }

    public String getUsername() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL, null);

    }

    public String getAddress() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_adddress, null);

    }

    public String getEmail() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL, null);

    }

    public void setTheme(String State) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(theme, State);
        editor.apply();
    }

    public String getTheme() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(theme, null);

    }

    public void setdatasaving(String State) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(datasaving, State);
        editor.apply();
    }

    public String getdatasaving() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(datasaving, null);

    }

    public String getnotifications() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(notifications, null);

    }

    //this method will logout the user
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(mCtx, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mCtx.startActivity(intent);
    }
}
