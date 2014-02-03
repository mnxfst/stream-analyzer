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
import java.util.Map;
import java.util.Set;

/**
 * Common interface to all settings used for setting up {@link AbstractStreamEventProcessingNode stream event processing nodes}
 * @author mnxfst
 * @since 03.02.2014
 */
public interface StreamEventProcessingNodeConfiguration extends Serializable {

	/** Returns the unique node identifier */
	public String getIdentifier();
	/** Sets the unique node identifier */
	public void setIdentifier(String identifier);
	/** returns the error handlers */
	public Map<String, Set<String>> getErrorHandlers();
	/** sets the error handlers */
	public void setErrorHandlers(Map<String, Set<String>> errorHandlers);
	/** Adds a new set of error handlers */
	public void addErrorHandlers(final String errorKey, final Set<String> handlers);

}
