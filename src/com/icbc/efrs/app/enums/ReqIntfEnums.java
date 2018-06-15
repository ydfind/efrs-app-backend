package com.icbc.efrs.app.enums;

public enum ReqIntfEnums {
	ErrIntf(0, "无法识别该接口"),
	FaHai(1000, "法海接口"),
	ZhongShu(2000, "中数接口"),
	FengXian(3000, "风险接口")
	;
	
	private Integer id;
	private String desc;
	
	ReqIntfEnums(Integer id, String desc){
		this.id = id;
		this.desc = desc;
	}
	
	public Integer getID(){
		return id;
	}
}
