package com.icbc.efrs.app.enums;
/**
 * 请求的接口类型
 *
 */
public enum ReqIntfEnums {
	ErrIntf(0, "无法识别该接口"),
	FaHai(1000, "法海接口"),
	ZhongShu(2000, "中数分页接口"),//法人对外投资、企业对外投资、股东信息
	ZSWithParam(2004, "中数带一个参数，分页"), // 软件著作、作品著作
	Qualification(2100, "资质认证"),
	Patent(2101, "专利"),
	CompanyReport(2501, "企业年报-中数不分页"),
	ZSWithParamNoPaged(2504, "中数带一个参数"),// 中数不分页接口，page和size可以在pc端请求中加入，但无效果
	FengXian(3000, "风险接口"),
	FXWithParam(3004, "风险带一个参数"),
	FXWithParams(3005, "风险带多个参数"),
	FuzzyQuery(5200, "模糊查询"),
	CompanyQuery(5201, "企业查询")
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
	// 是否是简单查询
//	public boolean simpleQuery(){
//		boolean result = true;
//		if(this == FengXian || this == CompanyQuery || this == ZhongShu || this == ErrIntf){
//			result = false;
//		}
//		return result;
//	}

	public String getDesc() {
		return desc;
	}
}
