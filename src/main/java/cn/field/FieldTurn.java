package cn.field;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.Test;

public class FieldTurn {

	@FunctionalInterface
		interface TriFunction<T, U, V, R> {
			R apply (T t, U u, V v);
	}
	 
	/** e文件列格式字段数组*/
	public Function<String, String[]> filedArray = fileName -> new String[] {appendSpace(fileName)};
	
	public Function<String, Integer> transformerfun = name -> {
		int re = 1;
		if (name.lastIndexOf("_") != -1) {
			String type = name.substring(name.lastIndexOf("_"));
			if (type.equals("_3w1")) {
				re = 31;
			} else if (type.equals("_3w2")) {
				re = 32;
			} else if (type.equals("_3w3")) {
				re = 33;
			} else {
				re = 1;
			}
		};
		return re;
	};
	
	public BiFunction<String, String, Integer> typeBiFunction = (fileName, line) -> {
		String[] arr = line.split(",");
		int re = 1;
		if (fileName.equals("ST.S3")) {
			String transName = arr[9].replace("'", "").trim();
			//获取变压器类型
			re = transformerfun.apply(transName);
		};
		return re;
	};

	public TriFunction<String, String, String[], StringBuffer> strBufferBiFunction = (fileName, value, strArr) -> {
		StringBuffer strBuffer = new StringBuffer("");
		if (fileName.equals("ST.S3")) {
			for (int i = 0; i < strArr.length; i++) {
				if (i == 7) {
					continue;
				}
				if (i == 8) {
					strArr[i] = value;
				}
				strBuffer.append(strArr[i]+",");
			}
		};
		return strBuffer;
	};
	
	public Function<String, String[]> valFunction = (line) -> line.split(",");
	
	
	
	public void fieldTurnMethod (String srcFileName, String descFileName) {
		String[] arr = filedArray.apply(srcFileName);
		for (int i = 0; i < arr.length; i++) {
			System.err.println(arr[i]);
		}
	}
	
	public String appendSpace (String dir){
		StringBuffer strBuffer = new StringBuffer("");
		List<String> list;
		String re = "";
		try {
			list = Files.readAllLines(Paths.get(dir), Charset.forName("gbk"));
			int startPos = 0;
			for (int i = 0; i < list.size(); i++) {
				++i;
				String str = list.get(i);
				if (str.contains("字段")) {
					startPos = i + 1;
					break;
				}
			}
			
			list.stream().skip(startPos).forEach(str -> {
				String field = str.split("\\s")[0];
				if (field.equals("IDName")) {
					strBuffer.append("\'"+field + "\'"+ ", ");
				} else {
					strBuffer.append("\""+field + "\""+ ", ");
				}
				
			});
			re = strBuffer.toString().substring(0, strBuffer.toString().lastIndexOf(","));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return re;
	}
	
	@Test
	public void test () {
		try {
//			fieldTurnMethod("C:\\Users\\Administrator\\Desktop\\国重\\PSASP数据文件格式V2.6(PSASP7.40)20190402(1)\\ST.S5.txt", "");
			String newFilePath = "D:\\IDE\\eclipse-oxygen\\workspace2\\dataTurn\\export";
			String standardFilePath = "D:\\IDE\\eclipse-oxygen\\workspace2\\AppRepo\\adpt.GZ2.3\\testdata\\test_Demonstrate\\data";
			turnDiffLsFile("ST.S3", newFilePath, standardFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void turnDiffLsFile (String fileName, String newFilePath, String standardFilePath) {
		try {
			StringBuffer sbuffer = new StringBuffer("");
			List<String> newlist = Files.readAllLines(Paths.get(newFilePath + File.separator + fileName), Charset.forName("gbk"));
			List<String> standardList = Files.readAllLines(Paths.get(standardFilePath + File.separator + fileName), Charset.forName("gbk"));
			String[] standardFieldArr = newlist.get(1).split(",");
			String[] newFieldArr = standardList.get(1).split(",");
			if (standardFieldArr.length != newFieldArr.length) {
				newlist.stream().forEach(str -> {
					StringBuffer value = strBufferBiFunction.apply(fileName, typeBiFunction.apply(fileName, str).toString(), valFunction.apply(str));
//					System.out.print(value.toString() +"\r\n");
					sbuffer.append(value + "\r\n");
				});
			}
			Files.write(Paths.get(newFilePath + File.separator +fileName),sbuffer.toString().getBytes());
			System.out.println("数据清洗完成！");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
