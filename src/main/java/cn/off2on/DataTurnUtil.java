package cn.off2on;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;


public class DataTurnUtil {
	
	public static String inputFilePath = "";
	
	public static String outPutFilePath = "";
	
	public static String fileDirectory = "";
	
	public static Map<String, String []> filePathMap = new HashMap<String, String []>();
	
	public static Map<String, Map<String, String>> cfMap = new ConcurrentHashMap<String, Map<String, String>>();
	
	public static String type = "";
	
	public static BiFunction<String, String, String> keyBiFunction = null;
	
	public static BiFunction<String, String, String[]> valBiFunction = null;
	
	public static TriFunction<String, String, String[], StringBuffer> strBufferBiFunction = null;
	
	public static StringBuffer fileStrBuffer = new StringBuffer("");
	
	public static StringBuffer subStrBuffer = new StringBuffer("");
	
	public static StringBuffer duplicateStrBuffer = new StringBuffer("");
	
	/** 变压器类型转换*/
	public static Function<String, String> transformerfun = null;
	
	public static Function<String, Double> transformerfun2 = null;
	
	/** L2数据多余项*/
	public static List<String> lfList = new ArrayList<String>();
	
	 @FunctionalInterface
		interface TriFunction<T, U, V, R> {
			R apply (T t, U u, V v);
	}
	 
