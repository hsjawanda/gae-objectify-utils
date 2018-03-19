/**
 *
 */
package com.hsjawanda.gaeobjectify.tasks;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.isNotBlank;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.appengine.api.taskqueue.Queue;
import com.hsjawanda.gaeobjectify.util.Constants;
import com.hsjawanda.gaeobjectify.util.TasksHelper;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public abstract class BaseTask {

	protected Map<String, String> stringParams = new HashMap<>();

	protected Map<String, byte[]> byteParams = new HashMap<>();

	protected boolean finalized = false;

	protected final String finalizedErrMsg = "Once addToQueue() has been called, this Task can't"
			+ " be modified or re-used.";

	/**
	 * Add a {@code String} parameter to pass to the Task.
	 *
	 * @param key
	 *            the {@code key} to set the value for.
	 * @param value
	 *            the {@code value} to set. Remove the data already set for this {@code key} if
	 *            {@code value} is {@code null}.
	 */
	public void param(String key, String value) {
		checkArgument(isNotBlank(key), "key" + Constants.NOT_BLANK);
		checkState(!this.finalized, this.finalizedErrMsg);
		if (null == value) {
			this.stringParams.remove(key);
		} else {
			this.stringParams.put(key, value);
		}
	}

	/**
	 * Add a {@code byte[]} parameter to pass to the Task.
	 *
	 * @param key
	 *            the {@code key} to set the value for.
	 * @param value
	 *            the {@code value} to set. Remove the data already set for this {@code key} if
	 *            {@code value} is {@code null}.
	 */
	public void param(String key, byte[] value) {
		checkArgument(isNotBlank(key), "key" + Constants.NOT_BLANK);
		checkState(!this.finalized, this.finalizedErrMsg);
		if (null == value) {
			this.byteParams.remove(key);
		} else {
			this.byteParams.put(key, value);
		}
	}

	protected void addToQueue(@Nonnull Queue queueName, @Nonnull String taskName,
			long delayMillis) {
		this.finalized = true;
		TasksHelper.addToQueue(queueName, taskName, Collections.unmodifiableMap(this.stringParams),
				Collections.unmodifiableMap(this.byteParams), delayMillis);
	}

}
