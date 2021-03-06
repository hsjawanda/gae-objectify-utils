/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.isBlank;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;

import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;
import lombok.NonNull;


/**
 * @author harsh.deep
 *
 */
public class FMHelper {

	private static final Logger		log		= Logger.getLogger(FMHelper.class.getName());

	private static File				docRoot;

	private static Configuration	config;

	private static BeansWrapper		wrapper	= BeansWrapper.getDefaultInstance();

	private FMHelper() {
	}

	public static void init(String documentRoot, boolean debug) {
		if (null != config)
			return;
		if (debug) {
			log.info("Initializing FM with documentRoot '" + documentRoot + "'");
		}
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

	public static void init(String documentRoot) {
		init(documentRoot, false);
	}

	public static Configuration config() {
		return config;
	}

	public static boolean processTemplate(String tmpl, Writer writer, Map<String, Object> args,
			String charEncoding) {
		if (null == config) {
			String msg = "FreeMarker has not been configured properly yet.";
			log.log(Level.SEVERE, msg);
			throw new IllegalStateException(msg);
		}
		if (null == writer)
			return false;
		Template template = null;
//		Template.getPlainTextTemplate(arg0, arg1, arg2, arg3)
		try {
			template = config.getTemplate(tmpl);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Failed to get template '" + tmpl + "'. Stacktrace:", e);
			return false;
		}
		try {
			charEncoding = null == charEncoding ? StandardCharsets.UTF_8.name() : charEncoding;
			Environment env = template.createProcessingEnvironment(args, writer);
			env.setOutputEncoding(charEncoding);
			env.process();
		} catch (TemplateException | IOException e) {
			log.log(Level.WARNING, "Failed to process template '" + tmpl + "'. Stacktrace:", e);
			return false;
		}
		return true;
	}

	public static String processTemplate(@NonNull Template template, Map<String, Object> args,
			int initialCapacity) {
		StringWriter sw = new StringWriter(initialCapacity);
		try {
			Environment env = template.createProcessingEnvironment(args, sw);
			env.setOutputEncoding(Constants.UTF_8);
			env.process();
		} catch (TemplateException | IOException e) {
			log.log(Level.WARNING, "Failed to process template '" + template + "'. Stacktrace:", e);
		}
		return sw.toString();
	}

	public static boolean processTemplate(String tmpl, HttpServletResponse res,
			Map<String, Object> args) {
		PrintWriter pw = null;
		try {
			res.setCharacterEncoding(StandardCharsets.UTF_8.name());
			pw = res.getWriter();
			return processTemplate(tmpl, pw, args, res.getCharacterEncoding());
		} catch (IOException e) {
			log.log(Level.WARNING, "Couldn't get writer from HttpServletResponse. Stacktrace:", e);
			return false;
		}
	}

	public static boolean processTemplate(String tmpl, Writer pw, Map<String, Object> args) {
		return processTemplate(tmpl, pw, args, StandardCharsets.UTF_8.name());
	}

	public static String processTemplate(String tmpl, Map<String, Object> args, int initialCapacity) {
		initialCapacity = Math.max(initialCapacity, 50);
		StringWriter sw = new StringWriter(initialCapacity);
		processTemplate(tmpl, sw, args, null);
		return sw.toString();
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

	@Nullable
	public static Template createTemplate(@Nonnull String templateContent, @Nonnull String templateName)
			throws NullPointerException, IllegalArgumentException {
		Objects.nonNull(templateContent);
		Objects.nonNull(templateName);
		if (isBlank(templateContent))
			throw new IllegalArgumentException("templateContent" + Constants.NOT_BLANK);
		if (isBlank(templateName))
			throw new IllegalArgumentException("templateName" + Constants.NOT_BLANK);
		Template tmpl = null;
		try {
			tmpl = new Template(templateName, new StringReader(templateContent), config());
		} catch (IOException e) {
			log.log(Level.WARNING, "Error creating template from String.", e);
		}
		return tmpl;
	}

}
