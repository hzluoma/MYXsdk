package com.hg6kwan.sdk;

import com.hg6kwan.sdk.inner.platform.Commplatform;

/**
 * SDK入口
 */
public class MYXChannelSDK extends Commplatform{

    private static MYXChannelSDK instance = null;
    private MYXChannelSDK(){}

    public static MYXChannelSDK getInstance(){
        if(instance == null){
            instance = new MYXChannelSDK();
        }
        return instance;
    }


}
