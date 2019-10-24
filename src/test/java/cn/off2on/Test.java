package cn.off2on;

import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public class Test {

	public static void main(String[] args) {
		String aa = "山东.(新)姚家站/10.5kV.#2主变低(10.5, 山东.(新)姚家站.10.35786) : 鲁姚家站110_2287(115, 鲁姚家站110)";
//		aa = "河北.宜安站/110kV.1L3负荷(110, 河北.宜安站.110.29959) : 冀宜安11_917(115, 冀宜安11)";
		String[] arr = aa.split(":");
		int startPos = arr[0].indexOf("(");
		int endPos = arr[0].indexOf(")");
		String on = arr[0].substring(0, arr[0].lastIndexOf("(")).trim();
		String off = arr[1].substring(0, arr[1].indexOf("(")).trim();
		if (arr[0].substring(startPos, endPos + 1).equals("(新)")) {
			int pos = StringUtils.ordinalIndexOf(arr[0], "(", 2);
			on = arr[0].substring(0, pos);
		}
//		System.out.println(arr[0].substring(startPos, endPos + 1));
		System.out.println(on);
		System.out.println(off);
		
		
		Stream.of("a", "", "b").forEach(str -> {
			if (str.equals("")) {
				return;
			}
			System.out.println(str);
		});
		
	}

}
