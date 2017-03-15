package com.mrwind.order.service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.cache.RedisCache;
import com.mrwind.common.factory.JSONFactory;
import com.mrwind.common.util.CodeUtils;
import com.mrwind.common.util.DateUtils;
import com.mrwind.common.util.HttpUtil;
import com.mrwind.order.App;
import com.mrwind.order.dao.ExpressDao;
import com.mrwind.order.entity.Address;
import com.mrwind.order.entity.Category;
import com.mrwind.order.entity.Express;
import com.mrwind.order.entity.ExpressCodeLog;
import com.mrwind.order.entity.Line;
import com.mrwind.order.entity.Line.LineUtil;
import com.mrwind.order.entity.Order;
import com.mrwind.order.entity.User;
import com.mrwind.order.repositories.ExpressCodeLogRepository;
import com.mrwind.order.repositories.ExpressRepository;

@Service
public class ExpressService {

	@Autowired
	RedisCache redisCache;

	@Autowired
	ExpressRepository expressRepository;

	@Autowired
	ExpressCodeLogRepository expressCodeLogRepository;

	ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();

	@Autowired
	ExpressBindService expressBindService;

	@Autowired
	ExpressDao expressDao;

	public List<Express> createExpress(Order order) {
		List<Express> list = new ArrayList<>();

		Date nowDate = Calendar.getInstance().getTime();
		List<Date> dueTimes = order.getDueTimes();
		JSONObject person = findPerson(order);
		User user = null;
		if (person != null) {
			user = JSONObject.toJavaObject(person, User.class);
		}
		if (dueTimes == null || dueTimes.size() == 0) {
			list.add(initVIPExpress(order, user, nowDate));
		} else {
			for (Date dueTime : dueTimes) {
				list.add(initVIPExpress(order, user, dueTime));
			}
		}

		expressRepository.save(list);
		sendExpressLog21010(list);
		return list;
	}

	public List<Express> createExpress(List<Order> orders) {

		if(orders==null || orders.size()==0){
			return null;
		}

		Date nowDate = Calendar.getInstance().getTime();
		List<Express> list = new ArrayList<>();
		JSONObject person = findPerson(orders.get(0));

		User user = null;
		if (person != null) {
			user = JSONObject.toJavaObject(person, User.class);
		}

		for(Order order : orders){
			List<Date> dueTimes = order.getDueTimes();
			if (dueTimes == null || dueTimes.size() == 0) {
				list.add(initVIPExpress(order, user, nowDate));
			} else {
				for (Date dueTime : dueTimes) {
					list.add(initVIPExpress(order, user, dueTime));
				}
			}
		}
		System.out.println(System.currentTimeMillis());
		expressRepository.save(list);
		sendExpressLog21010(list);
		System.out.println(System.currentTimeMillis());
		return list;
	}

	private Express initVIPExpress(Order order, User user, Date dueTime) {
		Express express = new Express(order);
		if (express.getCategory() == null || express.getCategory().getServiceType() == null) {
			return null;
		}
		express.setMode(express.getCategory().getServiceType().getType());

		Long pk = redisCache.getPK("express", 1);
		express.setExpressNo(pk.toString());

		if (!expressBindService.checkBind(express.getBindExpressNo())) {
			express.setBindExpressNo(null);
		}

		express.setDueTime(dueTime);
		if (DateUtils.diffMinute(dueTime) >= -60 * 2) {
			express.setStatus(App.ORDER_BEGIN);
			express.setSubStatus(App.ORDER_PRE_CREATED);
		} else {
			if (user != null) {
				List<Line> lineList = new ArrayList<>();
				Line line = new Line();
				line.setExecutorUser(user);
				line.setFence(order.getSender().getFence());
				line.setNode(order.getSender().getAddress());
				line.setIndex(1);
				line.setPlanTime(dueTime);
				lineList.add(line);
				express.setLines(lineList);
			}
		}
		return express;
	}

