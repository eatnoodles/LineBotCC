import com.cc.bean.WowCommandBean;
import com.cc.service.impl.NudoCCServiceImpl;

/**
 * 
 */

/**
 * @author Caleb2109
 *
 */
public class TestClass {

	public static void main(String[] args){
		NudoCCServiceImpl service = new NudoCCServiceImpl();
		WowCommandBean bean = service.processWowCommand("-wow -ec Eatnoodles");
		System.out.println(bean.getErrorMsg());
	}
}
