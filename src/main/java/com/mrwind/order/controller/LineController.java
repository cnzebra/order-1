package com.mrwind.order.controller;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.factory.JSONFactory;
import com.mrwind.order.entity.Express;
import com.mrwind.order.entity.Line;
import com.mrwind.order.entity.Line.LineUtil;
import com.mrwind.order.service.ExpressService;

@Controller
@RequestMapping("express/line")
public class LineController {

	@Autowired
	ExpressService expressService;
	
	@ResponseBody
	@RequestMapping(value = "", method = RequestMethod.GET)
	public JSONObject line(String expressNo) {
		Express resExpress = expressService.selectByExpressNo(expressNo);
		if (resExpress == null) {
			return JSONFactory.getErrorJSON("没有数据");
		}
		JSONObject successJSON = JSONFactory.getSuccessJSON();
		successJSON.put("data", resExpress.getLines());
		return successJSON;
	}

	@ResponseBody
	@RequestMapping(value = "/complete", method = RequestMethod.POST)
	public JSONObject completeLine(@RequestBody JSONObject param) {
		String expressNo = param.getString("expressNo");
		Express express = expressService.updateLineIndex(expressNo,1);
		JSONObject successJSON = JSONFactory.getSuccessJSON();
		successJSON.put("express", express);
		return successJSON;
	}

	@ResponseBody
	@RequestMapping(value = "/add/{expressNo}", method = RequestMethod.PUT)
	public JSONObject addLine(@RequestBody String jsonString, @PathVariable("expressNo") String expressNo) {
		
		List<Line> list = JSON.parseArray(jsonString, Line.class);
		expressService.addLine(expressNo, list);
		return JSONFactory.getSuccessJSON();
	}

	@ResponseBody
	@RequestMapping(value = "/remove", method = RequestMethod.DELETE)
	public JSONObject deleteLine(String expressNo, Integer lineIndex) {
		if (expressNo == null) {
			return JSONFactory.getErrorJSON("参数数据不正确");
		}
		expressService.removeLine(expressNo, lineIndex);
		return JSONFactory.getSuccessJSON();
	}

	/**
	 * 修改轨迹（覆盖）
	 * @param jsonString
	 * @param expressNo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/update/{expressNo}", method = RequestMethod.POST)
	public JSONObject updateLine(@RequestBody JSONObject jsonString, @PathVariable("expressNo") String expressNo) {
		JSONArray jsonArray = jsonString.getJSONArray("predict");
		Iterator<Object> iterator = jsonArray.iterator();
		List<Line> list = new ArrayList<>(jsonArray.size());
		while (iterator.hasNext()) {
			Line line = LineUtil.caseToLine((JSONObject) iterator.next());
			list.add(line.getIndex() - 1, line);
		}
		expressService.updateLine(expressNo, list);
		return JSONFactory.getSuccessJSON();
	}

	/**
	 * 修改轨迹（追加）
	 * @param param
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/modifi", method = RequestMethod.POST)
	public JSONObject modifiLine(@RequestBody JSONObject param) {

		JSONArray jsonArray = param.getJSONArray("lines");
		List<Line> list = jsonArray.toJavaList(Line.class);
		String expressNo = param.getString("expressNo");
		Date planTime = param.getDate("preTime");
		expressService.updateExpressPlanTime(expressNo,planTime);
		
		expressService.modifiLine(expressNo, list);
		return JSONFactory.getSuccessJSON();
	}

	/**
	 * 轨迹替换
	 * @param param
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/replace", method = RequestMethod.POST)
	public JSON replaceLine(@RequestBody final JSONObject param){
		int index = param.getIntValue("startIndex");
		String expressNo = param.getString("expressNo");
		List<Line> lines = new ArrayList<Line>(){{addAll(param.getJSONArray("lines").toJavaList(Line.class));}};

		if (StringUtils.isEmpty(expressNo)){
			return JSONFactory.getErrorJSON("订单号不能为空");
		}
		if (lines == null || lines.size() == 0)
			return JSONFactory.getErrorJSON("轨迹不能为空");
		expressService.replaceLine(index, lines, expressNo);
		return JSONFactory.getSuccessJSON();
	}
}