	/***
	 * 配送员普通单加单
	 *
	 */
	public Express initExpress(Express express) {
		JSONObject calculatePrice = HttpUtil.calculatePrice((JSONObject) JSONObject.toJSON(express.getCategory()));
		calculatePrice.put("serviceUser",express.getLines().get(0).getExecutorUser());
		calculatePrice.put("artificialPrice",0.3);
		calculatePrice.put("totalPrice",calculatePrice.getBigDecimal("totalPrice").add(BigDecimal.valueOf(0.3)));
		Category javaObject = JSON.toJavaObject(calculatePrice, Category.class);
		express.setCategory(javaObject);

		Long pk = redisCache.getPK("express", 1);
		express.setExpressNo(pk.toString());

		if(App.ORDER_TYPE_AFTER.equals(express.getType())){
			express.setStatus(App.ORDER_SENDING);
		}else{
			express.setStatus(App.ORDER_BEGIN);
		}
		express.setSubStatus(App.ORDER_PRE_PRICED);
		express.setCreateTime(Calendar.getInstance().getTime());
	    express.setDueTime(DateUtils.getDateInHour());
		expressRepository.save(express);

		if(App.ORDER_TYPE_AFTER.equals(express.getType())){
			return express;
		}

		sendExpressLog21003(express);
		return express;
	}

	/**
	 * 配送员后录单加单
	 * @param express
	 * @return
	 */
	public Express initAfterExpress(Express express){
		JSONObject calculatePrice = HttpUtil.calculatePrice((JSONObject) JSONObject.toJSON(express.getCategory()));
		calculatePrice.put("serviceUser",express.getLines().get(0).getExecutorUser());

		Category javaObject = JSON.toJavaObject(calculatePrice, Category.class);
		express.setCategory(javaObject);

		Long pk = redisCache.getPK("express", 1);
		express.setExpressNo(pk.toString());
		express.setStatus(App.ORDER_SENDING);
		express.setSubStatus(App.ORDER_PRE_PRICED);
		express.setCreateTime(Calendar.getInstance().getTime());
		expressRepository.save(express);

		return express;
	}

	private void sendExpressLog21003(final Express express) {
		// TODO Auto-generated method stub
		Thread thread = new Thread() {
			public void run() {
				JSONObject param = new JSONObject();
				Line currentLine = LineUtil.getLine(express.getLines(), express.getCurrentLine());
				if (currentLine == null) {
					return;
				}
				param.put("creatorId", currentLine.getExecutorUser().getId());
				param.put("createTime", DateUtils.convertToUtcTime(Calendar.getInstance().getTime()));
				param.put("type", "21003");
				param.put("orderId", express.getExpressNo());
				param.put("orderSum", express.getCategory().getTotalPrice());
				param.put("orderUserType", express.getMode());

				JSONObject caseDetail = new JSONObject();
				caseDetail.put("shopId", express.getShop().getId());
				caseDetail.put("receiver", express.getReceiver());
				caseDetail.put("sender", express.getSender());

				param.put("caseDetail", caseDetail);
				HttpUtil.sendWindDataLog(param);
			}
		};
		thread.start();
	}

	public void sendExpressLog21010(final Express express) {
		Thread thread = new Thread() {
			public void run() {
				JSONObject json = new JSONObject();
				json.put("type", "21010");
				json.put("createTime", DateUtils.convertToUtcTime(Calendar.getInstance().getTime()));
				json.put("shopId", express.getShop().getId());
				List<JSONObject> caseDetailList = new ArrayList<>();
				JSONObject caseDetail = new JSONObject();
				caseDetail.put("orderId", express.getExpressNo());
				caseDetail.put("shopId", express.getShop().getId());
				caseDetail.put("shopName", express.getShop().getName());
				caseDetail.put("shopTel", express.getShop().getTel());
				caseDetail.put("receiver", express.getReceiver());
				caseDetail.put("orderUserType", express.getMode());
				caseDetail.put("dueTime", DateUtils.convertToUtcTime(express.getDueTime()));
				caseDetail.put("sender", express.getSender());
				caseDetailList.add(caseDetail);
				json.put("caseDetail", caseDetailList);
				HttpUtil.sendWindDataLog(json);
			}
		};
		thread.start();

	}