	static {
		
		fileDirectory = "test_Demonstrate";
		fileDirectory = "test_GDoff";
//		fileDirectory = "test_takeof_ml4";
		filePathMap.put("test_Demonstrate", new String[] {"gd_20181219_1700.QS", "data"});
		filePathMap.put("test_GDoff", new String[] {"GD.QS", "GDoff", "output"});
		filePathMap.put("test_takeof_ml4", new String[] {"国调_20190420_0100.QS", "input", "ls"});
		inputFilePath = "testdata"+ File.separator + fileDirectory + File.separator;
		outPutFilePath = "testdata" + File.separator + fileDirectory + File.separator +"export" + File.separator;
		
		
		keyBiFunction = (fileName, line) -> {
			String[] arr = line.split(",");
			if (fileName.equals("LF.L2")) {
				String lineName = arr[17].replace("'", "").trim();
				//从映射文件找出对应在线名称 L2线路名称包括交流线、串补、并补
				String type = "";
				if (lineName.contains("_ac")) {
					type = "ln";
//					lineName = lineName.substring(0, lineName.lastIndexOf("_"));
				} else if (lineName.contains("_sc")) {
					type = "scap";
//					lineName = lineName.substring(0, lineName.lastIndexOf("_"));
				} else {
					type = "pcap";
				}
				String idName = cfMap.get(type).get(lineName);
				if (idName == null || idName.equals("")) {
//					if (type.equals("ln")) {
//						idName = lineName + "_ac";
//					} else if (type.equals("scap")) {
//						idName = lineName + "_sc";
//					} else {
//						idName = lineName;
//					}
//					idName = lineName;
					idName = "";
					lfList.add(lineName);
				} 
				return idName;
			} else if (fileName.equals("LF.L3")) {
				String transName = arr[24].replace("'", "").trim();
				Double ty = transformerfun2.apply(arr[24].replace("'", "").trim());
				String type = "";
				if (ty == 31D || ty == 32D || ty == 33D ) {
					type = "t3w";
				} else {
					type = "t2w";
				}
				String idName = cfMap.get(type).get(transName);
				if (idName == null || idName.equals("")) {
					lfList.add(transName);
//					idName = transName;
					idName = "";
				} 
				return idName;
			} else if (fileName.equals("LF.L5")) {
				String unitName = arr[18].replace("'", "").trim();
				String idName = cfMap.get("un").get(unitName);
				if (idName == null || idName.equals("")) {
					lfList.add(unitName);
//					idName = unitName;
					idName = "";
				} 
				return idName;
			} else if (fileName.equals("LF.L6")) {
				String loadName = arr[18].replace("'", "").trim();
				String idName = cfMap.get("ld").get(loadName);
				if (idName == null || idName.equals("")) {
					lfList.add(loadName);
//					idName = loadName;
					idName = "";
				} 
				return idName;
			}  else if (fileName.equals("LF.NL4")) {
				String dcLineName = arr[5].replace("'", "").trim();
				String idName = cfMap.get("dln").get(dcLineName);
				if (idName == null || idName.equals("")) {
					lfList.add(dcLineName);
//					idName = dcLineName;
					idName = "";
				} 
				return idName;
			} else if (fileName.equals("ST.S2")) {
				String lineName = arr[7].replace("'", "").trim();
				//从映射文件找出对应在线名称 L2线路名称包括交流线、串补、并补
				String type = "";
				if (lineName.contains("_ac")) {
					type = "ln";
//					lineName = lineName.substring(0, lineName.lastIndexOf("_"));
				} else if (lineName.contains("_sc")) {
					type = "scap";
//					lineName = lineName.substring(0,lineName.lastIndexOf("_"));
				} else {
					type = "pcap";
				}
				
				String idName = cfMap.get(type).get(lineName);
				if (idName == null || idName.equals("")) {
//					if (type.equals("ln")) {
//						idName = lineName + "_ac";
//					} else if (type.equals("scap")) {
//						idName = lineName + "_sc";
//					} else {
//						idName = lineName;
//					}
					lfList.add(lineName);
//					idName = lineName;
					idName = "";
				} 
				return idName;
			} else if (fileName.equals("ST.S3")) {
				String transName = arr[9].replace("'", "").trim();
				//获取变压器类型
				Double ty = transformerfun2.apply(transName);
				String type = "";
				if (ty == 31D || ty == 32D || ty == 33D ) {
					type = "t3w";
				} else {
					type = "t2w";
				}
				
				String transNameTemp = transName.substring(0, transName.lastIndexOf("_"));
				String idName = cfMap.get(type).get(transNameTemp);
				if (idName == null || idName.equals("")) {
					lfList.add(transName);
//					idName = transName;
					idName = "";
				} else {
					if (ty == 31D) {
						idName = idName + "_3w1";
					} else if (ty == 32D) {
						idName = idName + "_3w2";
					} else if (ty == 33D) {
						idName = idName + "_3w3";
					} else {
						idName = idName + "_2w";
					}
				}
				return idName;
			} else if (fileName.equals("ST.NS4")) {
				String dcLineName = arr[5].replace("'", "").trim();
				String idName = cfMap.get("dln").get(dcLineName);
				if (idName == null || idName.equals("")) {
					lfList.add(dcLineName);
//					idName = dcLineName;
					idName = "";
				} 
				return idName;
			} else if (fileName.equals("ST.S5")) {
				String unitName = arr[16].replace("'", "").trim();
				String idName = cfMap.get("un").get(unitName);
				if (idName == null || idName.equals("")) {
					lfList.add(unitName);
//					idName = unitName;
					idName = "";
				} 
				return idName;
			} else if (fileName.equals("ST.S6")) {
				String loadName = arr[6].replace("'", "").trim();
				String idName = cfMap.get("ld").get(loadName);
				if (idName == null || idName.equals("")) {
					lfList.add(loadName);
//					idName = loadName;
					idName = "";
				} 
				return idName;
			}  else {
				return "";
			}
		};
		
		valBiFunction = (fileName, line) -> {
			String[] arr = line.split(",");
			return arr;
		};
		
		strBufferBiFunction = (fileName, name, strArr) -> {
			StringBuffer strBuffer = new StringBuffer("");
			if (fileName.equals("LF.L2")) {
				strArr[17] = "'" + name + "'";
				for (int i = 0; i < strArr.length; i++) {
					strBuffer.append(strArr[i]+",");
				}
				return strBuffer;
			} else if (fileName.equals("LF.L3")) {
				strArr[24] = "'" + name + "'";
				for (int i = 0; i < strArr.length; i++) {
					strBuffer.append(strArr[i]+",");
				}
				return strBuffer;
			} else if (fileName.equals("LF.L5")) {
				strArr[18] = "'" + name + "'";
				for (int i = 0; i < strArr.length; i++) {
					strBuffer.append(strArr[i]+",");
				}
				return strBuffer;
			} else if (fileName.equals("LF.L6")) {
				strArr[18] = "'" + name + "'";
				for (int i = 0; i < strArr.length; i++) {
					strBuffer.append(strArr[i]+",");
				}
				return strBuffer;
			}  else if (fileName.equals("LF.NL4")) {
				strArr[5] = "'" + name + "'";
				for (int i = 0; i < strArr.length; i++) {
					strBuffer.append(strArr[i]+",");
					if (i == 5 || i == 15 || i == 16 || i == 27 || i == 38 || i == 41 || i == 47) {
						strBuffer.append("\n");
					}
				}
				return strBuffer;
			} else if (fileName.equals("ST.S2")) {
				strArr[7] = "'" + name + "'";
				for (int i = 0; i < strArr.length; i++) {
					strBuffer.append(strArr[i]+",");
				}
				return strBuffer;
			} else if (fileName.equals("ST.S3")) {
				strArr[9] = "'" + name + "'";
				for (int i = 0; i < strArr.length; i++) {
					strBuffer.append(strArr[i]+",");
				}
				return strBuffer;
			} else if (fileName.equals("ST.NS4")) {
				strArr[5] = "'" + name + "'";
				for (int i = 0; i < strArr.length; i++) {
					strBuffer.append(strArr[i]+",");
					if (i == 5 || i == 11 || i == 23 || i == 32) {
						strBuffer.append("\n");
					}
				}
				return strBuffer;
			} else if (fileName.equals("ST.S5")) {
				strArr[16] = "'" + name + "'";
				for (int i = 0; i < strArr.length; i++) {
					strBuffer.append(strArr[i]+",");
				}
				return strBuffer;
			} else if (fileName.equals("ST.S6")) {
				strArr[6] = "'" + name + "'";
				for (int i = 0; i < strArr.length; i++) {
					strBuffer.append(strArr[i]+",");
				}
				return strBuffer;
			} else {
				return new StringBuffer("");
			}
		};
		
		transformerfun = type -> {
			String re = "";
			if (type.equals("31")) {
				re = "_3w1";
			} else if (type.equals("32")) {
				re = "_3w2";
			} else if (type.equals("33")) {
				re = "_3w3";
			} else {
				re = "_2w";
			}
			return re;
		};
		
		transformerfun2 = name -> {
			double re = 0D;
			String type = "";
			if (name.lastIndexOf("_") != -1) {
				String ty = name.substring(name.lastIndexOf("_"));
				if (ty.contains("_2w") || ty.contains("_3w")) {
					type = ty;
				} else {
					type = "";
				}
			} else {
				type = "";
			}
			
			if (type.equals("_3w1")) {
				re = 31;
			} else if (type.equals("_3w2")) {
				re = 32;
			} else if (type.equals("_3w3")) {
				re = 33;
			} else {
				re = 2;
			}
			return re;
		};
	}

