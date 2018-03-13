package com.ysjr.mmjf.module.customer.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.HomeFilterItem;
import java.util.List;

/**
 * Created by Administrator on 2017-11-28.
 */

public class FilterAdapter extends BaseQuickAdapter<HomeFilterItem,BaseViewHolder>{
  public FilterAdapter(int layoutResId, @Nullable List<HomeFilterItem> data) {
    super(layoutResId, data);
  }

  @Override protected void convert(BaseViewHolder helper, final HomeFilterItem item) {
    RecyclerView rv = helper.getView(R.id.childRv);
    final FilterChildAdapter childAdapter =
        new FilterChildAdapter(R.layout.c_home_filter_child_item, item.values);
    GridLayoutManager layoutManager = null;
    if (item.id == 2 || item.id == 1) {
      layoutManager = new GridLayoutManager(mContext, 2);
    } else {
      layoutManager = new GridLayoutManager(mContext, 3);
    }

    rv.setAdapter(childAdapter);
    rv.setLayoutManager(layoutManager);
    helper.setText(R.id.tvTitle, item.name);
    childAdapter.setOnItemClickListener(new OnItemClickListener() {
      private int mPosition = -1;
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        HomeFilterItem.FilterItem currItem = item.values.get(position);
        currItem.isChecked = !currItem.isChecked;
        if (mPosition != position && mPosition != -1) {
          HomeFilterItem.FilterItem beforeItem = item.values.get(mPosition);
          if (beforeItem.isChecked) {
            beforeItem.isChecked = false;
          }
        }
        childAdapter.notifyDataSetChanged();
        mPosition = position;
      }
    });
  }

}
