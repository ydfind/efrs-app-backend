package com.icbc.efrs.app.domain;
/*
 * App请求实体：风险请求类
 */
public class FengXianAppResultEntity extends BaseAppResultEntity {
	
	private String datakeys;

	public void setDatakeys(String datakeys) {
		this.datakeys = datakeys;
	}

	public String getDatakeys() {
		return datakeys;
	}

}
