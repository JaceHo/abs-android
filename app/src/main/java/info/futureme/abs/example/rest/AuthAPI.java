package info.futureme.abs.example.rest;

import info.futureme.abs.entity.AccessToken;
import info.futureme.abs.entity.Result;
import info.futureme.abs.example.entity.RefreshTokenRequest;
import info.futureme.abs.example.entity.TokenRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * 3.1 安全认证服务
 */

public interface AuthAPI {

    /**
     * 3.1.1 登录认证
     */
    @POST("oauth2/token")
    Observable<Result<AccessToken>> getAccessToken(@Body TokenRequest authRequest);

    /**
     * 3.1.2 更新Token
     */
    @POST("oauth2/retoken")
    Call<Result<AccessToken>> refreshToken(@Body RefreshTokenRequest refreshTokenRequest);

    /**
     * 3.1.3 登出
     */
    @POST("oauth2/logout")
    Observable<Result> logout();


    /**
     * 获取验证码
     */
    @GET("engineers/{loginname}/securitycode")
    Observable<Result> getSecurityCode(@Path("loginname") String phoneNum);//手机号码

}
