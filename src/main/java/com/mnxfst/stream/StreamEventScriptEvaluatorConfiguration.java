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
package com.mnxfst.stream;

import java.io.Serializable;

/**
 * Common interface to all settings used to initialize derived instances of type {@link AbstractStreamEventScriptEvaluator}   
 * @author mnxfst
 * @since 03.02.2014
 */
public interface StreamEventScriptEvaluatorConfiguration extends Serializable {

	/** returns the unique component identifier */
	public String getIdentifier();
	/** set the unique component identifier */
	public void setIdentifier(String identifier);
	/** returns the script to evaluate */
	public String getScript();
	/** set the script to evaluate */
	public void setScript(String script);
	/** returns the unique identifier of the error handler */
	public String getErrorHandlerId();
	/** sets the unique identifier of the error handler */
	public void setErrorHandlerId(String errorHandlerId);

}
