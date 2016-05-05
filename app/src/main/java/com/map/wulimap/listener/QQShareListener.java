package com.map.wulimap.listener;

import android.content.Context;

import com.map.wulimap.util.ToastUtil;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

//QQ分享监听
public class QQShareListener implements IUiListener {
    Context context1;

    public QQShareListener(Context context) {
        this.context1 = context;
    }

    @Override
    public void onCancel() {
        // TODO Auto-generated method stub
        ToastUtil.show(context1, "分享取消");
    }

    @Override
    public void onComplete(Object arg0) {
        // TODO Auto-generated method stub
        ToastUtil.show(context1, "分享成功");

    }

    @Override
    public void onError(UiError arg0) {
        // TODO Auto-generated method stub
        ToastUtil.show(context1, "分享出错");

    }


}