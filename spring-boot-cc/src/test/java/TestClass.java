import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cc.bean.WowCharacterProfileItemResponse;
import com.cc.bean.WowCharacterProfileParamBean;
import com.cc.bean.WowCharacterProfileResponse;
import com.cc.enums.WowProfileFieldEnum;
import com.cc.service.IWowCharacterProfileService;
import com.cc.service.impl.WowCharacterProfileServiceImpl;
import com.utils.NudoCCUtil;

/**
 * 
 */

/**
 * @author Caleb.Cheng
 *
 */
public class TestClass {

	public static void main(String[] args) throws Exception{
		WowCharacterProfileServiceImpl service = new WowCharacterProfileServiceImpl();
		WowCharacterProfileParamBean paramBean = new WowCharacterProfileParamBean();
		paramBean.setFields(WowProfileFieldEnum.ITEMS);
		paramBean.setCharacterName("Eatnoodles");
		paramBean.setRealm(NudoCCUtil.DEFAULT_SERVER);
		WowCharacterProfileItemResponse resp = service.doSendItem(paramBean);
		System.out.println(resp.getThumbnail());
	}
	
	@Autowired
	private IWowCharacterProfileService service;
	
    @Test
    public void test() throws Exception{
		WowCharacterProfileParamBean paramBean = new WowCharacterProfileParamBean();
		paramBean.setCharacterName("Eatnoodles");
		paramBean.setRealm(NudoCCUtil.DEFAULT_SERVER);
		WowCharacterProfileResponse resp = service.doSendProfile(paramBean);
		System.out.println(resp.getThumbnail());
    }
}
