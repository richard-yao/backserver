package backserver;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.richard.wechat.util.MyUtil;

/**
 * @author YaoXiansheng
 * @date 2018年1月21日
 * @title TestMain
 * @todo TODO
 */

public class TestMain {

	@Test
	public void testListSub() {
		Integer[] array = {1,2,3,4,5,6,7,8};
		List<Integer> list = Arrays.asList(array);
		if(list.size() > 3) {
			List<Integer> temp = list.subList(0, 3);
			System.out.println(temp.toString());
			System.out.println("-------------");
			List<Integer> remain = list.subList(3, list.size());
			System.out.println(remain.toString());;
		}
	}
	
	@Test
	public void testXmlCdata() {
		String text = "<![CDATA[this is\n a url]]>";
		System.out.println(MyUtil.convertXmlCdata(text));
	}
}
