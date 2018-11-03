package com.tannotour.smartscaff.test

import com.tannotour.scaffanno.anno.RepositoryProvider
import com.tannotour.scafflib.Repository
import com.tannotour.scafflib.network.filter
import com.tannotour.scafflib.network.request
import com.tannotour.scafflib.update
import kotlinx.coroutines.experimental.delay
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by mitnick on 2018/10/8.
 * Description
 */
@RepositoryProvider(kClass = User::class)
class UserRepository: Repository<User>(User::class, hashMapOf("size" to "10")) {

    override suspend fun remote(params: HashMap<String, String>): User? {
        delay(3000)
        request(Service::class){
            fetchUser(params["phone"], params["password"])
        } filter {
            isSuccessful
        } set {
            repositoryData?.data?.phone = data.phone
            repositoryData?.data?.userName = data.userName
            repositoryData?.msg = msg
        }
        return repositoryData
    }

    interface Service{
        @POST("v1/user/login")
        @FormUrlEncoded
        fun fetchUser(
                @Field("phone") phone: String?,
                @Field("password") password: String?
        ): Call<User>
    }

    val i = 9

    suspend fun test(params: Map<String, String>){
        delay(3000)
        User::class update {
            this?.msg = "我是在UserRepository.test()中修改的消息，传入的参数是$params"
        }
    }
}