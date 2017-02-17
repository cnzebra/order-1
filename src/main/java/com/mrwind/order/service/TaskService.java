package com.mrwind.order.service;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mrwind.order.App;
import com.mrwind.order.dao.ExpressDao;
import com.mrwind.order.entity.Express;
import com.mrwind.order.repositories.OrderRepository;

@Service
public class TaskService {

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	ExpressDao expressDao;

	@Autowired
	ExpressService expressService;

	public void sendOrder(){
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.HOUR_OF_DAY, -2);
		
		List<Express> list = expressDao.findUnBegin(instance.getTime());
		for(Express express : list){
			expressDao.updateStatus(express.getExpressNo(), App.ORDER_BEGIN, App.ORDER_PRE_CREATED);
			expressService.sendExpressLog21010(express);
		}
	}

}
