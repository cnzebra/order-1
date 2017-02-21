package com.mrwind.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		Express express = expressService.selectByExpressNo(expressNo);
		if(express==null){
			return "要解绑定的运单号不存在";
		}
		expressService.updateExpressBindNo(expressNo, null);
		return null;
	}
	
	

}
