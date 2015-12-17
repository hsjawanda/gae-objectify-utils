/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;


/**
 * @author harsh.deep
 *
 */
public class FMHelper {

	private static final Logger log = Logger.getLogger(FMHelper.class.getName());

	private static File docRoot;

	private static Configuration config;

	private static BeansWrapper wrapper = BeansWrapper.getDefaultInstance();

	private FMHelper() {
	}

	public static void init(String documentRoot) {
		if (null != config)
			return;
		log.info("Initializing FM with documentRoot '" + documentRoot + "'");
		config = new Configuration();
		docRoot = new File(documentRoot);
		try {
			config.setDirectoryForTemplateLoading(docRoot);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Failed to set template directory.", e);
			config = null;
			return;
		}
		// Sets how errors will appear.
		if (WebUtil.isProduction()) {
			config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		} else {
			config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
		}
		config.setDefaultEncoding("UTF-8");
	}

	public static boolean processTemplate(String tmpl, HttpServletResponse res,
			Map<String, Object> args) {
		if (null == config) {
			String msg = "FreeMarker has not been configured properly yet.";
			log.log(Level.SEVERE, msg);
			throw new IllegalStateException(msg);
		}
		Template template = null;
		try {
			template = config.getTemplate(tmpl);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Failed to get template '" + tmpl + "'.", e);
			return false;
		}
		try {
			template.process(args, res.getWriter());
		} catch (TemplateException | IOException e) {
			log.log(Level.WARNING, "Failed to process template '" + tmpl + "'.", e);
			return false;
		}
		return true;
	}

	public static TemplateHashModel getEnum(Class<? extends Enum<?>> clazz) {
		TemplateHashModel enumModels = wrapper.getEnumModels();
		TemplateHashModel roundingModeEnums = null;
		try {
			roundingModeEnums = (TemplateHashModel) enumModels.get(clazz.getName());
		} catch (TemplateModelException e) {
			log.log(Level.WARNING,
					"Failed to get BeansWrapper for class " + clazz.getName() + "...", e);
		}
		return roundingModeEnums;
	}
}
