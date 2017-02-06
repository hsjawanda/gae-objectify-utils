/**
 *
 */
package com.hsjawanda.gaeobjectify.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;
import com.hsjawanda.gaeobjectify.util.PositionalUriInfo;
import com.hsjawanda.gaeobjectify.util.PositionalUriParserV2;

/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
public class PositionalUriParserTests {

	private static final String specifier = "/photos/user/{userId}/{photoId}";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

	@Test
	public void v2ExactParamTest() {
		PositionalUriParserV2 parser = PositionalUriParserV2.instance(specifier);
		Optional<PositionalUriInfo> info = parser.parse("/photoS/uSER/abc/def/", true);
		assertTrue(info.isPresent());
		assertEquals("abc", info.get().getParam("userId").orNull());
		assertEquals("def", info.get().getParam("photoId").orNull());
	}

	@Test
	public void v2NoParamTest() {
		PositionalUriParserV2 parser = PositionalUriParserV2.instance(specifier);
		Optional<PositionalUriInfo> info = parser.parse("/photoS/uSER/", true);
		assertTrue(info.isPresent());
		assertFalse(info.get().getParam("userId").isPresent());
		assertFalse(info.get().getParam("photoId").isPresent());
	}

	@Test
	public void v2OneMissingConstPartTest() {
		PositionalUriParserV2 parser = PositionalUriParserV2.instance(specifier);
		Optional<PositionalUriInfo> info = parser.parse("/photoS/uSE", true);
		assertFalse(info.isPresent());
		info = parser.parse("/photoS/", true);
		assertFalse(info.isPresent());
		info = parser.parse("/photo", true);
		assertFalse(info.isPresent());
	}

	@Test
	public void v2OneLessParamTest() {
		PositionalUriParserV2 parser = PositionalUriParserV2.instance(specifier);
		Optional<PositionalUriInfo> info = parser.parse("/photoS/uSER/abc/", true);
		assertTrue(info.isPresent());
		assertEquals("abc", info.get().getParam("userId").orNull());
		assertFalse(info.get().getParam("photoId").isPresent());
	}

	@Test
	public void v2ExtraParamTest() {
		PositionalUriParserV2 parser = PositionalUriParserV2.instance(specifier);
		Optional<PositionalUriInfo> info = parser.parse("/photoS/uSER/abc/def/ghi?uvw=xyz", true);
		assertTrue(info.isPresent());
		assertEquals("abc", info.get().getParam("userId").orNull());
		assertEquals("def", info.get().getParam("photoId").orNull());
	}

	@Test
	public void v2WithJsessionIdTest() {
		PositionalUriParserV2 parser = PositionalUriParserV2.instance(specifier);
		Optional<PositionalUriInfo> info = parser.parse("/photoS/uSER/abc/def;jSESSionid=ac", true);
		assertTrue(info.isPresent());
		assertEquals("abc", info.get().getParam("userId").orNull());
		assertEquals("def", info.get().getParam("photoId").orNull());
	}

}
