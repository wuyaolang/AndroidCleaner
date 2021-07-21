package com.cooling.artifact.ads;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cooling.artifact.R;
import com.gatherad.sdk.data.config.NativeAdType;
import com.gatherad.sdk.data.entity.AdImage;
import com.gatherad.sdk.data.entity.NativeCustomAdInfo;

import java.util.List;

public class NativeCustomAdRender {

    private View mCustomView;
    private ViewGroup mAdMediaView;

    public NativeCustomAdRender(Context context) {
        if (mCustomView == null) {
            mCustomView = LayoutInflater.from(context).inflate(R.layout.layout_native_custom_ad, null);
        }
        mAdMediaView = mCustomView.findViewById(R.id.mediaView);
    }

    public View getCustomView() {
        return mCustomView;
    }

    public ViewGroup getMediaView() {
        return mAdMediaView;
    }

    public void renderView(Context context, NativeCustomAdInfo nativeCustomAdInfo) {
        if (nativeCustomAdInfo == null) {
            return;
        }
//        TextView tv_ad_desc = mCustomView.findViewById(R.id.tv_ad_desc);
//        tv_ad_desc.setText(nativeCustomAdInfo.getDescriptionText());
//
//        ImageView img_ad_icon = mCustomView.findViewById(R.id.img_ad_icon);
//        if (nativeCustomAdInfo.getIconUrl() != null) {
//            Glide.with(context).load(nativeCustomAdInfo.getIconUrl()).into(img_ad_icon);
//        }
//
//        TextView tv_ad_name = mCustomView.findViewById(R.id.tv_ad_name);
//        tv_ad_name.setText(nativeCustomAdInfo.getTitle());
//
//        ImageView img_ad_logo = mCustomView.findViewById(R.id.img_ad_logo);
//        if (nativeCustomAdInfo.getAdLogo() != null) {
//            img_ad_logo.setImageBitmap(nativeCustomAdInfo.getAdLogo());
//        }
//        TextView btn_ad_action = mCustomView.findViewById(R.id.btn_ad_action);
//        if (nativeCustomAdInfo.getInteractionType() == NativeAdType.InteractionType.DOWNLOAD) {
//            btn_ad_action.setText("下载");
//        } else {
//            btn_ad_action.setText("查看详情");
//        }
        switch (nativeCustomAdInfo.getMaterialType()) {
            case NativeAdType.MaterialType.VIDEO: {
                break;
            }
            case NativeAdType.MaterialType.IMAGE:
            default: {
                ImageView img_ad_image = mCustomView.findViewById(R.id.img_ad_image);
                List<AdImage> adImageList = nativeCustomAdInfo.getAdImageList();
                if (adImageList != null && !adImageList.isEmpty()) {
                    Glide.with(context).load(adImageList.get(0).getImageUrl()).into(img_ad_image);
                }
                break;
            }
        }
    }


}
