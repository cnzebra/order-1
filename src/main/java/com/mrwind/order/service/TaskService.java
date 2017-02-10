package com.mrwind.order.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mrwind.order.App;
import com.mrwind.order.dao.OrderDao;
import com.mrwind.order.entity.Express;
import com.mrwind.order.entity.Order;
import com.mrwind.order.repositories.OrderRepository;

@Service
public class TaskService {

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderDao orderDao;

	@Autowired
	ExpressService expressService;

	public void sendOrder(){
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.HOUR_OF_DAY, 2);
		List<Order> unDuiOrderList = orderDao.findUnDuiOrder(instance.getTime());
		for(Order order : unDuiOrderList){
			List<Date> unDuiTimes = order.getUnDuiTimes();
			for(Date duiDate : unDuiTimes){
				if(duiDate.compareTo(instance.getTime())<1){
					Express initExpress = expressService.initExpress(order,duiDate);
					if(initExpress!=null){
						order.getUnDuiTimes().remove(duiDate);
					}
				}
			}
			
			if(unDuiTimes.size()==0){
				order.setStatus(App.ORDER_BEGIN);
				orderRepository.save(order);
			}
		}
	}

}