	/**
	 * 发送妥投验证码
	 *
	 * @param expressNo 订单号
	 * @return
	 */
    public JSONObject sendCode(String expressNo) {
       String cacheCode = redisCache.getString(App.RDKEY_VERIFY_CODE+expressNo);
    	if(cacheCode != null){
    		return JSONFactory.getSuccessJSON("不要重复发送验证码");
		}
        Express express = selectByNo(expressNo);
        if (express == null) {
			return JSONFactory.getErrorJSON("查找不到该订单信息");
		}
        String code = CodeUtils.genSimpleCode(4);
        String content = "您的妥投验证码为:" + code + ".签收前请检查货物是否损坏.";
        Collection<String> userIds = new HashSet<>();
        userIds.add(express.getShop().getId());
        HttpUtil.sendSMSToUserId(content, userIds);
		redisCache.set(App.RDKEY_VERIFY_CODE + expressNo,900,code);
        ExpressCodeLog expressCodeLog = new ExpressCodeLog(expressNo, new Date(),
                ExpressCodeLog.TypeConstant.TYPE_SEND, code);
        expressCodeLogRepository.save(expressCodeLog);
		return JSONFactory.getSuccessJSON("发送成功");
    }

	/**
	 * 验证码妥投
	 *
	 * @param expressNo 订单号
	 * @param verifyCode 验证码
	 * @param userInfo 用户信息
	 * @return
	 */
	public JSONObject completeByCode(String expressNo, String verifyCode, JSONObject userInfo) {
		String code = redisCache.getString(App.RDKEY_VERIFY_CODE+expressNo);
		if (!verifyCode.equals(code)) {
			return JSONFactory.getErrorJSON("验证码错误");
		}
		redisCache.delete(App.RDKEY_VERIFY_CODE+expressNo);
	    JSONObject jsonObject =	completeExpress(expressNo, null, userInfo);
		ExpressCodeLog expressCodeLog = new ExpressCodeLog(expressNo, new Date(),
				ExpressCodeLog.TypeConstant.TYPE_VERIFY, code);
		expressCodeLogRepository.save(expressCodeLog);
		return jsonObject;
	}


    public Express selectByExpress(Express express) {
		Example<Express> example = Example.of(express);
		return expressRepository.findOne(example);
	}

	public void sendExpressLog21010(final List<Express> express) {
		if (express == null || express.size() < 1)
			return;
		Thread thread = new Thread() {
			public void run() {
				JSONObject json = new JSONObject();
				json.put("type", "21010");
				json.put("createTime", DateUtils.convertToUtcTime(Calendar.getInstance().getTime()));
				json.put("shopId", express.get(0).getShop().getId());
				Iterator<Express> iterator = express.iterator();
				List<JSONObject> caseDetailList = new ArrayList<>();
				while (iterator.hasNext()) {
					Express next = iterator.next();
					if(!next.getStatus().equals(App.ORDER_BEGIN))continue;
					JSONObject caseDetail = new JSONObject();
					caseDetail.put("orderId", next.getExpressNo());
					caseDetail.put("shopId", next.getShop().getId());
					caseDetail.put("shopName", next.getShop().getName());
					caseDetail.put("shopTel", next.getShop().getTel());
					caseDetail.put("receiver", next.getReceiver());
					caseDetail.put("orderUserType", next.getMode());
					caseDetail.put("dueTime", DateUtils.convertToUtcTime(next.getDueTime()));
					caseDetail.put("sender", next.getSender());
					caseDetailList.add(caseDetail);
				}
				json.put("caseDetail", caseDetailList);
				HttpUtil.sendWindDataLog(json);
			}
		};
		thread.start();
	}

