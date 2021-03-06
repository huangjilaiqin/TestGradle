package com.lessask.test;

/**
 * Created by huangji on 2015/11/17.
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.github.captain_miao.recyclerviewutils.BaseLoadMoreRecyclerAdapter;
import com.github.captain_miao.recyclerviewutils.common.ClickableViewHolder;
import com.github.captain_miao.recyclerviewutils.listener.OnRecyclerItemClickListener;
import com.lessask.R;

import java.util.List;

/**
 * @author YanLu
 * @since 15/9/15
 */
public class SimpleAdapter extends BaseLoadMoreRecyclerAdapter<String, SimpleAdapter.ItemViewHolder>  implements OnRecyclerItemClickListener {

    public SimpleAdapter(List<String> items) {
        appendToList(items);
    }
    StringRequest stringRequest;

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new SimpleAdapter.ItemViewHolder(view);
    }


    @Override
    public void onBindItemViewHolder(SimpleAdapter.ItemViewHolder vh, int position) {
        vh.mTvContent.setText(getItem(position));
    }

    @Override
    public void onClick(View v, int position) {
        switch (v.getId()){
            case R.id.tv_content:
                Toast.makeText(v.getContext(), "on click " + position, Toast.LENGTH_SHORT).show();
                break;
            default:
                //mock click todo  last item
                remove(position);
                notifyItemRemoved(position);
        }
    }


    public class ItemViewHolder extends ClickableViewHolder{
        public TextView mTvContent;

        public ItemViewHolder(View view) {
            super(view);
            mTvContent = (TextView) view.findViewById(R.id.tv_content);
            setOnRecyclerItemClickListener(SimpleAdapter.this);
            //view.setOnClickListener(this);
            //mTvContent.setOnClickListener(this);
            addOnItemViewClickListener();
            addOnViewClickListener(mTvContent);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTvContent.getText();
        }
    }
}