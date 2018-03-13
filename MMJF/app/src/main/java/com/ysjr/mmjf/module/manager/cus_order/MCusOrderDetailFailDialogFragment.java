package com.ysjr.mmjf.module.manager.cus_order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.ConfigBean;
import com.ysjr.mmjf.module.customer.adapter.DropDownTabAdapter;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017-12-13.
 */

public class MCusOrderDetailFailDialogFragment extends DialogFragment {
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  Unbinder unbinder;
  private List<ConfigBean.ValuesBean> mConfigFourList = new ArrayList<>();
  private DropDownTabAdapter mTabFourAdapter;
  private ConfigBean.ValuesBean configBean = new ConfigBean.ValuesBean("资料不齐全",true);

  public static MCusOrderDetailFailDialogFragment newInstance() {
    MCusOrderDetailFailDialogFragment fg = new MCusOrderDetailFailDialogFragment();
    return fg;
  }
  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setCancelable(false);
  }
  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    View view =
        inflater.inflate(R.layout.m_dialog_fragment_cus_order_detail_fail, container, false);
    unbinder = ButterKnife.bind(this, view);
    initRv();
    return view;
  }

  private void initRv() {
    mConfigFourList.add(configBean);
    mConfigFourList.add(new ConfigBean.ValuesBean("资料不齐全",false));
    mConfigFourList.add(new ConfigBean.ValuesBean("资料不齐全",false));
    mConfigFourList.add(new ConfigBean.ValuesBean("资料不齐全",false));
    mConfigFourList.add(new ConfigBean.ValuesBean("资料不齐全",false));
    mConfigFourList.add(new ConfigBean.ValuesBean("资料不齐全",false));
    mTabFourAdapter = new DropDownTabAdapter(R.layout.drop_down_base, mConfigFourList);
    recyclerView.setAdapter(mTabFourAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    mTabFourAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        for (ConfigBean.ValuesBean bean : mConfigFourList) {
          bean.isChecked = false;
        }
        configBean= mConfigFourList.get(position);
        configBean.isChecked = true;
        mTabFourAdapter.notifyDataSetChanged();
      }
    });
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
  @OnClick(R.id.btnCancel) public void onBtnFailClicked() {
    dismiss();
  }
  @OnClick(R.id.btnCommit) public void onBtnThrowOrderClicked() {
    dismiss();
    EventBus.getDefault().post(configBean);
  }
}
