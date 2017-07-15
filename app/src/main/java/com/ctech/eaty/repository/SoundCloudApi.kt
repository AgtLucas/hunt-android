package com.ctech.eaty.repository

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SoundCloudApi {

    @GET("/playlists/{id}")
    fun getTracks(@Path("id") key: String, @Query("client_id") clientId: String): Observable<ResponseBody>
}