	public Integer updateLineIndex(String expressNo, int addNumber) {
		Express findFirstByExpressNo = expressRepository.findFirstByExpressNo(expressNo);
		return expressDao.updateExpressLineIndex(expressNo, findFirstByExpressNo.getCurrentLine(),
				findFirstByExpressNo.getCurrentLine() + addNumber);
	}

	/**
	 * 绑定的单号也一起查询
	 *
	 * @param no
	 * @return
	 */
	public Express selectByNo(String no) {
		Express express = selectByExpressNo(no);
		if (express == null) {
			return selectByBindExpressNo(no);
		}
		return express;
	}

	public Express selectByExpressNo(String expressNo) {
		Express express = expressRepository.findFirstByExpressNo(expressNo);
		return express;
	}

	public Express selectByBindExpressNo(String expressNo) {
		Express express = expressRepository.findFirstByBindExpressNo(expressNo);
		return express;
	}

	public int updateExpressBindNo(String expressNo, String bindExpressNo) {
		return expressDao.updateExpressBindNo(expressNo, bindExpressNo);
	}

	public List<Express> selectByExpressNo(Collection<String> express) {
		// TODO Auto-generated method stub
		List<Express> list = expressRepository.findByExpressNoIn(express);
		return list;
	}

	public JSONObject updateCategory(String expressNo, Category category) {
		Express firstExpress = expressRepository.findFirstByExpressNo(expressNo);
		if (firstExpress == null) {
			return JSONFactory.getErrorJSON("查无该订单");
		}
		if(!firstExpress.getStatus().equals(App.ORDER_BEGIN)){
			return JSONFactory.getErrorJSON("运单不允许改价！");
		}
		firstExpress.setCategory(category);
		firstExpress.setStatus(App.ORDER_BEGIN);
		firstExpress.setSubStatus(App.ORDER_PRE_PRICED);
		expressDao.updateCategoryAndStatus(firstExpress);
		sendExpressLog21003(firstExpress);
		return JSONFactory.getSuccessJSON();
	}

	public synchronized JSONObject saveExpress(final Express express) {
		Runnable runnable = new Runnable() {
			public void run() {
				Express firstExpress = expressRepository.findFirstByExpressNo(express.getExpressNo());
				if (firstExpress == null) {
					return;
				}
				if (express.getCategory() != null) {
					firstExpress.setCategory(express.getCategory());
				}

				if (StringUtils.isNotBlank(express.getStatus())) {
					firstExpress.setStatus(express.getStatus());
				}

				if (StringUtils.isNotBlank(express.getSubStatus())) {
					firstExpress.setSubStatus(express.getSubStatus());
				}

				if (StringUtils.isNotBlank(express.getMode())) {
					firstExpress.setMode(express.getMode());
				}

				if (StringUtils.isNotBlank(express.getRemark())) {
					firstExpress.setRemark(express.getRemark());
				}

				if (express.getBindExpressNo() != null) {
					firstExpress.setBindExpressNo(express.getBindExpressNo());
				}

				if (express.getCurrentLine() != null) {
					firstExpress.setCurrentLine(express.getCurrentLine());
				}

				if (express.getLines() != null) {
					firstExpress.setLines(express.getLines());
				}

				if (express.getDueTime() != null) {
					firstExpress.setDueTime(express.getDueTime());
				}

				if (express.getReceiver() != null) {
					firstExpress.setReceiver(express.getReceiver());
				}

				if (express.getShop() != null) {
					firstExpress.setShop(express.getShop());
				}

				if (express.getSender() != null) {
					firstExpress.setSender(express.getSender());
				}

				firstExpress.setUpdateTime(Calendar.getInstance().getTime());
				expressRepository.save(firstExpress);
			}
		};
		newSingleThreadExecutor.execute(runnable);
		return JSONFactory.getSuccessJSON();
	}

