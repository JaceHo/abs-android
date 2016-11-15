package info.futureme.abs.example.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import info.futureme.abs.example.R;
import info.futureme.abs.example.entity.SearchItem;
import info.futureme.abs.util.DLog;

/**
 * Created by Jeffrey on 6/19/16.
 */
public class SearchItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    CopyOnWriteArrayList<SearchItem> searchItemList = new CopyOnWriteArrayList<>();
    CopyOnWriteArrayList<SearchItem> searchItemShadowList;
    private OnSearchItemSelectedListener onSearchItemSelectedListener;

    public void setListData(List<SearchItem> listData) {
        this.searchItemList.clear();
        searchItemShadowList = null;
        if (listData == null) return;
        searchItemList.addAll(listData);
    }

    public void addListData(List<SearchItem> listData) {
        if (listData == null || listData.size() == 0)
            return;
        listData.addAll(0, this.searchItemList);
        this.searchItemList.clear();
        setListData(listData);
        searchItemShadowList = null;
    }

    public void filterList(String text) {
        if (searchItemShadowList == null) {
            searchItemShadowList = new CopyOnWriteArrayList<>();
            searchItemShadowList.addAll(searchItemList);
        }
        searchItemList.clear();
        searchItemList.addAll(searchItemShadowList);
        if (text == null || text.trim().isEmpty()) {
            searchItemShadowList = null;
            return;
        }
        Iterator<SearchItem> iterator = searchItemShadowList.iterator();
        while (iterator.hasNext()) {
            SearchItem item = iterator.next();
            DLog.w("search", item.toString() + " contain " + item.toString().contains(text));
            if (!item.toString().contains(text)) {
                DLog.w("search", "remove" + item.getId());
                searchItemList.remove(item);
            }
        }
        DLog.w("search", "size" + searchItemList.size());
        DLog.w("search", "size shadow" + searchItemShadowList.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, null));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        SearchItem item = searchItemList.get(position);
        if (((HeaderViewHolder) holder).textview != null) {
            if(item.getSerialNum() != null && item.getDeviceId() != -1) {
                ((HeaderViewHolder) holder).textview.setText(item.getSerialNum());
            }else if(item.getCustAddressId() != -1 && item.getName() != null){
                ((HeaderViewHolder) holder).textview.setText(item.getName());
            } else if (item.getProjectName() != null && item.getProjectId() != -1) {
                ((HeaderViewHolder) holder).textview.setText(item.getProjectName());
            } else if(item.getFaultId() != -1 && item.getFaultName() != null){
                ((HeaderViewHolder) holder).textview.setText(item.getFaultName());
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSearchItemSelectedListener != null) {
                    onSearchItemSelectedListener.onSearchItemSelected(searchItemList.get(position), holder.itemView.getContext());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchItemList == null ? 0 : searchItemList.size();
    }

    public void setOnSearchItemSelectedListener(OnSearchItemSelectedListener onSearchItemSelectedListener) {
        this.onSearchItemSelectedListener = onSearchItemSelectedListener;
    }

    public interface OnSearchItemSelectedListener {
        void onSearchItemSelected(SearchItem searchItem, Context context);
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        @Nullable
        @Bind(R.id.textView)
        public TextView textview;
        public HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
