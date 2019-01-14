package com.goukuai.dto;

import com.alibaba.fastjson.JSON;
/**
 * @description 云端存储文件链接实体类
 * @author LiPan[wjsxhclj@sina.com]
 * @date 2019/01/09
 */
public class LinkDto {
	private String link;// 链接地址
	private String qr_url;// 二维码链接地址
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getQr_url() {
		return qr_url;
	}
	public void setQr_url(String qr_url) {
		this.qr_url = qr_url;
	}
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	
}
