package com.hg6kwan.sdk.inner.service;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.alibaba.fastjson.parser.Feature;
import com.hg6kwan.sdk.inner.base.BaseInfo;
import com.hg6kwan.sdk.inner.log.LogUtil;
import com.hg6kwan.sdk.inner.net.HttpResultData;
import com.hg6kwan.sdk.inner.net.HttpUtility;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.utils.CommonFunctionUtils;
import com.hg6kwan.sdk.inner.utils.Constants;
import com.hg6kwan.sdk.inner.utils.MD5;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONObject;


public class PayService extends HttpUtility{


	private static String mUid;
	private static String mService;
	private static String mUrl;
	private static String mChannel;
	private static String mAppkey;
	private static String mAppid;
	private static String mUsername;
	private static String mServerName;
	private static String mServerId;
	private static String mRoleName;
	private static String mRoleID;
	private static String mRoleLevel;
	private static String mCpOrder;
	private static String mExtendstr;
	private static String mPrice;
	private static String mGoodsName;
	private static int mGoodsID;

	/**
	 * H5支付获取订单号
	 * @return 成功返回 订单号，失败返回null
	 * @throws Exception
	 */
	public HttpResultData getOrderId(String username,String serverID, String role,String
			goodsName, int goodsID,String extendstr){

		BaseInfo baseInfo = ControlCenter.getInstance().getBaseInfo();
		String channel = baseInfo.gChannnel;
		String appkey = baseInfo.gAppKey;
		String appid = baseInfo.gAppId;
		String uuid = baseInfo.UUID;
		String sid = baseInfo.gSessionId;
		String password = MD5.getMD5String("");
		String service = Constants.HTTP_GET_ORDER_SERVICE;
		String url = Constants.BASE_URL;

		if (username == null || TextUtils.isEmpty(username)){
			username = baseInfo.login.getU();
		}

		JSONObject data = new JSONObject();
		try {
			data.put("username", username);
			data.put("passwd", password);
			data.put("channel", channel);
			data.put("code", "");
			data.put("udid", uuid);
			data.put("mobile", "");
			data.put("sid", sid);
			data.put("server", "");
			data.put("goodsName", goodsName);
			data.put("goodsID", goodsID);
			data.put("role", role);
			data.put("extends", extendstr);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//签名数据
		String sign = CommonFunctionUtils.getSignString(service, appid, appkey, data);
		LogUtil.i("SIGN", "getResult: " + sign);

		HttpResultData result = new HttpResultData();

		try {
			okhttp3.Response response = OkHttpUtils
					.post()
					.url(url)
					.addParams("service", service)
					.addParams("appid", appid)
					.addParams("data", data.toString())
					.addParams("sign", sign)
					.build()
					.execute();

			com.alibaba.fastjson.JSONObject body= com.alibaba.fastjson.JSONObject.parseObject(response.body().string());

			result.state = body.getJSONObject("state");
			result.data = body.getJSONObject("data");

			transformsException(response.code(), result.data);

			return result;
		} catch (Exception e) {

		}
		return result;
	}

	/**
	 *
	 username 		账号
	 udid			设备码
	 channel		渠道
	 payChannel     支付渠道ID (这个是在getPayState里面获得的支付渠道ID)
	 serverName		服务器名称
	 serverId		服务器ID
	 roleName		角色名
	 roleID			角色ID
	 roleLevel		角色等级
	 goodsName		商品名称  (String)
	 goodsID		商品ID 	 (int)
	 cpOrder		CP传过来的订单参数
	 extends		拓展字段
	 * @return
	 */
	//获取支付方式和支付渠道ID
	public HttpResultData getPayState(String uid, String userName, String Price, String serverNAME, String
			serverID, String roleNAME, String roleId, String roleLEVEL, String goodsNAME, int goodsId, String cpORDER, String
			extendStr) {

		final HttpResultData result = new HttpResultData();

		BaseInfo baseInfo = ControlCenter.getInstance().getBaseInfo();
		mPrice = Price;
		mUid = uid;
		mService = Constants.SVERVICE_GET_PAYSTATE;
		mUrl = Constants.BASE_URL;
		mChannel = baseInfo.gChannnel;
		mAppkey = baseInfo.gAppKey;
		mAppid = baseInfo.gAppId;
		mUsername = userName;
		mServerName = serverNAME;
		mServerId = serverID;
		mRoleName = roleNAME;
		mRoleID = roleId;
		mRoleLevel = roleLEVEL;
		mGoodsName = goodsNAME;
		mGoodsID = goodsId;
		mCpOrder = cpORDER;
		mExtendstr = extendStr;
		JSONObject data = new JSONObject();

		com.hg6kwan.sdk.inner.log.LogUtil.d("6kwangetpayState uid : ",mUid);
		com.hg6kwan.sdk.inner.log.LogUtil.d("6kwangetpayState username : ",mUsername);
		try {
			data.put("username", mUsername);
			data.put("udid", mUid);
			data.put("channel", mChannel);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//签名数据
		String sign = CommonFunctionUtils.getSignString(mService, mAppid, mAppkey, data);
		LogUtil.i("SIGN", "getResult: " + sign);

		try {
			okhttp3.Response response = OkHttpUtils
					.post()
					.url(mUrl)
					.addParams("service", mService)
					.addParams("appid", mAppid)
					.addParams("data", data.toString())
					.addParams("sign", sign)
					.build()
					.execute();

            com.alibaba.fastjson.JSONObject body= com.alibaba.fastjson.JSONObject.parseObject
                    (response.body().string(),Feature.OrderedField);

			result.state = body.getJSONObject("state");
			result.data = body.getJSONObject("data");
			LogUtil.i(Constants.tag,body.toString());
			transformsException(response.code(), result.data);

		} catch (Exception e) {
			e.printStackTrace();
		}


		return result;
	}

	//获取支付宝支付信息
	public HttpResultData getOrderInfo(String payChannel) {

		HttpResultData result = new HttpResultData();

		String service = Constants.SERVICE_GET_APPORDER;

		JSONObject data = new JSONObject();
		try {
			data.put("username", mUsername);
			data.put("udid", mUid);
			data.put("price", mPrice);
			data.put("channel", mChannel);
			data.put("payChannel", payChannel);
			data.put("serverName", mServerName);
			data.put("serverId", mServerId);
			data.put("roleName", mRoleName);
			data.put("roleID", mRoleID);
			data.put("roleLevel", mRoleLevel);
			data.put("goodsName", mGoodsName);
			data.put("goodsID", mGoodsID);
			data.put("cpOrder", mCpOrder);
			data.put("extends", mExtendstr);
			LogUtil.i("getOrderInfo", "getOrderInfo: " + data.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//签名数据
		String sign = CommonFunctionUtils.getSignString(service, mAppid, mAppkey, data);
		LogUtil.i("getOrderInfo: ", "getOrderInfo: " + sign);
		try {
			okhttp3.Response response = OkHttpUtils
					.post()
					.url(Constants.BASE_URL)
					.addParams("service", Constants.SERVICE_GET_APPORDER)
					.addParams("appid", mAppid)
					.addParams("data", data.toString())
					.addParams("sign", sign)
					.build()
					.execute();

			com.alibaba.fastjson.JSONObject body= com.alibaba.fastjson.JSONObject.parseObject(response.body().string());
			LogUtil.i("getOrderInfo", "getOrderInfo: " + body.toJSONString());

			result.state = body.getJSONObject("state");
			result.data = body.getJSONObject("data");

			LogUtil.i(Constants.tag,result.state.toString());

			transformsException(response.code(), result.data);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 获取微信预付单
	 * @param payChannel
	 * @return
     */
	public HttpResultData getWeChatOrder(String payChannel) {

		HttpResultData result = new HttpResultData();

		String service = Constants.SERVICE_GET_APPORDER;

		JSONObject data = new JSONObject();
		try {
			data.put("username", mUsername);
			data.put("udid", mUid);						//这里命名不是很好,返回账户唯一标识,应该叫uid
			data.put("price", mPrice);
			data.put("channel", mChannel);
			data.put("payChannel", payChannel);
			data.put("serverName", mServerName);
			data.put("serverId", mServerId);
			data.put("roleName", mRoleName);
			data.put("roleID", mRoleID);
			data.put("roleLevel", mRoleLevel);
			data.put("goodsName", mGoodsName);
			data.put("goodsID", mGoodsID);
			data.put("cpOrder", mCpOrder);
			data.put("extends", mExtendstr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//签名数据
		String sign = CommonFunctionUtils.getSignString(service, mAppid, mAppkey, data);

		try {
			okhttp3.Response response = OkHttpUtils
					.post()
					.url(Constants.BASE_URL)
					.addParams("service", Constants.SERVICE_GET_APPORDER)
					.addParams("appid", mAppid)
					.addParams("data", data.toString())
					.addParams("sign", sign)
					.build()
					.execute();
			
			com.alibaba.fastjson.JSONObject body= com.alibaba.fastjson.JSONObject.parseObject(response.body().string());

			result.state = body.getJSONObject("state");
			result.data = body.getJSONObject("data");
			LogUtil.i("getOrderInfo", "getOrderInfo: " + body.toString());
			transformsException(response.code(), result.data);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 查询微信支付的支付结果
	 * @param order_id	订单ID
	 * @return
     */
	public HttpResultData checkWXPayResult(String order_id) {

		HttpResultData result = new HttpResultData();

		String service = Constants.SERVICE_CHECK_WXRESULT;

		JSONObject data = new JSONObject();
		try {
			data.put("order_id", order_id);
			data.put("channel", mChannel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//签名数据
		String sign = CommonFunctionUtils.getSignString(service, mAppid, mAppkey, data);

		try {
			okhttp3.Response response = OkHttpUtils
					.post()
					.url(Constants.BASE_URL)
					.addParams("service", service)
					.addParams("appid", mAppid)
					.addParams("data", data.toString())
					.addParams("sign", sign)
					.build()
					.execute();
			com.alibaba.fastjson.JSONObject body= com.alibaba.fastjson.JSONObject.parseObject(response.body().string());

			result.state = body.getJSONObject("state");
			result.data = body.getJSONObject("data");
			transformsException(response.code(), result.data);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

		/**
	 * 支付
	 * @throws Exception
	 */
	public void pay( WebView view){
		try {
			BaseInfo baseInfo = ControlCenter.getInstance().getBaseInfo();
			String coinCount = String.valueOf(baseInfo.payParam.getPrice());
			String url = baseInfo.payParam.getPayUrl();
			String parms = "&money="+coinCount;
			//
			view.postUrl(url,parms.getBytes());
			//
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	}
