/**
 *
 */
package com.hsjawanda.gaeobjectify.tests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.hsjawanda.gaeobjectify.util.Constants;
import com.hsjawanda.gaeobjectify.util.Holdall;
import com.hsjawanda.gaeobjectify.util.KeyValueUriInfo;
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
		String retVal = WebUtil.plaintext2Html(
				"<p>Arbit string <b>of</b>" + System.lineSeparator() + System.lineSeparator()
						+ "text. <em>What</em> to<br/> <strong>do</strong> with it?");
//		System.out.println(retVal);
		assertEquals("<p>Arbit string <b>of</b></p><p>text. <em>What</em> to<br> "
				+ "<strong>do</strong> with it?</p>", retVal);
	}

	@Test
	public void testAddPassThruParameters() {
		String baseUrl = "/mapping/action";
		String fullUrl = "action/ipp/50/pgnum/2/arbit";
		UriParser parser = UriParser.instance("/mapping", true);
		KeyValueUriInfo info = parser.parse(fullUrl, false);
		String resultUrl = WebUtil.addPassThruParams(info, baseUrl, "ipp", "pgNum", "arbit");
		assertEquals("/mapping/action/ipp/50/pgNum/2/arbit/", resultUrl);
		// System.out.println("Result: " + resultUrl);
	}

	@Test
	public void testCompactList() {
		List<String> addrList = Lists.newArrayList("S.C.O 37", "Opposite E.S.I Hospital",
				"Cellulosics Road", "Phase 7", "Industrial Area", "Sector 73");
		List<String> unchangingAddrList = Lists.newArrayList(addrList);
//		Holdall.printList(unchangingAddrList);
		Holdall.compactList(addrList, 4);
//		System.out.println(EMPTY);
//		Holdall.printList(addrList);
		assertEquals(Constants.ADDR_JOIN.join(unchangingAddrList),
				Constants.ADDR_JOIN.join(addrList));
	}

}