	public JSONObject errorComplete(List<String> list, JSONObject userInfo) {
		Iterator<String> iterator = list.iterator();
		User user = JSONObject.toJavaObject(userInfo, User.class);
		JSONArray json = new JSONArray();
		while (iterator.hasNext()) {
			String expressNo = iterator.next();
			Express express = expressRepository.findFirstByExpressNo(expressNo);
			List<Line> lines = express.getLines();

			Line line = new Line();
			line.setExecutorUser(user);
			line.setRealTime(Calendar.getInstance().getTime());
			line.setTitle(user.getName() + "异常妥投了订单");
			line.setIndex(lines.size());
			lines.add(line);
			express.setCurrentLine(lines.size());
			express.setLines(lines);
			express.setStatus(App.ORDER_COMPLETE);
			express.setSubStatus(App.ORDER_ERROR_COMPLETE);
			expressDao.updateExpress(express);

			JSONObject tmp = new JSONObject();
			tmp.put("order", expressNo);
			tmp.put("status", "CLOSE");
			tmp.put("sendLog", false);
			tmp.put("des", "妥投");
			json.add(tmp);
		}
		HttpUtil.compileExpressMission(json);
		return JSONFactory.getSuccessJSON();
	}

	public void addLine(String expressNo, List<Line> list) {
		Date beginTime = list.get(0).getPlanTime();
		System.out.println(beginTime);
		expressDao.addLines(expressNo, list);
	}

	public void removeLine(String expressNo, Integer lineIndex) {
		expressDao.removeLine(expressNo, lineIndex);
	}

	public void updateLine(String expressNo, List<Line> list) {
		expressDao.updateLines(expressNo, list);
	}

	public Page<Express> selectByShop(String shopId, Integer pageIndex, Integer pageSize) {
		// TODO Auto-generated method stub
		Sort sort = new Sort(Direction.DESC, "createTime");
		PageRequest pageRequest = new PageRequest(pageIndex, pageSize, sort);
		if (ObjectId.isValid(shopId)) {
			ObjectId objectId = new ObjectId(shopId);
			return expressRepository.findByShopId(objectId, pageRequest);
		}
		return expressRepository.findByShopId(shopId, pageRequest);
	}

	public int udpateExpressStatus(String expressNo, String status, String subStatus) {
		return expressDao.updateStatus(expressNo, status, subStatus);
	}

	public Page<Express> selectAllByExpress(Express express, Integer pageIndex, Integer pageSize) {
		Sort sort = new Sort(Direction.DESC, "createTime");
		PageRequest page = new PageRequest(pageIndex, pageSize, sort);
		Example<Express> example = Example.of(express);
		return expressRepository.findAll(example, page);
	}

	public List<Express> selectAll(String param, String fenceName, String mode, String status, String day, Date dueTime,
			Integer pageIndex, Integer pageSize) {
		Sort sort = new Sort(Direction.DESC, "createTime");
		PageRequest page = new PageRequest(pageIndex, pageSize, sort);
		return expressDao.findExpress(param, fenceName, mode, status, day, dueTime, page);
	}

	public void modifiLine(String expressNo, List<Line> list) {
		Express express = expressRepository.findFirstByExpressNo(expressNo);
		List<Line> lines = express.getLines();
		Line[] newArray = new Line[(lines == null ? 0 : lines.size()) + list.size()];

		if (lines != null) {
			for (Line line : lines) {
				if (line == null)
					continue;
				newArray[line.getIndex() - 1] = line;
			}
		}

		if (list != null) {
			for (Line line : list) {
				newArray[line.getIndex() - 1] = line;
			}
		}

		List<Line> newList = new ArrayList<>();
		for (int i = 0; i < newArray.length; i++) {
			Line line = newArray[i];
			if (line == null)
				continue;
			newList.add(line);
		}

		JSONArray expressMission = HttpUtil.findExpressMission(expressNo);
		Iterator<Object> iterator = expressMission.iterator();
		Integer currentLine = (int) Short.MAX_VALUE;
		while (iterator.hasNext()) {
			JSONObject next = (JSONObject) iterator.next();
			Integer index = next.getInteger("missionNodeIndex");
			if (index < currentLine) {
				currentLine = index;
			}
		}

		expressDao.updateLines(expressNo, newList, currentLine);
	}

