package com.mrwind.order.controller.web;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.bean.Result;
import com.mrwind.order.entity.Order;
import com.mrwind.order.service.OrderService;

@Controller
@RequestMapping("web/order")
public class WebOrderController {

	Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private OrderService orderService;

	@ResponseBody
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public JSONObject create(@RequestBody JSONObject json) {
		Order order = JSONObject.toJavaObject(json, Order.class);
		JSONObject res = orderService.insert(order);
		
		return res;
	}

	@ResponseBody
	@RequestMapping(value = "/select", method = RequestMethod.GET)
	public Result select(Order order) {
		Order res = orderService.selectByOrder(order);
		return Result.success(res);
	}

	/**@ResponseBody
	@RequestMapping(value = "/excel/create", method = RequestMethod.POST)
	public JSONObject excelCreate(@RequestParam("expressFile") CommonsMultipartFile file)
			throws IllegalStateException, IOException {

		if (file == null) {
			return JSONFactory.getErrorJSON("没有找到您上传的文件");
		}
		String fileFileName = file.getOriginalFilename();
		if (fileFileName == null || !fileFileName.matches("^.+\\.(?i)((xls)|(xlsx))$")) {
			return JSONFactory.getErrorJSON("excel文件格式错误");
		}
		String fileType = "2003";
		if (fileFileName.matches("^.+\\.(?i)(xlsx)$")) {
			fileType = "2007";
		}
		Workbook wb = null;
		if (fileType.equals("2003")) {
			wb = new HSSFWorkbook(file.getInputStream());
		}
		if (fileType.equals("2007")) {
			wb = new XSSFWorkbook(file.getInputStream());
		}

		String sheetName = wb.getSheetName(0);
		Sheet sheet = null;
		if (sheetName == null || "".equals(sheetName)) {
			sheet = wb.getSheetAt(0);
		} else {
			sheet = wb.getSheet(sheetName);
		}

		for (int i =2; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			String bindExpressNo = row.getCell(1).getStringCellValue();
			String receiverUserName = row.getCell(2).getStringCellValue();
			String receiverTel = row.getCell(3).getStringCellValue();
			String receiverAddress = row.getCell(4).getStringCellValue();
			String remark = row.getCell(5).getStringCellValue();
		}
		return JSONFactory.getSuccessJSON();
	}*/

}
