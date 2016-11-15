package info.futureme.abs.example.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import info.futureme.abs.base.InjectableDialogFragment;
import info.futureme.abs.example.R;
import info.futureme.abs.example.conf.MVSConstants.FragmentType;
import info.futureme.abs.example.entity.OrderMenu;
import info.futureme.abs.example.ui.adapter.MenuAdapter;
public class TicketContentMoreDialogFragment extends InjectableDialogFragment implements  MenuAdapter.OnMenuSelectedListener{
    public static final String TYPE = "type";
    public static final String MENU = "menu";
    public static final String ACTION_ID = "actionId";
    @Bind(R.id.order_menus) RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_NoActionBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = super.onCreateView(inflater, container, savedInstanceState);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Bundle b = getArguments();
        ArrayList<OrderMenu> list = b.getParcelableArrayList(TicketContentMoreDialogFragment.MENU);
        MenuAdapter adapter = new MenuAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        adapter.setListData(list);
        adapter.setOnMenuSelectedListener(this);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        int downY = (int) event.getY() + outHeight * 2;
                        int downY = (int) event.getY();
                        if (isOutDistrict(downY, v)) {
                            dismiss();
                        }
                        break;
                }
                return false;
            }
        });
        return view;
    }

    @OnClick(R.id.rl_order_content_arrows)
    public void onCancelMenu(View view){
        dismiss();
    }

    @Override
    public int provideContentRes() {
        return R.layout.order_content_more_dialogfragment;
    }

    @Override
    protected void onFragmentInVisible(Bundle savedInstanceState) {

    }

    @Override
    protected void onFragmentVisible(Bundle savedInstanceState) {

    }

    @Override
    public void onMenuSelected(OrderMenu menu) {
        Bundle bundle = new Bundle();
        bundle.putInt(ACTION_ID, menu.getActionId());
        mInterface.onRetrieveDialogFragmentData(bundle, FragmentType.DIALOG_MORE_FRAGMENT);
        dismiss();
    }
}
