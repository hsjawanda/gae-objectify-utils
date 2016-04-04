package com.hsjawanda.gaeobjectify.tests;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;
import com.hsjawanda.gaeobjectify.util.PositionalUriInfo;
import com.hsjawanda.gaeobjectify.util.UriInfo;
import com.hsjawanda.gaeobjectify.util.UriParser;
import com.hsjawanda.gaeobjectify.util.UriPositionalParser;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class UriParserTests {

	UriParser actionParser = UriParser.instance("/", true);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testParseNullUri() {
		UriInfo info = this.actionParser.parse((String) null, false);
		assertNotNull("The UriInfo object was unexpectedly null.", info);
	}

	@Test
	public void testParseEmptyUri() {
		UriInfo info = this.actionParser.parse(EMPTY, false);
		assertNotNull("The UriInfo object was unexpectedly null.", info);
	}

	@Test
	public void testParseEmptyAction() {
		UriInfo info = this.actionParser.parse("", false);
		assertNotNull("The UriInfo object was unexpectedly null.", info);
		assertEquals(EMPTY, info.action);
	}

	@Test
	public void testParseWithTrailingSlash() {
		UriInfo info = this.actionParser.parse("/action/", false);
		assertNotNull("The UriInfo object was unexpectedly null.", info);
		assertEquals("action", info.action);
	}

	@Test
	public void testParseWithoutTrailingSlash() {
		UriInfo info = this.actionParser.parse("/action", false);
		assertNotNull("The UriInfo object was unexpectedly null.", info);
		assertEquals("action", info.action);
	}

	@Test
	public void testParseMixedCaseAction() {
		UriInfo info = this.actionParser.parse("/ACTion", false);
		assertNotNull("The UriInfo object was unexpectedly null.", info);
		assertEquals("ACTion", info.action);
	}

	@Test
	public void testParseMixedCaseOneKeyValue() {
		UriInfo info = this.actionParser.parse("/ACTion/Key1/ValuE1", false);
		assertNotNull("The UriInfo object was unexpectedly null.", info);
		assertTrue(info.containsParam("key1"));
		assertEquals("ValuE1", info.getParam("key1").orNull());
	}

	@Test
	public void testParseMixedCaseTwoKeyValue() {
		UriInfo info = this.actionParser.parse("/ACTion/Key1/ValuE1/kEY2/VALUE2", false);
		assertNotNull("The UriInfo object was unexpectedly null.", info);
		assertTrue(info.containsParam("key1"));
		assertTrue(info.containsParam("key2"));
		assertFalse(info.containsParam(null));
		assertEquals("VALUE2", info.getParam("key2").orNull());
	}

	@Test
	public void testParseMissingLastValue() {
		UriInfo info = this.actionParser.parse("/ACTion/Key1/ValuE1/kEY2", false);
		assertNotNull("The UriInfo object was unexpectedly null.", info);
		assertTrue(info.containsParam("key1"));
		assertTrue(info.containsParam("key2"));
		assertEquals(EMPTY, info.getParam("key2").orNull());
	}

	@Test
	public void testParsePositionalUri() {
		UriPositionalParser positionalParser = UriPositionalParser
				.instance("/photos/{profile}/{photoId}");
		Optional<PositionalUriInfo> info = positionalParser.parse("/photos/hsjawanda/lfauouekllfl",
				false);
		assertTrue(info.isPresent());
		assertEquals("hsjawanda", info.get().getParam("profile").get());
		assertEquals("lfauouekllfl", info.get().getParam("photoId").get());
	}

	@Test
	public void testParsePositionalUriMismatch() {
		UriPositionalParser positionalParser = UriPositionalParser
				.instance("/photos/{profile}/{photoId}");
		Optional<PositionalUriInfo> info = positionalParser.parse("/photosa/hsjawanda/lfauouekllfl",
				false);
		assertFalse(info.isPresent());
	}

}
