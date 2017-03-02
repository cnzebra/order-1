package com.mrwind.order.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mrwind.common.util.NumberUtils;
import com.mrwind.order.entity.Express;

@Service
public class ExpressBindService {
	
	@Autowired ExpressService expressService;

	public String bindExpress(String expressNo, String bindExpressNo) {
		// TODO Auto-generated method stub
		Express bindExpress = expressService.selectByBindExpressNo(bindExpressNo);
		if(bindExpress!=null){
			return "该单号已经绑定了其它运单，所以无法再次绑定";
		}
		Express express = expressService.selectByExpressNo(expressNo);
		if(express==null){
			return "要绑定的运单号不存在，无法绑定";
		}
		
		expressService.updateExpressBindNo(expressNo, bindExpressNo);
		return null;
	}

	public String cancelExpressBindNo(String expressNo) {
		// TODO Auto-generated method stub
		Express express = expressService.selectByNo(expressNo);
		if(express==null){
			return "要解绑定的运单号不存在";
		}
		expressService.updateExpressBindNo(express.getExpressNo(), "");
		return null;
	}
	
	public boolean checkBind(String bindExpressNo){
		if(StringUtils.isBlank(bindExpressNo))return true;
		boolean checkoutExpress = NumberUtils.checkoutExpress(bindExpressNo);
		if(!checkoutExpress) return false;
		Express bindExpress = expressService.selectByBindExpressNo(bindExpressNo);
		if(bindExpress!=null){
			return false;
		}
		return true;
	}

}
