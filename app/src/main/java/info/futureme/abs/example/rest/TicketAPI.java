package info.futureme.abs.example.rest;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import info.futureme.abs.entity.Result;
import info.futureme.abs.example.entity.BillNumber;
import info.futureme.abs.example.entity.Ticket;
import info.futureme.abs.example.entity.TicketRequest;
import info.futureme.abs.example.entity.TicketUpdateRequest;
import info.futureme.abs.example.entity.g.Project;
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
 * 2.23.3	订单服务
 */
public interface TicketAPI {
    //订单列表
    @POST("tickets")
    Observable<Result<ArrayList<Ticket>>> tickets(@Body TicketRequest request);

    //ticket detail
    @GET("tickets/{ticketid}")
    Observable<Result<Ticket>> ticketDetail(@Path("ticketid") String ticketId);

    //获取工程师订单数量
    @GET("tickets/total")
    Observable<Result<BillNumber>> ticketsLeft();

    //update ticket info
    @PUT("tickets/{ticketid}/{action}")
    Observable<Result<LinkedHashMap<String, String>>> updateTicket(@Path("ticketid") String ticketid, @Path("action") String action, @Body TicketUpdateRequest ticketUpdateRequest);

    @POST("api/repairBill/projectList")
    Call<Result<ArrayList<Project>>> projectList();

    @POST("api/repairBill/savePageInfo")
    @FormUrlEncoded
    Observable<Result> savePageInfo(@Field("billId") String billId, @Field("stepConfigId") String stepConfigId, @Field("status") String status);

}
