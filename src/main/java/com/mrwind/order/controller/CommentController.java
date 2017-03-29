package com.mrwind.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.factory.JSONFactory;
import com.mrwind.order.entity.Comment;
import com.mrwind.order.service.CommentService;

@Controller
@RequestMapping("comment")
public class CommentController {

	
	@Autowired
	CommentService commentService;
	
	@ResponseBody
	@RequestMapping(value = "/select", method = RequestMethod.GET)
	public JSONObject select(Integer pageIndex,Integer pageSize) {
		PageRequest pageRequest = new PageRequest(pageIndex-1, pageSize,new Sort(Sort.Direction.DESC, "createTime"));
		List<Comment> all = commentService.findAll(pageRequest);
		JSONObject successJSON = JSONFactory.getSuccessJSON();
		successJSON.put("content", all);
		return successJSON;
	}
	
	@ResponseBody
	@RequestMapping(value = "/insert", method = RequestMethod.PUT)
	public JSONObject insert(@RequestBody JSONObject json) {
		Comment comment = JSON.toJavaObject(json, Comment.class);
		Comment res=commentService.insertCommnet(comment);
		if(res==null){
			return JSONFactory.getErrorJSON("评价失败");
		}
		return JSONFactory.getSuccessJSON("评价成功！");
	}
}
