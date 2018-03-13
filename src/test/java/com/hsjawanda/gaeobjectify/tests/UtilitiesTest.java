/**
 *
 */
package com.hsjawanda.gaeobjectify.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.hsjawanda.gaeobjectify.models.UniqueIndex;
import com.hsjawanda.gaeobjectify.util.BaseX;
import com.hsjawanda.gaeobjectify.util.Constants;
import com.hsjawanda.gaeobjectify.util.Holdall;
import com.hsjawanda.gaeobjectify.util.KeyValueUriInfo;
import com.hsjawanda.gaeobjectify.util.PrintableTable;
import com.hsjawanda.gaeobjectify.util.SplitJoin;
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
		Holdall.compactList(addrList, 4);
		assertEquals(Constants.ADDR_JOIN.join(unchangingAddrList),
				Constants.ADDR_JOIN.join(addrList));
	}

	@Test
	public void testUniqueIndexIdGen() {
		String id1 = UniqueIndex.genIdFor("  ", " 5");
		assertEquals("5", id1);
		String id2 = UniqueIndex.genIdFor(" Content    Type for", "abc");
		assertEquals("Content Type for" + SplitJoin.token + "abc", id2);
	}

	@Test
	public void testContainInRange() {
		int lowValue = 5, highValue = 25;
		// TODO: also test for case where either or both ends of Range are open
		Range<Integer> allowable = Range.closed(lowValue, highValue);
		int retVal = Holdall.constrainToRange(allowable, lowValue - 1);
		assertTrue(retVal == lowValue);
		retVal = Holdall.constrainToRange(allowable, highValue + 1);
		assertTrue(retVal == highValue);
		retVal = Holdall.constrainToRange(allowable, highValue - 1);
		assertTrue(retVal == highValue - 1);
	}

	@Test
	public void testBaseXEncoding() {
		BaseX defaultBase62 = BaseX.get("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
		BaseX base62 = BaseX.get("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
		assertEquals("qZ", defaultBase62.encode10(1673));
		assertEquals("0", defaultBase62.encode10(0));
		assertEquals("A9", base62.encode10(1673));
		assertEquals("a", base62.encode10(0));
		assertEquals("d0ja", BaseX.URL_SAFE_BASE64.encode10(1_000_000));
		assertEquals("7MSOa", BaseX.URL_SAFE_BASE64.encode10(1_000_000_000));
	}

	@Test
	public void testBaseXDecoding() {
		assertEquals(1_000_000, BaseX.URL_SAFE_BASE64.decode10("d0ja"));
		assertEquals(1_000_000_000, BaseX.URL_SAFE_BASE64.decode10("7MSOa"));
	}

	@Test
	public void testPrintableTable() {
		PrintableTable table = PrintableTable.create().setSeparator(" | ");
		table.add("Harshdeep").add("Singh").add("Jawanda");
		table.startRow().add("Adarsh").add("ArbitrarilyLongMiddleName").add("Sharma");
		table.startRow().add("Gajendra").add(null).add("Singh");
		table.startRow().add("Gajendra").add("Singh");
		for (int i = 0; i < 5; i++) {
			table.startRow().add("Gajendra").add(null).add("Singh");
		}
		System.out.print(table.printable(null));
	}

}
