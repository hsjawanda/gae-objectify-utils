/**
 *
 */
package in.co.amebatechnologies.empireapp.util;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author Harsh.Deep
 *
 */
public class UniqueIdGenerator {

	public UniqueIdGenerator() {
	}

	private static SecureRandom random = new SecureRandom();

	public static String next() {
		return new BigInteger(130, random).toString(32);
	}
}
