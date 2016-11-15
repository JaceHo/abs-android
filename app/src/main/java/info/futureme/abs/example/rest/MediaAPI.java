package info.futureme.abs.example.rest;


import info.futureme.abs.entity.Result;
import info.futureme.abs.entity.UpdateResponse;
import info.futureme.abs.example.entity.Attachs;
import info.futureme.abs.example.entity.UpdateRequest;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import rx.Observable;

/**
 * Created by hippo on 9/29/15.
 */
public interface MediaAPI {
    /**
     * upload file
     *
     * @return result
     */
    @POST("attUpload")
    @Multipart
    Call<Result> uploadAttachmentAsync(@Part("file\"; filename=\"file.bin\" ") RequestBody file, @Part("id") RequestBody lazyId, @Part("type") RequestBody type);

    @POST("flow/attachment/{ticketid}")
    Observable<Result<Attachs>> createAttachmentRecord(@Path("ticketid") String ticketid, @Body RequestBody createAttachmentRecordRequest);

//    @POST("flow/attachment")
//    Observable<Result<ArrayList<AttachInfos>>> createAttachmentRecord(@Path("ticketid") String ticketid, @Body ArrayList<AttachInfos> attachs);

    @GET("{end}")
    @Headers({"Content-Type:*/*"})
    @Streaming
    Call<ResponseBody> getFile(@Path("end") String end);


    @POST("app/update")
    Call<Result<UpdateResponse>> getUpdateInfo(@Body UpdateRequest updateRequest);
}
