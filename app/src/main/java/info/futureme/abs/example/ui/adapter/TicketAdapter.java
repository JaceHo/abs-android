package info.futureme.abs.example.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import info.futureme.abs.FApplication;
import info.futureme.abs.base.FBaseActivity;
import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.conf.FConstants;
import info.futureme.abs.entity.Result;
import info.futureme.abs.example.ABSApplication;
import info.futureme.abs.example.R;
import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.example.conf.TicketAction;
import info.futureme.abs.example.entity.Ticket;
import info.futureme.abs.example.entity.TicketStatus;
import info.futureme.abs.example.entity.TicketUpdateRequest;
import info.futureme.abs.example.entity.g.Notification;
import info.futureme.abs.example.entity.g.NotificationDao;
import info.futureme.abs.example.rest.ServiceGenerator;
import info.futureme.abs.example.rest.TicketAPI;
import info.futureme.abs.example.ui.WebActivity;
import info.futureme.abs.example.ui.fragment.TicketListFragment;
import info.futureme.abs.example.util.ActionCallbacks;
import info.futureme.abs.example.util.Utils;
import info.futureme.abs.example.widget.swipe.SwipeLayout;
import info.futureme.abs.example.widget.swipe.adapters.RecyclerSwipeAdapter;
import info.futureme.abs.rest.NetworkObserver;
import info.futureme.abs.service.LocationService;
import info.futureme.abs.util.DLog;
import info.futureme.abs.util.FPreferenceManager;
import info.futureme.abs.util.WindowUtils;
import info.futureme.abs.util.maputil.MapDistance;
import info.futureme.abs.view.FXRecyclerView;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class TicketAdapter extends RecyclerSwipeAdapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_HEADER = 0x01;
    public static final int VIEW_TYPE_CONTENT = 0x00;

    static NotificationDao dao;
    private static final int LINEAR = 0;
    private static SimpleDateFormat hourMinFormater;
    private static LatLng latLng;
    private final Scheduler.Worker worker;
    private final RxFragment fragment;
    CopyOnWriteArrayList<Ticket> ticketList = new CopyOnWriteArrayList<>();
    CopyOnWriteArrayList<Ticket> ticketShadowList;
    OnTicketSelectedListener listener;
    String type;//Fragment 类型匹配不同状态


    public TicketAdapter(String type, Scheduler.Worker worker, RxFragment fragment) {
        this.type = type;
        this.worker = worker;
        this.fragment = fragment;
    }

    public void setStatus(String status) {
        this.type = status;
    }


    public static void onBindTicket(final TicketViewHolder holder, final Ticket ticket, final String type, final OnTicketSelectedListener listener, final Scheduler.Worker worker, final TicketAdapter adapter, final RxFragment fragment) {
        Context context = ContextManager.context();

        if(holder == null) return;
        if (holder.ticketDesc != null) {
            holder.ticketDesc.setText("故障分类: " + ticket.getIssue());
        }
        if (holder.ticketId != null) {
            holder.ticketId.setText("订单编号: " + ticket.getTicketid());
        }

        Boolean immediate = false;
        if (ticket.getSla() != null && ticket.getSla().getAlarmtime() != null
                && ticket.getSla().getOvertime() != null) {
            if (ticket.getSla().getAlarmtime().getTime() < System.currentTimeMillis()) {
                immediate = true;
            }
            if (ticket.getSla().getOvertime().getTime() < System.currentTimeMillis()) {
                immediate = null;
            }
        }

        if (holder.resLevel != null) {
            if (ticket.getResplevel() != null) {
                holder.resLevel.setVisibility(View.VISIBLE);
                holder.resLevel.setText(ticket.getResplevel());
            } else {
                holder.resLevel.setVisibility(View.GONE);
            }
        }

        if (holder.vipText != null) {
            if (ticket.getCustomer() == null || ticket.getCustomer().getLevel() == null) {
                holder.vipText.setVisibility(View.GONE);
            } else {
                if (ticket.getCustomer().getLevel().contains("VIP")) {
                    holder.vipText.setVisibility(View.VISIBLE);
                    holder.vipText.setText("VIP");
                    holder.vipText.setBackgroundResource(R.drawable.shape_rectagle_vip_red);
                    if (holder.resLevel != null && ticket.getResplevel() != null) {
                        holder.vipText.setBackgroundResource(R.drawable.shape_rectagle_vip_red_combine);
                        holder.resLevel.setTextColor(fragment.getResources().getColor(R.color.red_primary));
                        holder.resLevel.setBackgroundResource(R.drawable.res_level_red_shape_combine);
                    }
                } else {
                    holder.vipText.setVisibility(View.GONE);
                    if (holder.resLevel != null) {
                        holder.resLevel.setTextColor(fragment.getResources().getColor(R.color.item_text_gray));
                        holder.resLevel.setBackgroundResource(R.drawable.res_level_gray_shape);
                    }
                }
            }
        }

        if (holder.cardView != null) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        if(holder.swipeLayout != null && !holder.swipeLayout.isClick()){
                            return;
                        }
                        listener.onTicketSelected(ticket, v.getContext());
                    }
                }
            });
        }
        final Integer[] nameLeft = {0, 0, 0, 0, 0};

        if (holder.distance != null) {
            if (latLng != null && ticket.getCustomer() != null && ticket.getCustomer().getLatitude() != 0 && ticket.getCustomer().getLongitude() != 0 && latLng.latitude !=0 && latLng.longitude != 0) {
                holder.distance.setText(MapDistance.getDistanceKM(latLng, new LatLng(ticket.getCustomer().getLatitude(), ticket.getCustomer().getLongitude())) + "KM");
            } else {
                holder.distance.setText("-.- KM");
            }
        }

        if (MVSConstants.FragmentType.FRAGMENT_DONE_HISTORYORDERS.equals(type)
                        || MVSConstants.FragmentType.FRAGMENT_CLOSE_HISTORYORDERS.equals(type)
                        || (MVSConstants.FragmentType.FRAGMENT_DONE_HISTORYORDERS
                        + "," + MVSConstants.FragmentType.FRAGMENT_CLOSE_HISTORYORDERS).equals(type)
                ) {
            if(holder.catalogIcon != null){
                if(ticket.getStatus().getState() == 0){
                    holder.catalogIcon.setImageResource(R.drawable.grab_catelog);
                }else{
                    holder.catalogIcon.setImageResource(R.drawable.receive_catelog);
                }
            }
            if (ticket.getScore() != null && ticket.getScore() > 0) {
                if (holder.ratingGrade != null && holder.ratingBar != null) {
                    holder.ratingBar.setVisibility(View.VISIBLE);
                    holder.ratingGrade.setVisibility(View.VISIBLE);

                    holder.ratingGrade.setText(ticket.getScore() + "分");
                    holder.ratingBar.setRating((float) ticket.getScore().doubleValue());
                }
                if (holder.noScore != null) {
                    holder.noScore.setVisibility(View.GONE);
                }
            } else {
                if (holder.noScore != null) {
                    holder.noScore.setVisibility(View.VISIBLE);
                }
                if (holder.ratingGrade != null && holder.ratingBar != null) {
                    holder.ratingBar.setVisibility(View.GONE);
                    holder.ratingGrade.setVisibility(View.GONE);
                }
            }
        } else if (type.equals(MVSConstants.FragmentType.FRAGMENT_ACTIVORDERS + "," + MVSConstants.FragmentType.FRAGMENT_FINISH_ORDERS)) {
            //todo ticketid have unread message?
            //NotificationDao dao = MVSApplication.getDaoSession().getNotificationDao();
            if (holder.speaker != null) {
                if(dao == null) {
                    dao = ABSApplication.getDaoSession().getNotificationDao();
                }

                String account = FPreferenceManager.getString(MVSConstants.ACCOUNT_SIGNED, "");
                List<Notification> list = dao.queryBuilder().where(
                        NotificationDao.Properties.Account.eq(account),
                        NotificationDao.Properties.Readed.eq(false)
                        ,NotificationDao.Properties.Ticketid.eq(ticket.getTicketid())
                ).list();
                if(list == null || list.size() == 0){
                    holder.speaker.setVisibility(View.GONE);
                }else {
                    holder.speaker.setVisibility(View.VISIBLE);
                }
            }
        }

        if (holder.shopName != null && holder.sumary != null) {
            holder.shopName.setText(ticket.getCustomer().getName());

//            if(ticket.getStatus().getState() <= 1){
                holder.shopName.setVisibility(View.GONE);
//            }
            if(holder.shopName.getVisibility() == View.GONE)
            RxView.preDraws(holder.sumary,  new Func0<Boolean>() {
                @Override
                public Boolean call() {
                    return holder.shopName.getVisibility() == View.GONE;
                }
            }).first()
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            if (holder.vipText != null) {
                                nameLeft[0] = holder.vipText.getMeasuredWidth();
                            }
                            if (holder.resLevel != null) {
                                nameLeft[1] = holder.resLevel.getMeasuredWidth();
                            }
                            if (holder.distance != null) {
                                holder.distance.setVisibility(View.VISIBLE);
                                nameLeft[2] = holder.distance.getMeasuredWidth();
                            }
                            if (holder.itemView.findViewById(R.id.location_icon_imageview) != null) {
                                holder.itemView.findViewById(R.id.location_icon_imageview).setVisibility(View.VISIBLE);
                                nameLeft[3] = holder.itemView.findViewById(R.id.location_icon_imageview).getMeasuredWidth();
                            }
                            if(holder.speaker != null){
                                nameLeft[4] = holder.speaker.getMeasuredWidth();
                            }
                            int width = 0;
                            if (holder.shopName != null && ticket.getCustomer() != null) {
                                width = holder.sumary.getMeasuredWidth() - nameLeft[0] - nameLeft[1] - nameLeft[2] - nameLeft[3] - nameLeft[4] - WindowUtils.dp2px(10);
                                holder.shopName.setMaxWidth(width);
                            }
                            DLog.i("width", Arrays.deepToString(nameLeft) + " width:" + width);
                            holder.shopName.setVisibility(View.VISIBLE);
                            holder.sumary.requestLayout();
                        }
                    });
        }


        switch (type) {
            case MVSConstants.FragmentType.FRAGMENT_GRABORDERS + "," + MVSConstants.FragmentType.FRAGMENT_RECEIVEORDERS:
                if(holder.ticketId != null){
                    holder.ticketId.setText(ticket.getTicketid());
                }
                if (holder.expectedText != null && ticket.getSla() != null && ticket.getSla().getExpecttime() != null) {
                    holder.expectedText.setText(new SimpleDateFormat(MVSConstants.DATE_FORMAT_NORMAL).format(ticket.getSla().getExpecttime()));
                }
                if (holder.descText != null && ticket.getIssue() != null) {
                    holder.descText.setText(ticket.getIssue());
                }

                if (holder.noteText != null && ticket.getNotes() != null) {
                    holder.noteText.setText(ticket.getNotes());
                }

                //grab ticket
                if (ticket.getStatus() != null && ticket.getStatus().getState() == 0) {
                    final Date[] dates = ticket.getTimers();
                    long curr = System.currentTimeMillis();
                    if (dates != null && dates.length > 0) {
                        Date last = null;
                        for (Date date : dates) {
                            if (curr < date.getTime()) {
                                last = date;
                                break;
                            }
                        }
                        if (last == null) {
                            //说明时间值超过，删掉这个item吧
//                            if(fragment.getView() != null)
//                            new Handler(Looper.getMainLooper())
//                                    .postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            RxBus rxBus = FApplication.getRxBus();
//                                            rxBus.send(TicketListFragment.REFRESH_LSIT_RECEIVE);
//                                        }
//                                    }, 1000);
                            Drawable drawable = ContextManager.context().getResources().getDrawable(R.drawable.grab_countdown);
/// 这一步必须要做,否则不会显示.
                            drawable.setBounds(0, 0, 50, 50);
                            if (holder.leftAction != null) {
                                holder.leftAction.setCompoundDrawables(drawable, null, null, null);
                                holder.leftAction.setText("已结束");
                                holder.leftAction.setTextColor(ContextManager.context().getResources().getColor(R.color.grab_color));
                            }
                        } else {
                            //刷新订单列表
                            if (last.getTime() - System.currentTimeMillis() < 1000) {
//                                new Handler(Looper.getMainLooper())
//                                        .postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                RxBus rxBus = FApplication.getRxBus();
//                                                rxBus.send(TicketListFragment.REFRESH_LSIT_RECEIVE);
//                                            }
//                                        }, 1000);
                                Drawable drawable = ContextManager.context().getResources().getDrawable(R.drawable.grab_countdown);
/// 这一步必须要做,否则不会显示.
                                drawable.setBounds(0, 0, 50, 50);
                                if (holder.leftAction != null) {
                                    holder.leftAction.setCompoundDrawables(drawable, null, null, null);
                                    holder.leftAction.setText("已结束");
                                    holder.leftAction.setTextColor(ContextManager.context().getResources().getColor(R.color.grab_color));
                                }
                            }else {
                                Date remain = new Date(last.getTime() - System.currentTimeMillis());
                                hourMinFormater = new SimpleDateFormat("mm分ss秒");
                                if (holder.leftAction != null) {
                                    Drawable drawable = ContextManager.context().getResources().getDrawable(R.drawable.grab_countdown);
/// 这一步必须要做,否则不会显示.
                                    //drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                    drawable.setBounds(0, 0, 50, 50);
                                    holder.leftAction.setCompoundDrawables(drawable, null, null, null);
                                    holder.leftAction.setTextColor(ContextManager.context().getResources().getColor(R.color.grab_color_time));
                                    if (remain.getTime() >= 0) {
                                        holder.leftAction.setText(hourMinFormater.format(remain));
                                    } else {
                                        holder.leftAction.setText("已结束");
                                    }
                                }
                            }
                            if (holder.rightAction != null) {
                                holder.rightAction.setText(R.string.graborder);
                                Drawable drawable = ContextManager.context().getResources().getDrawable(R.drawable.grab_icon);
/// 这一步必须要做,否则不会显示.
                                //drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                drawable.setBounds(0, 0, 50, 50);
                                holder.rightAction.setCompoundDrawables(drawable, null, null, null);
                                //holder.rightAction.setCompoundDrawablePadding(4);
                                holder.rightAction.setTextColor(ContextManager.context().getResources().getColor(R.color.grab_color));
                                holder.rightAction.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        ActionCallbacks.uploadInfoAsync(fragment.getContext(), new Runnable() {
                                            @Override
                                            public void run() {
                                                BDLocation location = LocationService.getRealTimeLatLngTimeless();
                                                TicketUpdateRequest ticketUpdateRequest = new TicketUpdateRequest();
                                                if (location != null) {
                                                    LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<String, String>();
                                                    linkedHashMap.put("longitude", "" + location.getLongitude());
                                                    linkedHashMap.put("latitude", "" + location.getLatitude());
                                                    linkedHashMap.put("address", "" + location.getAddrStr());
                                                    linkedHashMap.put("addressname", (location.getBuildingName() == null ? location.getAddrStr() : location.getBuildingName()));
                                                    ticketUpdateRequest.setFields(linkedHashMap);
                                                }

                                                TicketAPI ticketAPI = ServiceGenerator.createService(TicketAPI.class);
                                            }
                                        });
                                    }
                                });
                            }

                        }
                    } else {
//                        //说明时间值超过，删掉这个item吧
//                        if(fragment.getView() != null)
//                            FApplication.getRxBus().send(TicketListFragment.REFRESH_LSIT_RECEIVE);
                        Drawable drawable = ContextManager.context().getResources().getDrawable(R.drawable.grab_countdown);
/// 这一步必须要做,否则不会显示.
                        drawable.setBounds(0, 0, 50, 50);
                        if (holder.leftAction != null) {
                            holder.leftAction.setCompoundDrawables(drawable, null, null, null);
                            holder.leftAction.setText("已结束");
                            holder.leftAction.setTextColor(ContextManager.context().getResources().getColor(R.color.grab_color));
                        }
                    }
                } else {
                    //receive ticket
                    if (holder.leftAction != null) {
                        holder.leftAction.setText(ContextManager.context().getString(R.string.pass_ticket));
                        holder.leftAction.setTextColor(ContextManager.context().getResources().getColor(R.color.black));
                        holder.leftAction.setCompoundDrawables(null, null, null, null);

                        holder.leftAction.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ContextManager.context(), WebActivity.class);
                                intent.putExtra(WebActivity.TICKET_ID, ticket.getTicketid());
                                intent.putExtra(WebActivity.CLIENT_NAME, ticket.getCustomer().getName());
                                intent.putExtra(FConstants.X5WEBVIEW_INITIAL_URL, String.format(MVSConstants.APIConstants.TICKET_BACK, ticket.getTicketid()));
                                fragment.startActivityForResult(intent, MVSConstants.FragmentType.FRAGMENT_RECEIVEORDERS.hashCode());
                            }
                        });
                    }
                    if (holder.rightAction != null) {
                        holder.rightAction.setText(ContextManager.context().getString(R.string.receive_tickets));
                        Drawable drawable = ContextManager.context().getResources().getDrawable(R.drawable.receive_ticket_icon);
/// 这一步必须要做,否则不会显示.
                        //drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        drawable.setBounds(0, 0, 50, 50);
                        holder.rightAction.setCompoundDrawables(drawable, null, null, null);
                        holder.rightAction.setTextColor(ContextManager.context().getResources().getColor(R.color.recieve_color));
                        holder.rightAction.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.rightAction.setClickable(false);
                                holder.itemView.setClickable(false);
                                //进入详情页面
                                ((FBaseActivity) (fragment.getActivity())).processPreview(fragment.getString(R.string.receiving), null, false);
                                DLog.i("show", "ing");
                                ActionCallbacks.uploadInfoAsync(fragment.getContext(), new Runnable() {
                                    @Override
                                    public void run() {
                                        BDLocation location = LocationService.getRealTimeLatLngTimeless();
                                        TicketUpdateRequest ticketUpdateRequest = new TicketUpdateRequest();
                                        if (location != null) {
                                            LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<String, String>();
                                            linkedHashMap.put("longitude", "" + location.getLongitude());
                                            linkedHashMap.put("latitude", "" + location.getLatitude());
                                            linkedHashMap.put("address", "" + location.getAddrStr());
                                            linkedHashMap.put("addressname", (location.getBuildingName() == null ? location.getAddrStr() : location.getBuildingName()));
                                            ticketUpdateRequest.setFields(linkedHashMap);
                                        }
                                        TicketAPI ticketAPI = ServiceGenerator.createService(TicketAPI.class);
                                        ticketAPI.updateTicket(ticket.getTicketid(), TicketAction.CONFIRM.getValue(), ticketUpdateRequest)
                                                // Started in onStart(), running until in onStop()
                                                .compose(fragment.<Result<LinkedHashMap<String, String>>>bindUntilEvent(FragmentEvent.DESTROY))
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .unsubscribeOn(Schedulers.io())
                                                .subscribe(new NetworkObserver<Result<LinkedHashMap<String, String>>>() {
                                                    @Override
                                                    public void onSuccess(Result<LinkedHashMap<String, String>> result) {
                                                        holder.rightAction.setClickable(true);
                                                        holder.itemView.setClickable(true);
                                                        if (result.getEcode() != 0) {
                                                            onFailure(result.getReason());
                                                        } else {
                                                            ((FBaseActivity) (fragment.getActivity())).processDismiss(true, fragment.getString(R.string.receive_success));
                                                            //接单成功 上传信息
                                                            List<Ticket> tickets = adapter.getTicketList();
                                                            int postion = tickets.indexOf(ticket);
                                                            if(postion == -1){
                                                                for(int i = 0; i<tickets.size(); i++){
                                                                    if(tickets.get(i).getTicketid().equals(ticket.getTicketid())){
                                                                        postion = i;
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                            if(postion == -1 && tickets.size() > 0)
                                                                postion = 1;
                                                            Bundle b = fragment.getArguments();
                                                            b.putInt(TicketListFragment.REMOVE_POSITION, postion);
                                                            FApplication.getRxBus().send(TicketListFragment.RECEIVE_SUCCESS);
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(String tip) {
                                                        holder.rightAction.setClickable(true);
                                                        holder.itemView.setClickable(true);
                                                        ((FBaseActivity) (fragment.getActivity())).processDismiss(false, tip);
                                                    }
                                                });
                                    }
                                });
                            }
                        });
                    }
                }
                break;
            case MVSConstants.FragmentType.FRAGMENT_ACTIVORDERS + "," + MVSConstants.FragmentType.FRAGMENT_FINISH_ORDERS://活动订单

                if (holder.actionNameTip != null) {
                    holder.actionNameTip.setVisibility(View.VISIBLE);
                    if (immediate == null) {
                        holder.actionNameTip.setText(context.getString(R.string.doit_passed));
                    } else if (immediate) {
                        holder.actionNameTip.setText(context.getString(R.string.doit));
                    } else
                        holder.actionNameTip.setVisibility(View.INVISIBLE);
                }

                if (holder.actionTimeTip != null && ticket.getSla() != null && ticket.getSla().getOvertime() != null) {
                    holder.actionTimeTip.setVisibility(View.VISIBLE);
                    holder.actionTimeTip.setText("不晚于" + Utils.getSimpleDay(ticket.getSla().getOvertime().getTime()));
                }

                if (holder.actionTime != null) {
                    holder.actionTime.setVisibility(View.VISIBLE);
                    if (ticket.getSla() != null && ticket.getSla().getOvertime() != null) {
                        holder.actionTime.setText(new SimpleDateFormat(MVSConstants.DATE_FORMAT_HOUR_MIN).format(ticket.getSla().getOvertime()));
                        if (immediate == null || immediate)
                            holder.actionTime.setTextColor(ContextManager.context().getResources().getColor(R.color.ticket_hint_red));
                        else
                            holder.actionTime.setTextColor(ContextManager.context().getResources().getColor(R.color.blue_tip));
                    } else {
                        if (holder.actionNameTip != null) {
                            holder.actionNameTip.setVisibility(View.INVISIBLE);
                        }
                        if (holder.actionTimeTip != null) {
                            holder.actionTimeTip.setVisibility(View.INVISIBLE);
                        }
                        holder.actionTime.setText("--:--");
                        holder.actionTime.setTextColor(ContextManager.context().getResources().getColor(R.color.blue_tip));
                    }
                }

                final TicketStatus status = ticket.getStatus();

                if (holder.swipeLayout != null) {
                    if (status.getCurrstate() == null) {
                        holder.layoutPencil.setBackgroundResource(R.drawable.grey_circle);
                        holder.actionTime.setTextColor(fragment.getResources().getColor(R.color.item_text_gray));
                        holder.swipeLayout.setSwipeEnabled(false);
                    }else{
                        holder.swipeLayout.setSwipeEnabled(true);
                    }
                    holder.swipeLayout.setClick(true);
                }
                if (status != null && status.getCurrstate() == null) {
                    if (holder.actionName != null) {
                        holder.actionName.setText("待关单");
                    }
                    Glide.with(fragment).load(R.drawable.to_be_closed).into(holder.actionIcon);
                    holder.layoutPencil.setVisibility(View.VISIBLE);
                }

                if (holder.bottom != null) {
                    holder.shortcutAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.bottom.performClick();
                        }
                    });
                    holder.bottom.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //TODO shortcut action
                                    if (adapter != null) {
                                        List<SwipeLayout> layouts = adapter.getOpenLayouts();
                                        for (SwipeLayout swipeLayout : layouts) {
                                            swipeLayout.close(false);
                                        }
                                    }
                                }
                            });
                }

                break;
        }
    }

    private static String replaceNull(String pattern) {
        return pattern == null ? "" : pattern;
    }

    public void setLocation(Location location) {
        this.setLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    public void setOnOrderSelectedListener(OnTicketSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (fragment instanceof TicketListFragment) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                viewGroup.setBackground(null);
            } else {
                viewGroup.setBackgroundDrawable(null);
            }
            viewGroup.setBackgroundColor(viewGroup.getResources().getColor(android.R.color.transparent));
        }
        return createViewHolder(viewGroup, type, viewType);
    }

    public static RecyclerView.ViewHolder createViewHolder(ViewGroup viewGroup, String status, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view;
        if (viewType == VIEW_TYPE_HEADER) {
            if ((MVSConstants.FragmentType.FRAGMENT_ACTIVORDERS + "," + MVSConstants.FragmentType.FRAGMENT_FINISH_ORDERS).equals(status)) {
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.header_item, viewGroup, false);
            } else if (
                    MVSConstants.FragmentType.FRAGMENT_DONE_HISTORYORDERS.equals(status)
                            || MVSConstants.FragmentType.FRAGMENT_CLOSE_HISTORYORDERS.equals(status)
                            || (MVSConstants.FragmentType.FRAGMENT_DONE_HISTORYORDERS
                            + "," + MVSConstants.FragmentType.FRAGMENT_CLOSE_HISTORYORDERS).equals(status)
                    ) {
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.history_header_item, viewGroup, false);
            } else {
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.header_item_recieve, viewGroup, false);
            }
            return new HeaderViewHolder(view);
        } else {
            if ((MVSConstants.FragmentType.FRAGMENT_ACTIVORDERS + "," + MVSConstants.FragmentType.FRAGMENT_FINISH_ORDERS).equals(status)) {
                view = inflater.inflate(R.layout.active_order_item, viewGroup, false);
            } else if ((MVSConstants.FragmentType.FRAGMENT_GRABORDERS + "," + MVSConstants.FragmentType.FRAGMENT_RECEIVEORDERS).equals(status)) {
                view = inflater.inflate(R.layout.receive_order_item, viewGroup, false);
            } else if (
                    MVSConstants.FragmentType.FRAGMENT_DONE_HISTORYORDERS.equals(status)
                            || MVSConstants.FragmentType.FRAGMENT_CLOSE_HISTORYORDERS.equals(status)
                            || (MVSConstants.FragmentType.FRAGMENT_DONE_HISTORYORDERS
                            + "," + MVSConstants.FragmentType.FRAGMENT_CLOSE_HISTORYORDERS).equals(status)
                    ) {
                view = inflater.inflate(R.layout.history_ticket, viewGroup, false);
                return new TicketViewHolder(view);
            } else {
                view = inflater.inflate(R.layout.order_item, viewGroup, false);
            }
            return new OrderContentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Ticket ticket = ticketList.get(position);

        if(MVSConstants.FragmentType.FRAGMENT_DONE_HISTORYORDERS.equals(type)
                || MVSConstants.FragmentType.FRAGMENT_CLOSE_HISTORYORDERS.equals(type)
                || (MVSConstants.FragmentType.FRAGMENT_DONE_HISTORYORDERS
                + "," + MVSConstants.FragmentType.FRAGMENT_CLOSE_HISTORYORDERS).equals(type)){
//            /** Embed section configuration. **/
//            final GridSLM.LayoutParams params = (GridSLM.LayoutParams) holder.itemView.getLayoutParams();
//
//            params.setSlm(LinearSLM.ID);
//
//            // Position of the first item in the section. This doesn't have to
//            // be a header. However, if an item is a header, it must then be the
//            // first item in a section.
//            params.setFirstPosition(ticket.sectionFirstPosition);
//            holder.itemView.setLayoutParams(params);
//            final GridSLM.LayoutParams lp = GridSLM.LayoutParams.from(holder.itemView.getLayoutParams());
//            // Overrides xml attrs, could use different layouts too.
//            if (ticket.isHeader) {
//                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
//            }
//            lp.setSlm(ticket.sectionManager == LINEAR ? LinearSLM.ID : GridSLM.ID);
//            lp.setFirstPosition(ticket.sectionFirstPosition);
//            holder.itemView.setLayoutParams(lp);

        }

        if (ticket.isHeader) {
            holder.itemView.bringToFront();
            if ((MVSConstants.FragmentType.FRAGMENT_GRABORDERS + "," + MVSConstants.FragmentType.FRAGMENT_RECEIVEORDERS).equals(type)) {
                if (ticket.getStatus() != null && ticket.getStatus().getState() == 0) {
                    if (((HeaderViewHolder) holder).icon != null) {
                        ((HeaderViewHolder) holder).icon.setBackgroundResource(R.drawable.grab_catelog);
                        if (((HeaderViewHolder) holder).textview != null) {
                            ((HeaderViewHolder) holder).textview.setText(ContextManager.context().getString(R.string.to_graborder));
                        }
                    }
                } else {
                    if (((HeaderViewHolder) holder).icon != null) {
                        ((HeaderViewHolder) holder).icon.setBackgroundResource(R.drawable.receive_catelog);
                        if (((HeaderViewHolder) holder).textview != null) {
                            ((HeaderViewHolder) holder).textview.setText(ContextManager.context().getString(R.string.to_receive_tickets));
                        }
                    }
                }
                if (((HeaderViewHolder) holder).totalNumber != null) {
                    ((HeaderViewHolder) holder).totalNumber.setVisibility(View.GONE);
                    ((HeaderViewHolder) holder).totalNumber.setText("(" + ticket.total + ")");
                }
            } else if ((MVSConstants.FragmentType.FRAGMENT_ACTIVORDERS + "," + MVSConstants.FragmentType.FRAGMENT_FINISH_ORDERS).equals(type)) {
                DLog.i("isheader", "" + ticket.getCtime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(ticket.getCtime() == null ? System.currentTimeMillis() : ticket.getCtime().getTime());
            } else if (
                    MVSConstants.FragmentType.FRAGMENT_DONE_HISTORYORDERS.equals(type)
                            || MVSConstants.FragmentType.FRAGMENT_CLOSE_HISTORYORDERS.equals(type)
                            || (MVSConstants.FragmentType.FRAGMENT_DONE_HISTORYORDERS
                            + "," + MVSConstants.FragmentType.FRAGMENT_CLOSE_HISTORYORDERS).equals(type)
                    ) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(ticket.getCtime() == null ? System.currentTimeMillis() : ticket.getCtime().getTime());
                ((HeaderViewHolder) holder).textview.setText(new SimpleDateFormat(MVSConstants.TICKET_HEADER_HISTORY_TIME).format(ticket.getCtime() == null ? new Date() : ticket.getCtime()));
            }
        } else {
            TicketAdapter.onBindTicket((TicketViewHolder) holder, ticket, type, listener, worker, this, fragment);
            if (holder instanceof OrderContentViewHolder) {
                if (type.equals(MVSConstants.FragmentType.FRAGMENT_ACTIVORDERS + "," + MVSConstants.FragmentType.FRAGMENT_FINISH_ORDERS))
                    mItemManger.bind(holder.itemView, position);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return ticketList.get(position).isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public int getItemCount() {
        return ticketList == null || ticketList.size() == 0 ? 0 : ticketList.size();
    }

    public static List<Ticket> getGroupedListData(List<Ticket> listData, List<Ticket> ticketList, String type) {
        if ((MVSConstants.FragmentType.FRAGMENT_ACTIVORDERS + "," + MVSConstants.FragmentType.FRAGMENT_FINISH_ORDERS).equals(type)) {
            //Insert headers into list of items.
            String lastHeader = "";
            int sectionManager = -1;
            int headerCount = 0;
            int sectionFirstPosition = 0;
            Calendar instance = Calendar.getInstance();
            for (int i = 0; i < listData.size(); i++) {
                Ticket item = listData.get(i);
                instance.setTime(item.getCtime());
                String header = (instance.get(Calendar.DAY_OF_YEAR) + instance.get(Calendar.YEAR) * 1000) + "";
                if (!TextUtils.equals(lastHeader, header)) {
                    // Insert new header view and update section data.
                    sectionManager = (sectionManager + 1) % 2;
                    sectionFirstPosition = i + headerCount;
                    lastHeader = header;
                    headerCount += 1;

                    Ticket billOrder = new Ticket(true, sectionFirstPosition + 1, sectionManager);
                    billOrder.setCtime(item.getCtime());
                    ticketList.add(billOrder);
                }
                item.isHeader = false;
                item.sectionFirstPosition = sectionFirstPosition;
                item.sectionManager = sectionManager;
                ticketList.add(item);
            }
        } else if ((MVSConstants.FragmentType.FRAGMENT_GRABORDERS + "," + MVSConstants.FragmentType.FRAGMENT_RECEIVEORDERS).equals(type)) {
            //Insert headers into list of items.
            String lastHeader = "";
            int sectionManager = -1;
            int headerCount = 0;
            int sectionFirstPosition = 0;
            int total = 0;
            Ticket billOrder = null;
            for (int i = 0; i < listData.size(); i++) {
                Ticket item = listData.get(i);
                String header = item.getStatus().getState() + "";
                if (!TextUtils.equals(lastHeader, header)) {
                    // Insert new header view and update section data.
                    sectionManager = (sectionManager + 1) % 2;
                    sectionFirstPosition = i + headerCount;
                    lastHeader = header;
                    headerCount += 1;
                    total = 0;
                    total++;

                    billOrder = new Ticket(true, sectionFirstPosition + 1, sectionManager);
                    billOrder.setStatus(item.getStatus());

                    billOrder.total = total;
                    ticketList.add(billOrder);
                } else {
                    if (billOrder != null) {
                        total++;
                        billOrder.total = total;
                    }
                }
                item.isHeader = false;
                item.sectionFirstPosition = sectionFirstPosition;
                item.sectionManager = sectionManager;
                ticketList.add(item);
            }
        } else if (
                MVSConstants.FragmentType.FRAGMENT_DONE_HISTORYORDERS.equals(type)
                        || MVSConstants.FragmentType.FRAGMENT_CLOSE_HISTORYORDERS.equals(type)
                        || (MVSConstants.FragmentType.FRAGMENT_DONE_HISTORYORDERS
                        + "," + MVSConstants.FragmentType.FRAGMENT_CLOSE_HISTORYORDERS).equals(type)
                ) {
            //Insert headers into list of items.
            String lastHeader = "";
            int sectionManager = -1;
            int headerCount = 0;
            int sectionFirstPosition = 0;
            Calendar instance = Calendar.getInstance();
            for (int i = 0; i < listData.size(); i++) {
                Ticket item = listData.get(i);
                instance.setTime(item.getCtime());
                String header = (instance.get(Calendar.MONTH) + instance.get(Calendar.YEAR) * 100) + "";
                if (!TextUtils.equals(lastHeader, header)) {
                    // Insert new header view and update section data.
                    sectionManager = (sectionManager + 1) % 2;
                    sectionFirstPosition = i + headerCount;
                    lastHeader = header;
                    headerCount += 1;

                    Ticket billOrder = new Ticket(true, sectionFirstPosition + 1, sectionManager);
                    billOrder.setCtime(item.getCtime());
                    ticketList.add(billOrder);
                }
                item.isHeader = false;
                item.sectionFirstPosition = sectionFirstPosition;
                item.sectionManager = sectionManager;
                ticketList.add(item);
            }
        }
        return ticketList;
    }

    public void setListData(List<Ticket> listData) {
        this.ticketList.clear();
        ticketShadowList = null;
        if (listData == null) return;
        if (type.equals(MVSConstants.FragmentType.FRAGMENT_ACTIVORDERS + "," + MVSConstants.FragmentType.FRAGMENT_FINISH_ORDERS)
        ||MVSConstants.FragmentType.FRAGMENT_DONE_HISTORYORDERS.equals(type)
                || MVSConstants.FragmentType.FRAGMENT_CLOSE_HISTORYORDERS.equals(type)
                || (MVSConstants.FragmentType.FRAGMENT_DONE_HISTORYORDERS
                + "," + MVSConstants.FragmentType.FRAGMENT_CLOSE_HISTORYORDERS).equals(type)
                ) {
            Collections.sort(listData, new Comparator<Ticket>() {
                @Override
                public int compare(Ticket lhs, Ticket rhs) {
                    return -lhs.getCtime().compareTo(rhs.getCtime());
                }
            });
        }else {
            //filter time passed grab ticket
//            Iterator<Ticket> iterator = listData.iterator();
//            while (iterator.hasNext()){
//                Ticket t = iterator.next();
//                if(t.getStatus().getState() == 1)
//                    break;
//                final Date[] dates = t.getTimers();
//                long curr = System.currentTimeMillis();
//                if (dates != null && dates.length > 0) {
//                    Date last = null;
//                    for (Date date : dates) {
//                        if (curr < date.getTime()) {
//                            last = date;
//                            break;
//                        }
//                    }
//                    if (last == null || last.getTime() - System.currentTimeMillis() < 1000) {
//                        iterator.remove();
//                    }
//                }else {
//                    iterator.remove();
//                }
//            }
            Collections.sort(listData, new Comparator<Ticket>() {
                @Override
                public int compare(Ticket lhs, Ticket rhs) {
                    return -lhs.getStatus().getState() + rhs.getStatus().getState();
                }
            });
        }
        getGroupedListData(listData, this.ticketList, type);
    }

    public void addListData(List<Ticket> listData) {
        if (listData == null || listData.size() == 0)
            return;
        for (Ticket t : ticketList) {
            if (t.isHeader) {
                ticketList.remove(t);
            }
        }
        listData.addAll(0, this.ticketList);
        this.ticketList.clear();
        setListData(listData);
        ticketShadowList = null;
    }

    public CopyOnWriteArrayList<Ticket> getTicketList() {
        return ticketList;
    }

    public void filterList(String text) {
        if (ticketShadowList == null) {
            ticketShadowList = new CopyOnWriteArrayList<>();
            ticketShadowList.addAll(ticketList);
        }
        ticketList.clear();
        ticketList.addAll(ticketShadowList);
        if (text == null || text.trim().isEmpty()) {
            ticketShadowList = null;
            return;
        }
        Iterator<Ticket> iterator = ticketShadowList.iterator();
        while (iterator.hasNext()) {
            Ticket item = iterator.next();
            DLog.w("search", item.toString() + " contain " + item.toString().contains(text));
            if (!item.toString().contains(text)) {
                DLog.w("search", "remove" + item.getTicketid());
                ticketList.remove(item);
            }
        }
        DLog.w("search", "size" + ticketList.size());
        DLog.w("search", "size shadow" + ticketShadowList.size());
    }

    public void setLatLng(LatLng latLng) {
        TicketAdapter.latLng = latLng;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.repo_card;
    }

    public void refreshTimers(FXRecyclerView recyclerView) {
        int count = getItemCount();
        for(int i = 0; i< count; i++) {
            Ticket ticket = getTicketList().get(i);
            RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(1+i);
            if(holder instanceof TicketViewHolder)
                bindGrabTimer(ticket , (TicketViewHolder) holder, this);
        }
    }

    public static void bindGrabTimer(final Ticket ticket, final TicketViewHolder holder, final TicketAdapter adapter){
        if (ticket.getStatus() != null && ticket.getStatus().getState() == 0) {
            final Date[] dates = ticket.getTimers();
            long curr = System.currentTimeMillis();
            if (dates != null && dates.length > 0) {
                Date last = null;
                for (Date date : dates) {
                    if (curr < date.getTime()) {
                        last = date;
                        break;
                    }
                }
                if (last == null) {
//                    new Handler(Looper.getMainLooper())
//                            .postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    RxBus rxBus = FApplication.getRxBus();
//                                    rxBus.send(TicketListFragment.REFRESH_LSIT_RECEIVE);
//                                }
//                            }, 1000);
//                    说明时间值超过，删掉这个item吧
                    if (holder.leftAction != null) {
                        Drawable drawable = ContextManager.context().getResources().getDrawable(R.drawable.grab_countdown);
/// 这一步必须要做,否则不会显示.
                        drawable.setBounds(0, 0, 50, 50);
                        if (holder.leftAction != null) {
                            holder.leftAction.setCompoundDrawables(drawable, null, null, null);
                            holder.leftAction.setText("已结束");
                            holder.leftAction.setTextColor(ContextManager.context().getResources().getColor(R.color.grab_color));
                        }
                    }
                } else {
                    //刷新订单列表
                    if (last.getTime() - System.currentTimeMillis() < 1000) {
//                        new Handler(Looper.getMainLooper())
//                                .postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        RxBus rxBus = FApplication.getRxBus();
//                                        rxBus.send(TicketListFragment.REFRESH_LSIT_RECEIVE);
//                                    }
//                                }, 1000);
                        if (holder.leftAction != null) {
                            Drawable drawable = ContextManager.context().getResources().getDrawable(R.drawable.grab_countdown);
/// 这一步必须要做,否则不会显示.
                            drawable.setBounds(0, 0, 50, 50);
                            if (holder.leftAction != null) {
                                holder.leftAction.setCompoundDrawables(drawable, null, null, null);
                                holder.leftAction.setText("已结束");
                                holder.leftAction.setTextColor(ContextManager.context().getResources().getColor(R.color.grab_color));
                            }
                        }
                        return;
                    }
                    Date remain = new Date(last.getTime() - System.currentTimeMillis());
                    hourMinFormater = new SimpleDateFormat("mm分ss秒");
                    if (holder.leftAction != null) {
                        Drawable drawable = ContextManager.context().getResources().getDrawable(R.drawable.grab_countdown);
/// 这一步必须要做,否则不会显示.
                        //drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        drawable.setBounds(0, 0, 50, 50);
                        holder.leftAction.setCompoundDrawables(drawable, null, null, null);
                        if (remain.getTime() >= 0) {
                            holder.leftAction.setText(hourMinFormater.format(remain));
                            holder.leftAction.setTextColor(ContextManager.context().getResources().getColor(R.color.grab_color_time));
                        }else {
                            if (holder.leftAction != null) {
                                holder.leftAction.setText("已结束");
                                holder.leftAction.setTextColor(ContextManager.context().getResources().getColor(R.color.grab_color));
                            }
                        }
                    }
                }
            } else {
//                FApplication.getRxBus().send(TicketListFragment.REFRESH_LSIT_RECEIVE);
                if (holder.leftAction != null) {
                    Drawable drawable = ContextManager.context().getResources().getDrawable(R.drawable.grab_countdown);
/// 这一步必须要做,否则不会显示.
                    drawable.setBounds(0, 0, 50, 50);
                    if (holder.leftAction != null) {
                        holder.leftAction.setCompoundDrawables(drawable, null, null, null);
                        holder.leftAction.setText("已结束");
                        holder.leftAction.setTextColor(ContextManager.context().getResources().getColor(R.color.grab_color));
                    }
                }
            }
        }
    }


    public interface OnTicketSelectedListener {
        void onTicketSelected(Ticket order, Context context);
    }

    public static class OrderContentViewHolder extends TicketViewHolder implements SwipeLayout.SwipeListener, View.OnClickListener {

        public OrderContentViewHolder(View itemView) {
            super(itemView);
//set show mode.
            if (swipeLayout != null) {
                swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
//add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
                swipeLayout.addDrag(SwipeLayout.DragEdge.Right, itemView.findViewById(R.id.bottom_wrapper));

                swipeLayout.addSwipeListener(this);
            }
        }

        @Override
        public void onClick(View v) {
        }

        @Override
        public void onStartOpen(SwipeLayout layout) {
        }

        @Override
        public void onOpen(SwipeLayout layout) {
        }

        @Override
        public void onStartClose(SwipeLayout layout) {
        }

        @Override
        public void onClose(SwipeLayout layout) {
        }

        @Override
        public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

        }

        @Override
        public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        @Nullable
        @Bind(R.id.text)
        public TextView textview;
        @Nullable
        @Bind(R.id.image_icon)
        public ImageView icon;
        @Nullable
        @Bind(R.id.total_number)
        public TextView totalNumber;

        public HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}

