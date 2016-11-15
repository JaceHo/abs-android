package info.futureme.abs.example.rest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import info.futureme.abs.entity.Result;
import info.futureme.abs.example.entity.ProjectRequest;
import info.futureme.abs.example.entity.g.Client;
import info.futureme.abs.example.entity.g.Project;
import info.futureme.abs.example.entity.g.SysDict;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;


/**
 * Created by hippo on 11/12/15.
 * 3.5	基础数据服务
 */
public interface DataAPI {

    //3.5.1	获取客户列表
    @POST("clients")
    Call<Result<ArrayList<Client>>> clientList();

    //2.3.23.5.2	获取客户详细信息
    @GET("clients/{clientid}")
    Observable<Result<Client>> clientDetail(@Path("clientid") String clientid);


    //2.3.33.5.3	获取项目列表
    @POST("projects")
    Call<Result<ArrayList<Project>>> projectList(@Body ProjectRequest projectRequest);


    @POST("clients/{clientid}")
    @FormUrlEncoded
    Call<Result<ArrayList<SysDict>>> dict(@Field("dictType") int type);

    @POST("{end}")
    Observable<ResponseBody> dynamicPost(@Path(value = "end", encoded = true) String end, @Body RequestBody body);

    @PUT("{end}")
    Observable<ResponseBody> dynamicPut(@Path(value = "end", encoded = true) String end, @Body RequestBody body);

    @GET("{end}")
    Observable<ResponseBody> dynamicGet(@Path(value = "end", encoded = true) String path, @QueryMap Map<String, String> queryMap);

    @POST("api/user/userModify")
    @FormUrlEncoded
    Observable<Result<LinkedHashMap<String,String>>> avatarPost(@FieldMap Map<String,String> params);

}