	public void updateExpressPlanTime(String expressNo, Date planTime) {
		expressDao.updateExpressPlanEndTime(expressNo, planTime);
	}

	public void updateExpress(Express express) {
		expressDao.updateExpress(express);
	}

	public void cancelExpress(String expressNo) {
		JSONArray json = new JSONArray();
		JSONObject tmp = new JSONObject();
		tmp.put("order", expressNo);
		tmp.put("status", "CLOSE");
		tmp.put("sendLog", false);
		tmp.put("des", "取消订单");
		json.add(tmp);
		HttpUtil.compileExpressMission(json);
		expressDao.updateExpressBindNo(expressNo, "");
		expressDao.updateStatus(expressNo, App.ORDER_CANCEL, App.ORDER_CANCEL);
	}

	public JSONObject completeExpress(List<String> list, JSONObject userInfo) {
		// TODO Auto-generated method stub
		Iterator<String> iterator = list.iterator();
		User user = JSONObject.toJavaObject(userInfo, User.class);

		JSONArray json = new JSONArray();
		while (iterator.hasNext()) {
			String expressNo = iterator.next();
			Express express = expressRepository.findFirstByExpressNo(expressNo);
			List<Line> lines = express.getLines();

			Line line = new Line();
			line.setExecutorUser(user);
			line.setRealTime(Calendar.getInstance().getTime());
			line.setTitle(user.getName() + "妥投了订单");
			line.setIndex(lines.size());
			lines.add(line);
			express.setCurrentLine(lines.size());
			express.setLines(lines);
			express.setStatus(App.ORDER_COMPLETE);
			express.setSubStatus(App.ORDER_COMPLETE);
			expressDao.updateExpress(express);

			JSONObject tmp = new JSONObject();
			tmp.put("order", expressNo);
			tmp.put("status", "COMPLETE");
			tmp.put("sendLog", false);
			tmp.put("des", "妥投");
			json.add(tmp);
		}
		HttpUtil.compileExpressMission(json);

		return JSONFactory.getSuccessJSON();
	}

	public JSONObject completeExpress(String expressNo, Address endAddress, JSONObject userInfo) {
		User user = JSONObject.toJavaObject(userInfo, User.class);

		JSONArray json = new JSONArray();
		Express express = expressRepository.findFirstByExpressNo(expressNo);
		if (express == null)
			return JSONFactory.getErrorJSON("运单不存在");

		JSONObject tmp = new JSONObject();
		tmp.put("order", expressNo);
		tmp.put("status", "COMPLETE");
		tmp.put("sendLog", false);
		tmp.put("des", "妥投");
		json.add(tmp);
		//妥投验证
		boolean isComplete = HttpUtil.compileExpressMission(json);
		if(!isComplete){
			return JSONFactory.getfailJSON("妥投失败，请重新妥投");
		}

		List<Line> lines = express.getLines();
		Line line = new Line();
		line.setExecutorUser(user);
		Date sysDate = Calendar.getInstance().getTime();
		line.setRealTime(sysDate);
		line.setTitle(user.getName() + "妥投了订单");
		line.setIndex(lines.size()+1);
		lines.add(line);

		express.setCurrentLine(lines.size());
		express.setLines(lines);
		express.setStatus(App.ORDER_COMPLETE);
		if (!App.ORDER_TYPE_AFTER.equals(express.getType())){
			express.setSubStatus(App.ORDER_COMPLETE);
		}
		if(endAddress!=null){
			express.setEndAddress(endAddress);
			if(App.ORDER_TYPE_AFTER.equals(express.getType())){
				updateAfterExpressPrice(endAddress, express);
			}
		}
		express.setRealEndTime(sysDate);
		expressDao.updateExpress(express);

        if(express.getSender()!= null) {
            HttpUtil.sendSMSToUserTel("您的包裹已妥投，妥投类型：本人签收，期待您再次使用风先生", express.getSender().getTel());
        }
        if(express.getReceiver()!= null){
            HttpUtil.sendSMSToUserTel("我是配送员" + user.getName() + "，已不辱使命将你最宝贵的物品安全送达！ 期待你为我的全力以赴续满能量", express.getReceiver().getTel());
        }
		return JSONFactory.getSuccessJSON();
	}

