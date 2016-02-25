/**
 *
 */
package com.hsjawanda.gaeobjectify.tests;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hsjawanda.gaeobjectify.util.UriInfo;
import com.hsjawanda.gaeobjectify.util.UriParser;
import com.hsjawanda.gaeobjectify.util.WebUtil;


/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
public class UtilitiesTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPlaintext2Html() {
		String retVal = WebUtil.plaintext2Html("<p>Arbit string <b>of</b>" + System.lineSeparator()
				+ System.lineSeparator()
				+ "text. <em>What</em> to<br/> <strong>do</strong> with it?");
//		System.out.println(retVal);
		assertEquals("<p>Arbit string <b>of</b></p><p>text. <em>What</em> to<br> "
				+ "<strong>do</strong> with it?</p>", retVal);
	}

	@Test
	public void testAddPassThruParameters() {
		String baseUrl = "/mapping/action";
		String fullUrl = "action/ipp/50/pgnum/2/arbit";
		UriParser parser = UriParser.builder().setHasAction(true).build();
		UriInfo info = parser.parse(fullUrl, false);
		String resultUrl = WebUtil.addPassThruParams(info, baseUrl, "ipp", "pgNum", "arbit");
		assertEquals("/mapping/action/ipp/50/pgNum/2/arbit/", resultUrl);
		// System.out.println("Result: " + resultUrl);
	}

}
