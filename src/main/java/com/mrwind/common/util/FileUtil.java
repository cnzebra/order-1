package com.mrwind.common.util;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件处理工具类
 * 
 * @author lws
 *
 */
public class FileUtil {
	
	/**
	 * 复制指定目录下所有的文件及文件夹
	 * 
	 * @param sourcePath
	 * @param targetPath
	 * @throws IOException
	 */
	public static String copyTargetFile(String sourcePath, String targetPath) throws IOException {
		new File(targetPath).mkdirs();
		
		File[] file = new File(sourcePath).listFiles();
		if(file == null || file.length <= 0) {
			return targetPath;
		}
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) { //复制文件
				copyFile(file[i], new File(targetPath + file[i].getName()));
			}
			if (file[i].isDirectory()) { // 复制目录
				String sourceDir = sourcePath + File.separator + file[i].getName();
				String targetDir = targetPath + File.separator + file[i].getName();
				copyDirectiory(sourceDir, targetDir);
			}
		}
		return targetPath;
	}

	/**
	 * 复制文件
	 * 
	 * @param sourceFile
	 * @param targetFile
	 * @throws IOException
	 */
	private static void copyFile(File sourceFile, File targetFile) throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			outBuff.flush();
		} finally {
			// 关闭流
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}

	/**
	 * 复制指定目录到目标目录
	 * 
	 * @param sourceDir
	 * @param targetDir
	 * @throws IOException
	 */
	public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {
		new File(targetDir).mkdirs();
		// 获取源文件夹当前下的文件或目录
		File[] file = (new File(sourceDir)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				File sourceFile = file[i];
				File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
				copyFile(sourceFile, targetFile);
			}
			if (file[i].isDirectory()) {
				// 准备复制的源文件夹
				String sDir = sourceDir + "/" + file[i].getName();
				// 准备复制的目标文件夹
				String tDir = targetDir + "/" + file[i].getName();
				copyDirectiory(sDir, tDir);
			}
		}
	}

	/**
	 * 删除指定目录下的文件
	 * 
	 * @param filepath
	 * @throws IOException
	 */
	public static void del(String filepath) throws IOException {
		File f = new File(filepath);// 定义文件路径
		if (f.exists() && f.isDirectory()) {// 判断是文件还是目录
			if (f.listFiles().length == 0) {// 若目录下没有文件则直接删除
				f.delete();
			} else {// 若有则把文件放进数组，并判断是否有下级目录
				File delFile[] = f.listFiles();
				int i = f.listFiles().length;
				for (int j = 0; j < i; j++) {
					if (delFile[j].isDirectory()) {
						del(delFile[j].getAbsolutePath());// 递归调用del方法并取得子目录路径
					}
					delFile[j].delete();// 删除文件
				}
			}
		}
	}
	
//	public static boolean doReport(String jrxmlPath,Map parameter,List<Map> lstdatamap,String pdfPath ){
//		JasperReport jasperReport;
//		JasperPrint jasperPrint;
//		try {
//			jasperReport = JasperCompileManager.compileReport(jrxmlPath);
//
//			JREmptyDataSource d = new JREmptyDataSource();
//
//			jasperPrint = JasperFillManager.fillReport(jasperReport, parameter, createReportDataSource(lstdatamap));
//
//			JasperExportManager.exportReportToPdfFile(jasperPrint, pdfPath);
//			return true;
//		} catch (JRException e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
//	private static JRDataSource createReportDataSource(List<Map> lstdatamap) {
//		
//		if(lstdatamap == null || lstdatamap.size() == 0){
//			return new JREmptyDataSource();
//		}else{
//			Map[] reportRows = initializeMapArray(lstdatamap);
//			return new JRMapArrayDataSource(reportRows);
//		}
//	}
//	
//	private static Map[] initializeMapArray(List<Map> lstdatamap) {
//		// 你可以把数组里面的每个map看成一个对象，就相于数据库里面的每个字段		
//		HashMap[] reportRows = (HashMap[]) lstdatamap.toArray();
//		
//		return reportRows;
//	}
	
}
