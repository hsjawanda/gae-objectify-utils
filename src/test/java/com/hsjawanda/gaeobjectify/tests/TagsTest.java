package com.hsjawanda.gaeobjectify.tests;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import com.hsjawanda.gaeobjectify.models.Tag;
import com.hsjawanda.gaeobjectify.models.TagManager;
import com.hsjawanda.gaeobjectify.models.TagStore;


public class TagsTest {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(TagsTest.class.getName());

	@SuppressWarnings("unused")
	private final LocalDatastoreServiceTestConfig datastoreConfig = new LocalDatastoreServiceTestConfig()
			.setDefaultHighRepJobPolicyUnappliedJobPercentage(10);

	private final LocalDatastoreServiceTestConfig dsConfigAllSucceed = new LocalDatastoreServiceTestConfig()
			.setApplyAllHighRepJobPolicy();

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			this.dsConfigAllSucceed);

	Closeable session;

	@Before
	public void setUp() throws Exception {
		this.helper.setUp();
		this.session = ObjectifyService.begin();
		ObjectifyService.register(TagStore.class);
	}

	@After
	public void tearDown() throws Exception {
		if (null != this.session) {
			this.session.close();
		}
		this.helper.tearDown();
		this.session = null;
	}

	@Test
	public void test() {
		TagStore contentTags = TagManager.getStore("Content");
		TagStore offerTags = TagManager.getStore("offers");

		String contentTag1Display = "Arbit   PEOPLE";
		contentTags.addTag("  " + contentTag1Display + "   ");
		contentTags.addTag("Arbit people");
		contentTags.addTag("victory");
		offerTags.addTag("10 pc off");
		ofy().save().entities(contentTags, offerTags).now();

		TagStore fromDbContentTags = TagManager.getStore(" content");
		int counter = 0;
		for (Tag tag : fromDbContentTags.getAllTags()) {
			System.out.println(tag);
			counter++;
		}
		assertEquals(2, counter);
		Tag contentTag1 = contentTags.getTag(contentTag1Display);
		assertNotNull(contentTag1);
		assertEquals(normalizeSpace(contentTag1Display), contentTag1.displayName());
	}

}
