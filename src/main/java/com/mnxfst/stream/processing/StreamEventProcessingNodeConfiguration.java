/**
 * Copyright 2014 Christian Kreutzfeldt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.mnxfst.stream.processing;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mnxfst.stream.processing.evaluator.StreamEventScriptEvaluatorConfiguration;
import com.mnxfst.stream.processing.persistence.StreamEventESWriterConfiguration;

/**
 * Common interface to all settings used for setting up {@link AbstractStreamEventProcessingNode stream event processing nodes}
 * @author mnxfst
 * @since 03.02.2014
 */
@JsonRootName ( value = "nodeConfiguration" )
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes ({ @JsonSubTypes.Type(StreamEventScriptEvaluatorConfiguration.class), @JsonSubTypes.Type( StreamEventESWriterConfiguration.class) })
public interface StreamEventProcessingNodeConfiguration extends Serializable {

	/** get processing node class */
	public String getProcessingNodeClass();
	/** set processing node clas */
	public void setProcessingNodeClass(String processingNodeClass);
	/** Returns the unique node identifier */
	public String getIdentifier();
	/** Sets the unique node identifier */
	public void setIdentifier(String identifier);
	/** Returns the description */
	public String getDescription();
	/** sets the description */
	public void setDescription(final String description);
	/** returns the number of node instances made available through a round-robin router */
	public int getNumOfNodeInstances();
	/** sets the number of node instances to be made available through a round-robin router */
	public void setNumOfNodeInstances(final int numOfNodeInstances);

}
