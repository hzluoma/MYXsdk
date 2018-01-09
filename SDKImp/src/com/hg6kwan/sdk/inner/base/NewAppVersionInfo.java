package com.hg6kwan.sdk.inner.base;

/**
 * 应用更新信息
 *
 */
public class NewAppVersionInfo {
	private int updateType;       //更新类型 0-不更新 1-强制更新 2-推荐更新
	private String newVersion;    //新版本号
	private String newVersionUrl; //更新地址
	private String appName;       //应用名称
	private String updateLog;     //备用
	private int softSize = -1;    //应用大小
	private String notify;        //通知
	
	
	
	@Override
	public String toString() {
		return "NewAppVersionInfo [updateType=" + updateType
				+ ", newVersion=" + newVersion + ", newVersionUrl="
				+ newVersionUrl + ", appName=" + appName + ", updateLog="
				+ updateLog + ", softSize=" + softSize + ", notify=" + notify
				+ ", k=" + k + "]";
	}

	/**更新类型 */
	public static final int VERSION_SAME = 0;
	public static final int FORCE_UPDATE = 1;
	public static final int UN_FORCE_UPDATE = 2;

	public String getNotify()
	{
		return this.notify;
	}

	public void setNotify(String notify)
	{
		this.notify = notify;
	}

	public String getAppName()
	{
		return this.appName;
	}

	public void setAppName(String appName)
	{
		this.appName = appName;
	}

	public String getUpdateLog()
	{
		return this.updateLog;
	}

	public void setUpdateLog(String updateLog)
	{
		this.updateLog = updateLog;
	}

	public int getUpdateType()
	{
		return this.updateType;
	}

	public void setUpdateType(int updateType)
	{
		this.updateType = updateType;
	}

	public String getNewVersion()
	{
		return this.newVersion;
	}

	public void setNewVersion(String newVersion)
	{
		this.newVersion = newVersion;
	}

	public String getNewVersionUrl()
	{
		return this.newVersionUrl;
	}

	public void setNewVersionUrl(String newVersionUrl)
	{
		this.newVersionUrl = newVersionUrl;
	}

	public int getSoftSize()
	{
		return this.softSize;
	}

	public void setSoftSize(int aSize)
	{
		this.softSize = aSize;
	}
	private String k;
	public String getK() {
		return k;
	}
	public void setK(String k) {
		this.k = k;
	}
	
	
	
}
