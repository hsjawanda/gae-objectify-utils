/**
 *
 */
package com.hsjawanda.gaeobjectify.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
@Data
public abstract class DatedEntity implements GaeEntity {

	@JsonIgnore
	@Index
	@Setter(AccessLevel.NONE)
	protected Date dateCreated;

	@JsonIgnore
	@Index
	@Setter(AccessLevel.NONE)
	protected Date dateLastModified;

	@OnSave
	protected void saveActions() {
		this.dateLastModified = new Date();
		if (this.dateCreated == null) {
			this.dateCreated = this.dateLastModified;
		}
	}

}
