package info.futureme.abs.example.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.example.R;
import info.futureme.abs.example.entity.OrderMenu;

/**
 * Created by hippo on 11/29/15.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    List<OrderMenu> menuList = new ArrayList<OrderMenu>();
    OnMenuSelectedListener listener;

    public MenuAdapter(Context ctx) {
    }

    public void setOnMenuSelectedListener(OnMenuSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.order_content_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        OrderMenu menu = menuList.get(i);
        Glide.with(ContextManager.context()).load(menu.getIcon()).into(holder.imageView);
        holder.textView.setText(menu.getActionName());
    }

    @Override
    public int getItemCount() {
        return menuList == null ? 0 : menuList.size();
    }

    public void setListData(List<OrderMenu> listData) {
        this.menuList.clear();
        this.menuList = listData;
    }

    public void addMoreData(List<OrderMenu> moreData) {
        this.menuList.addAll(moreData);
    }

    public List<OrderMenu> getOrderMenuList() {
        return menuList;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.menu_title)
        TextView textView;
        @Bind(R.id.menu_icon)
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (listener != null) {
                OrderMenu menu = menuList.get(position);
                listener.onMenuSelected(menu);
            }
        }
    }

    public static interface OnMenuSelectedListener {
        void onMenuSelected(OrderMenu menu);
    }
}

