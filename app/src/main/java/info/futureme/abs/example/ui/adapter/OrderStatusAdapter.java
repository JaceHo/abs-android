package info.futureme.abs.example.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import info.futureme.abs.example.R;
import info.futureme.abs.example.entity.g.SysDict;

/**
 * Created by hippo on 11/29/15.
 */
public class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.ViewHolder> {
    List<SysDict> SysDictList = new ArrayList<>();
    OnStautsSelectedListener listener;
    private int currentIndex = -1;


    public OrderStatusAdapter(Context ctx) {
    }

    public void setOnMenuSelectedListener(OnStautsSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.order_status_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        if (currentIndex == i) {
            holder.orderSysDictBtn.setSelected(true);
        } else {
            holder.orderSysDictBtn.setSelected(false);
        }
        SysDict SysDict = SysDictList.get(i);
        String state = SysDict.getDictName();
        holder.orderSysDictBtn.setText(state);

    }

    @Override
    public int getItemCount() {
        return SysDictList == null ? 0 : SysDictList.size();
    }

    public void setListData(List<SysDict> listData) {
        SysDictList.clear();
        this.SysDictList.addAll(listData);
    }

    public void addMoreData(List<SysDict> moreData) {
        this.SysDictList.addAll(moreData);
    }

    public List<SysDict> getOrderSysDictList() {
        return SysDictList;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.order_status_btn)
        Button orderSysDictBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (listener != null) {
                if(currentIndex == position){
                    currentIndex = -1;
                }else {
                currentIndex = position;
                }
                SysDict SysDict = SysDictList.get(position);
                listener.onSysDictSelected(SysDict,currentIndex);
            }
        }
    }

    public static interface OnStautsSelectedListener {
        void onSysDictSelected(SysDict SysDict,int index);
    }
}