    public static void main(String []args) {
//        scannerMethod();
//		String cfMapPath = "C:\\Users\\Administrator\\Desktop\\automap";
//		String exportPath = "E:\\export";
//		String sourcePath = "D:\\IDE\\eclipse-oxygen\\workspace2\\AppRepo\\adpt.GZ2.3\\testdata\\test_GDoff\\GDoff";
		DataTurnUtil util = new DataTurnUtil();
		util.scannerMethod();
    }



	public void scannerMethod() {
		Scanner input = new Scanner(System.in);
        System.out.println("请输入ls文件目录路径");
        String sourcePath = input.nextLine();

        System.out.println("请输入映射文件路径");
        String cfMapPath = input.nextLine();
        turnOff2OnMethod(sourcePath, "export", cfMapPath);
		input.close();
	}



    @Test
    public void test () {
//        String cfMapPath = "E:\\CF.MAP";
        String cfMapPath = "C:\\Users\\Administrator\\Desktop\\automap";
//    	  String exportPath = "E:\\export";
        String sourcePath = "D:\\IDE\\eclipse-oxygen\\workspace2\\AppRepo\\adpt.GZ2.3\\testdata\\test_GDoff\\GDoff";
        turnOff2OnMethod(sourcePath, "export", cfMapPath);
    }



