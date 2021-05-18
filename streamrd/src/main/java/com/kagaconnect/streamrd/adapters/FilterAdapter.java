package com.kagaconnect.streamrd.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kagaconnect.streamrd.R;
import com.kagaconnect.streamrd.helpers.FilterInfo;

import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder>{

    private List<FilterInfo> filters;
    private Context context;
    private int selected = 0;

    public FilterAdapter(Context context, List<FilterInfo> filters) {
        this.filters = filters;
        this.context = context;
        if(filters.size() > 0)filters.get(0).setSelected(true);
    }

    @Override
    public FilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.filter_item_layout,
                parent, false);
        FilterHolder viewHolder = new FilterHolder(view);
        viewHolder.thumbImage = (ImageView) view
                .findViewById(R.id.filter_thumb_image);
        viewHolder.filterName = (TextView) view
                .findViewById(R.id.filter_thumb_name);
        viewHolder.filterRoot = (LinearLayout)view
                .findViewById(R.id.filter_root);
        viewHolder.thumbSelected = (FrameLayout) view
                .findViewById(R.id.filter_thumb_selected);
        viewHolder.thumbSelected_bg = view.
                findViewById(R.id.filter_thumb_selected_bg);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FilterHolder holder,final int position) {
        FilterInfo filter = filters.get(position);

        holder.filterName.setText(filter.getText());

        if(position == selected){
            holder.thumbSelected.setVisibility(View.VISIBLE);
            //holder.thumbSelected_bg.setBackgroundColor(context.getResources().getColor(
            //        FilterTypeHelper.FilterType2Color(filters[position])));
            holder.thumbSelected_bg.setAlpha(0.7f);
        }else {
            holder.thumbSelected.setVisibility(View.GONE);
        }

        holder.filterRoot.setOnClickListener(v -> {
            if(selected == position)
                return;
            int lastSelected = selected;
            selected = position;
            notifyItemChanged(lastSelected);
            notifyItemChanged(position);
            onFilterChangeListener.onFilterChanged(filter);
        });

        /*holder.thumbImage.setImageResource(FilterTypeHelper.FilterType2Thumb(filters[position]));
        holder.filterName.setText(FilterTypeHelper.FilterType2Name(filters[position]));
        holder.filterName.setBackgroundColor(context.getResources().getColor(
                FilterTypeHelper.FilterType2Color(filters[position])));
        */
    }

    @Override
    public int getItemCount() {
        return filters == null ? 0 : filters.size();
    }

    class FilterHolder extends RecyclerView.ViewHolder {
        ImageView thumbImage;
        TextView filterName;
        FrameLayout thumbSelected;
        LinearLayout filterRoot;
        View thumbSelected_bg;

        public FilterHolder(View itemView) {
            super(itemView);
        }
    }

    public interface onFilterChangeListener{
        void onFilterChanged(FilterInfo filter);
    }

    private onFilterChangeListener onFilterChangeListener;

    public void setOnFilterChangeListener(onFilterChangeListener onFilterChangeListener){
        this.onFilterChangeListener = onFilterChangeListener;
    }
}
