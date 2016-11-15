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
import info.futureme.abs.example.entity.g.Project;

/**
 * Created by hippo on 11/29/15.
 */
public class OrderProjectAdapter extends RecyclerView.Adapter<OrderProjectAdapter.ViewHolder> {
    List<Project> projectList = new ArrayList<Project>();
    OnProjectSelectedListener listener;
    private int currentIndex = -1;

    public OrderProjectAdapter(Context ctx) {
    }

    public void setOnMenuSelectedListener(OnProjectSelectedListener listener) {
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
            holder.orderStatusBtn.setSelected(true);
        } else {
            holder.orderStatusBtn.setSelected(false);
        }
        Project project = projectList.get(i);
        holder.orderStatusBtn.setText(project.getName());
    }

    @Override
    public int getItemCount() {
        return projectList == null ? 0 : projectList.size();
    }

    public void setListData(List<Project> listData) {
        this.projectList.clear();
        this.projectList.addAll(listData);
    }

    public void addMoreData(List<Project> moreData) {
        this.projectList.addAll(moreData);
    }

    public List<Project> getOrderStatusList() {
        return projectList;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.order_status_btn)
        Button orderStatusBtn;

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
                Project project = projectList.get(position);
                listener.onProjectSelected(project,currentIndex);
            }
        }
    }

    public interface OnProjectSelectedListener {
        void onProjectSelected(Project project,int index);
    }
}