	public void turnOff2OnMethod(String sourcePath, String exportPath, String cfMapPath) {
        mappingFilesToCach(cfMapPath);
        Stream<String> of = Stream.of( "LF.L2", "LF.L3", "LF.L5", "LF.L6", "LF.NL4", "ST.S2", "ST.S3", "ST.NS4", "ST.S5", "ST.S6");
        System.out.println("转换开始...");
        of.forEach(fileName -> {
//			System.out.println(fileName);
			readLs(sourcePath, fileName, exportPath );
		});
		try {
			Files.write(Paths.get(exportPath + File.separator +"在线重复映射数据.txt"),duplicateStrBuffer.toString().getBytes());
		} catch (IOException e) {
			
		}
		System.out.println("转换完成！");
		System.exit(0);
	}
    
    /**
     * 读取映射文件
     * @param dir
     */
    public void turnOn2OffMap (String dir) {
		List<String> list;
		try {
			list = Files.readAllLines(Paths.get(dir), Charset.defaultCharset());
			Map<String, String> map = new HashMap<String, String>();
			list.forEach(str -> {
				if (str.startsWith("<!")) {

				} else if (str.startsWith("<") && !str.startsWith("</")) {
					type = str.trim().replace("<", "").replace(">", "");
					if (type.contains("un")) {
						type = "un";
					}
					if (type.contains("st") && !type.equals("Strategy")) {
						type = "st";
					}
				} else if (str.startsWith("</")) {
					HashMap<String, String> tmp = new HashMap<String, String>(map);
					cfMap.put(type, tmp);
					type = "";
					map.clear();
				} else if (str.startsWith("#")) {
					map.put(str.split("\\s+")[3], str.split("\\s+")[2]);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
//		 cfMap.forEach((k,v) -> System.out.println(k+"--"+v));
	}
    
    
   
    
    public void readLs (String sourcePath, String fileName, String newDir) {
    	try {
			List<String> list = Files.readAllLines(Paths.get(sourcePath + File.separator + fileName), Charset.defaultCharset());
			List<String> sortList = new ArrayList<String>();
			Map<String, String[]> mapTem = new HashMap<String, String[]>();
			if (fileName.equals("LF.NL4")) {
				List<String> nl4List = new ArrayList<String>();
				int i = 0;
				String str8 = "";
				for (String str : list) {
					i++;
					str8 = str8 + str;
					if (i % 8 == 0) {
						nl4List.add(str8);
						str8 = "";
					}
				}
				list.clear();
				list.addAll(nl4List);
			}
			
			if (fileName.equals("ST.NS4")) {
				List<String> nl4List = new ArrayList<String>();
				int i = 0;
				String str8 = "";
				for (String str : list) {
					i++;
					str8 = str8 + str;
					if (i % 5 == 0) {
						nl4List.add(str8);
						str8 = "";
					}
				}
				list.clear();
				list.addAll(nl4List);
			}
			
			list.forEach(str -> {
				String key = keyBiFunction.apply(fileName, str);
				if (key.equals("")) {
					return;
				}
				String [] value = valBiFunction.apply(fileName, str);
				if (mapTem.containsKey(key)) {
					System.err.println(key);
//					System.err.println(str);
					duplicateStrBuffer.append(key + "\n");
				} else {
					mapTem.put(key, value);
				}
			});
			
			mapTem.forEach((k, v) -> {
				StringBuffer sss = strBufferBiFunction.apply(fileName, k, v);
				sortList.add(sss.toString());
			});
//			Arrays.sort(sortList.toArray(),Collator.getInstance(Locale.CHINA));
			sortList.sort((a, b) -> a.compareTo(b));
			sortList.forEach(str -> fileStrBuffer.append(str+"\n"));
			
			File file = new File(newDir);
			if (!file.exists()) {
				file.mkdirs();
			}
			lfList.sort((a, b) -> a.compareTo(b));
			lfList.forEach(str -> subStrBuffer.append(str +"\n"));
			
			Files.write(Paths.get(newDir + File.separator +fileName),fileStrBuffer.toString().getBytes());
			Files.write(Paths.get(newDir + File.separator +fileName+"bak"),subStrBuffer.toString().getBytes());
			fileStrBuffer.setLength(0);
			subStrBuffer.setLength(0);
			lfList.clear();
		} catch (IOException e) {
			System.err.println(fileName + " 文件未找到！");
		}
    }
    
    /**
     * 
     * @param path
     */
    public void mappingFilesToCach (String path){
    	try {
			File[] files = new File(path).listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					continue;
				}
				if (files[i].getName().equals("st.txt")) {
					continue;
				}
				try {
//					System.out.println(files[i].getName());
					readMappingFileToCach(path, files[i].getName());
				} catch (IOException e) {
					System.err.println("read file" + files[i].getName() + "exception");
				}
			}
		} catch (Exception e) {
			System.err.println("映射文件路径或格式错误！");
		}
//    	cfMap.forEach((k, v) ->{
//    		System.out.println(k + "---" + v.size());
//    	});
    	
    }
    
    /**
     * 读取映射文件
     * @param dir
     * @throws IOException 
     */
    public void readMappingFileToCach (String path, String fileName) throws IOException {
    	List<String> list = Files.readAllLines(Paths.get(path + File.separator + fileName), Charset.defaultCharset());
		int startPos = 0, endPos = 0;
		for (int i = 0; i < list.size(); i++) {
			String str = list.get(i);
			if (str.startsWith("映射结果")) {
				startPos = i + 1;
			} else if (str.startsWith("未映射")) {
				endPos = i - 1;
				break;
			}
		}
		//获取开始位置、结束位置
//		System.out.println(startPos + "--" + endPos);
		Map<String, String> map = new HashMap<String, String>();
		List<String> listTemp = list.stream().skip(startPos).limit(endPos - startPos).collect(Collectors.toList());
		listTemp.forEach(str -> {
			String[] arr = str.split(":");
			String on = "", off = "";
			if (arr[0].indexOf("(") != -1) {
//				on = arr[0].substring(0, arr[0].indexOf("(")).trim();
				int startPosition = arr[0].indexOf("(");
				int endPosition = arr[0].indexOf(")");
				on = arr[0].substring(0, arr[0].lastIndexOf("(")).trim();
				if (arr[0].substring(startPosition, endPosition + 1).equals("(新)")) {
					int pos = StringUtils.ordinalIndexOf(arr[0], "(", 2);
					on = arr[0].substring(0, pos);
				}
			}
			if (arr[1].indexOf("(") != -1) {
				off = arr[1].substring(0, arr[1].indexOf("(")).trim();
			}
//			System.out.println(on + "--" + off);
			if (map.containsKey(off)) {
//				System.err.println(fileName + "--" + off);
			} else {
				map.put(off, on);
			}
		});
		
//		System.out.println(listTemp.size());
//		System.out.println(map.size());
		String key = fileName.substring(0, fileName.indexOf("."));
		cfMap.put(key, map);
	}
    
}
