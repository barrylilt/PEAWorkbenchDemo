package com.saama.workbench.bean;

import org.apache.commons.lang.StringEscapeUtils;

import com.saama.workbench.util.AppConstants;

public class PromoDIMBean {
	private long promotionId;
	private String promotion;
	
	public long getPromotionId() {
		return promotionId;
	}
	public void setPromotionId(long promotionId) {
		this.promotionId = promotionId;
	}
	public String getPromotion() {
		if (promotion != null) {
			if (promotion.contains(AppConstants.SYM_POUND_SIGN)) {
				promotion = StringEscapeUtils.escapeJavaScript(promotion.replaceAll(AppConstants.SYM_POUND_SIGN, AppConstants.SYM_POUND));
			}
		}
		return promotion;
	}
	public void setPromotion(String promotion) {
		this.promotion = promotion;
	}
	
	
}
