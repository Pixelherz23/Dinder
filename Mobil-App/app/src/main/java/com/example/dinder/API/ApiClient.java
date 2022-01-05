package com.example.dinder.API;

import com.example.dinder.Datamodel.Benutzer;
import com.example.dinder.Datamodel.LoginResponse;
import com.example.dinder.Datamodel.Profildaten;
import com.example.dinder.Datamodel.StartElemente;
import com.example.dinder.Datamodel.TierDaten;
import com.example.dinder.Datamodel.User;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiClient {
    @FormUrlEncoded
    @POST("account/new")
    Call<Void> createPost(@Field("firstname") String firstname,
                          @Field("lastname") String lastname,
                          @Field("email") String email,
                          @Field("passwort") String passwort,
                          @Field("birthday") String birthday,
                          @Field("institution") String institution);
    @GET("login")
    Call<LoginResponse> login(@Header("Authorization") String authHeader);


    @POST("profil/create")
    Call<Void> createProfil(@Query("token") String token,@Body JsonObject body);


    @POST("profil/uploadImage")
    Call<Void> sendImg(@Query("token") String token,@Query("profilName")String profil,  @Body RequestBody requestBody);

    @GET("mostAttractive")
    Call<ArrayList<StartElemente>> getMost();


    @GET("profil/getPicFromOtherUser")
    Call<ResponseBody> getPic(@Query("token") String token,@Query("profilName") String profil, @Query("mail") String mail);

    @GET("profil/getProfilFromOtherUser")
    Call<Profildaten> getProfil(@Query("token") String token, @Query("profilName") String profil, @Query("mail") String mail);

    @GET("profil/getProfilsMobile")
    Call<ArrayList<TierDaten>> getTiere(@Query("token") String token);

    @GET("profil/getPic")
    Call<ResponseBody> getPicProfil(@Query("token") String token, @Query("profilName") String profil);

    @POST("setPositive")
    Call<Void> like(@Query("token") String token, @Query("profilName") String profil, @Query("mail") String mail);

    @POST("setNegative")
    Call<Void> dislike(@Query("token") String token, @Query("profilName") String profil, @Query("mail") String mail);

    @GET("searchProfilsByGPSMobile")
    Call<ArrayList<TierDaten>> search(@Query("token") String token, @Query("lat") String lat, @Query("lg") String lg, @Query("distance") String distance);

    @POST("profil/rememberList/setEntry")
    Call<Void> addToBookmark(@Query("token") String token, @Query("profilName") String profil, @Query("profilToSave") String profilToSave, @Query("mailToSave") String mailToSave);

    @GET("profil/rememberList/getEntriesMobile")
    Call<ArrayList<Benutzer>> getBenutzer(@Query("token") String token, @Query("profilName") String profil);

    @POST("profil/friendlist/addFriendRequest")
    Call<Void> addFriend(@Query("token") String token, @Query("requestProfilName") String selectedProfil, @Query("reciverProfilName") String profilToAdd, @Query("reciverEmail") String mail);


    @GET("profil/friendlist/getFriendRequestMobile")
    Call<ArrayList<User>> getRequest(@Query("token") String token, @Query("profilName") String profil);

    @GET("profil/friendlist/getFriendsMobile")
    Call<ArrayList<User>> getFriends (@Query("token") String token , @Query("profilName") String profil);

    @POST("profil/rememberList/deleteEntry")
    Call<Void> removeFromBookmark(@Query("token") String token, @Query("profilName") String profil, @Query("profilToDelete") String profilToDelete, @Query("mailToDelete") String mailToDelete);

    @POST("profil/friendlist/acceptFriendRequest")
    Call<Void> accecptFriend(@Query("token") String token, @Query("profilName") String profil , @Query("requestFromProfil") String requestProfil, @Query("requestFromUser") String mail);

    @POST("profil/friendlist/deleteFriendshipOrRequest")
    Call<Void> deleteFriendorRequest(@Query("token") String token, @Query("profilName") String profil, @Query("notFriendProfil") String noFriend, @Query("notFriendUser") String nomail);
}