	private void updateAfterExpressPrice(Address endAddress, Express express) {
		User sender = express.getSender();
		Integer distance = HttpUtil.getDistance(endAddress.getLat(), endAddress.getLng(), sender.getLat(), sender.getLng());
		JSONObject calculatePrice = HttpUtil.calculatePrice(express.getShop().getId(), express.getCategory().getWeight().intValue(), distance);
		Category category = JSONObject.toJavaObject(calculatePrice, Category.class);
		express.setCategory(category);
	}

	public JSONObject errorComplete(String expressNo, Address endAddress, JSONObject userInfo) {
		User user = JSONObject.toJavaObject(userInfo, User.class);
		JSONArray json = new JSONArray();
		Express express = expressRepository.findFirstByExpressNo(expressNo);
		List<Line> lines = express.getLines();

		Line line = new Line();
		line.setExecutorUser(user);
		Date sysDate = Calendar.getInstance().getTime();
		line.setRealTime(sysDate);
		line.setTitle(user.getName() + "异常妥投了订单");
		line.setIndex(lines.size());
		lines.add(line);
		express.setCurrentLine(lines.size());
		express.setLines(lines);
		express.setStatus(App.ORDER_COMPLETE);
		express.setSubStatus(App.ORDER_ERROR_COMPLETE);
		if(endAddress!=null){
			express.setEndAddress(endAddress);
		}
		express.setRealEndTime(sysDate);
		expressDao.updateExpress(express);

		JSONObject tmp = new JSONObject();
		tmp.put("order", expressNo);
		tmp.put("status", "CLOSE");
		tmp.put("sendLog", false);
		tmp.put("des", "异常妥投");
		json.add(tmp);
		HttpUtil.compileExpressMission(json);
		return JSONFactory.getSuccessJSON();
	}

	private JSONObject findPerson(Order order) {
		Double lat = order.getSender().getLat();
		Double lng = order.getSender().getLng();
		String shopId = order.getShop().getId();
		String mode = order.getCategory().getServiceType().getType();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("lat", lat);
		jsonObject.put("lng", lng);
		jsonObject.put("shopId", shopId);
		jsonObject.put("mode", mode);
		return HttpUtil.findPersion(jsonObject);
	}

	public JSONObject findRelationship(String userId){
		JSONObject jsonObject = JSONFactory.getSuccessJSON();
		List<Express> expresses = expressDao.findRelationship(userId);

		jsonObject.put("size", expresses.size());
		jsonObject.put("content", expresses);
		return jsonObject;
	}

    public JSONObject updateExpressPrinted(List<String> expressList) {

        int count = expressDao.updateExpressPrinted(expressList);
        if (count > 0) {
            return JSONFactory.getSuccessJSON();
        }
        return JSONFactory.getfailJSON("更新失败");
    }

	public List<Express> selectByShopIdAndMode(String id,String tel,String expressNo,Date date,Integer pageIndex, Integer pageSize) {
		Sort sort = new Sort(Direction.DESC, "dueTime").and(new Sort(Direction.DESC,"tel"));
		PageRequest page = new PageRequest(pageIndex,pageSize,sort);
		return expressDao.selectByShopIdAndMode(id,tel,expressNo,date,page);
	}
}
