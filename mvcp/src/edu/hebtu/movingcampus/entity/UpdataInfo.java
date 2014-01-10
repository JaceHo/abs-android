package edu.hebtu.movingcampus.entity;

import android.content.Context;

public class UpdataInfo {
	private String apk;
	private Context context;
	private String information;
	private String url;
	private String version;

	public UpdataInfo(Context paramContext) {
		this.context = paramContext;
	}

	public String getApk() {
		return this.apk;
	}

	public String getInformation() {
		return this.information;
	}

	public String getUrl() {
		return this.url;
	}

	public String getVersion() {
		return this.version;
	}

	public void setApk(String paramString) {
		this.apk = paramString;
	}

	public void setInformation(String paramString) {
		this.information = paramString;
	}

	public void setUrl(String paramString) {
		this.url = paramString;
	}

	public void setVersion(String paramString) {
		this.version = paramString;
	}
}

/*
 * Location: /tmp/apksrc_tmp_9gTLc/classes-dex2jar.jar Qualified Name:
 * com.caii101.util.UpdataInfo JD-Core Version: 0.6.2
 */