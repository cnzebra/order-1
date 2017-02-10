package com.mrwind.common.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.util.StringUtils;

@SuppressWarnings("rawtypes")
public class CSVUtil {

	public static File createCSVFile(List exportData, LinkedHashMap rowMapper,
            String outPutPath, String filename) {

        File csvFile = null;
        BufferedWriter csvFileOutputStream = null;
        try {
            csvFile = new File(outPutPath + filename + ".csv");
            // csvFile.getParentFile().mkdir();
            File parent = csvFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            csvFile.createNewFile();

            // 读取分隔符","
            csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(csvFile), "gbk"), 1024);
            // 写入文件头部
            for (Iterator propertyIterator = rowMapper.entrySet().iterator(); propertyIterator.hasNext();) {
                java.util.Map.Entry propertyEntry = (java.util.Map.Entry) propertyIterator
                        .next();
                csvFileOutputStream.write("\""
                        + propertyEntry.getValue().toString() + "\"");
                if (propertyIterator.hasNext()) {
                    csvFileOutputStream.write(",");
                }
            }
            csvFileOutputStream.newLine();

           


            // 写入文件内容
            for (Iterator iterator = exportData.iterator(); iterator.hasNext();) {  
               // Object row = (Object) iterator.next();  
            	LinkedHashMap row = (LinkedHashMap) iterator.next();
             
                for (Iterator propertyIterator = row.entrySet().iterator(); propertyIterator.hasNext();) {  
                    java.util.Map.Entry propertyEntry = (java.util.Map.Entry) propertyIterator.next();  
                   // System.out.println( BeanUtils.getProperty(row, propertyEntry.getKey().toString()));
                    csvFileOutputStream.write("\""  
                            +  propertyEntry.getValue().toString() + "\"");  
                   if (propertyIterator.hasNext()) {  
                       csvFileOutputStream.write(",");  
                    }  
               }  
                if (iterator.hasNext()) {  
                   csvFileOutputStream.newLine();  
                }  
           }  
            csvFileOutputStream.flush();  
        } catch (Exception e) {  
           e.printStackTrace();  
        } finally {  
           try {  
                csvFileOutputStream.close();  
            } catch (IOException e) {  
               e.printStackTrace();
           }  
       }  
        return csvFile;
    }
	
	/**
	 * 导出csv文件
	 * 
	 * @param outPath 输出路径,文件不存在会自动创建(D://down/)
	 * @param fileName 文件名称
	 * @param fileTitle 文件标题(表头)
	 * @param dataList 数据(格式："债券名称,平台名称,债权类型")
	 * @return
	 */
    public static boolean exportCsvFile(String outPath, String fileName, String fileTitle, List<String> dataList){
        boolean isSucess=false;
        
        FileOutputStream out=null;
        OutputStreamWriter osw=null;
        BufferedWriter bw=null;
        try {
        	// 创建文件
        	File outFile = new File(outPath + fileName + ".csv");
        	File parent = outFile.getParentFile();
        	if (parent != null && !parent.exists()) {
                  parent.mkdirs();
        	}
        	outFile.createNewFile();
        	
        	// 读取并写入数据
            out = new FileOutputStream(outFile);
            osw = new OutputStreamWriter(out, "gbk");
            bw = new BufferedWriter(osw);
            if(!StringUtils.isEmpty(fileTitle)) {
            	bw.append(fileTitle).append("\r");
            }
            if(dataList!=null && !dataList.isEmpty()){
                for(String data : dataList){
                  bw.append(data).append("\r");
                }
            }
            isSucess=true;
        } catch (Exception e) {
            isSucess=false;
        }finally{
        	try {
	            if(bw != null){
                    bw.close();
                    bw=null;
	            }
	            if(osw != null){
                    osw.close();
                    osw=null;
	            }
	            if(out != null){
                    out.close();
                    out=null;
	            }
        	} catch (IOException e) {
        		e.printStackTrace();
        	} 
        }
        
        return isSucess;
    }
	
}
