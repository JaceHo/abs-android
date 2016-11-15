package info.futureme.abs.example.rest;


import info.futureme.abs.entity.Result;
import info.futureme.abs.example.entity.Engineer;
import info.futureme.abs.example.entity.PositionRequest;
import info.futureme.abs.example.entity.ResetPassRequest;
import info.futureme.abs.example.entity.StatusRequest;
import info.futureme.abs.example.entity.UpdateEngineerRequest;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * 3.2	工程师服务
 */

public interface UserAPI {
    /**
     * 3.2.1 获取工程师基本信息
     *
     * @return engineer
     */
    @GET("engineers/{engineerid}")
    Call<Result<Engineer>> engineerInfo(@Path("engineerid") String engineerid);

    /**
     * 3.2.2 设置工程师状态
     */
    @PUT("engineers/{engineerid}/status")
    Observable<Result> updateEngineerStatus(@Body StatusRequest statusRequest);

    /**
     * 匹配验证码
     */
    @POST("engineers/{loginname}/securitycode")
    Observable<Result> checkSecurityCode(@Path("loginname") String loginname, @Body RequestBody code);

    @PUT("engineers/password")
    Observable<Result> resetPassword(@Body ResetPassRequest resetPassRequest);
    //验证码

    /**
     * update engineer account information
     *
     * @return common result
     */
    @PUT("engineers/{engineerid}")
    Observable<Result> updateEngineerInfo(@Path("engineerid") String engineerid, @Body UpdateEngineerRequest updateEngineerRequest);

    /**
     * update engineer postion
     */
    @POST("engineers/{engineerid}/position")
    Observable<Result> updateEngineerPostion(@Path("engineerid") String engineerid, @Body PositionRequest positionRequest);

    //TODO
    @POST("api/user/appConfig")
    @FormUrlEncoded
    Call<Result<String>> appConfig(@Field("appConfigJson") String config);
}
