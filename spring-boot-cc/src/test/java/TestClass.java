import java.util.Date;

/**
 * 
 */

/**
 * @author Caleb2109
 *
 */
public class TestClass {
	public static void main(String[] args) throws Exception{
		Date now = new Date();
		
		System.out.println(now.getTime());
		System.out.println(now.getTime() - 1499963640000L);
	}
}
