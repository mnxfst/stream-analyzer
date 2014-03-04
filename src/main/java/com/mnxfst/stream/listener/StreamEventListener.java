/**
 *  Copyright 2014 Christian Kreutzfeldt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.mnxfst.stream.listener;


/**
 * Common interface to be implemented by everyone listening to event streams
 * being forwarded into the analyzer
 * @author mnxfst
 * @since 28.02.2014
 *
 */
public interface StreamEventListener extends Runnable {

	/** 
	 * Returns the unique identifier of the listener
	 * @return
	 */
	public String getId();
	
	/**
	 * Returns the unique name of the listener
	 * @return
	 */
	public String getName();
	
	/**
	 * Returns the description of the listener
	 * @return
	 */
	public String getDescription();
	
	/**
	 * Returns the listener version
	 * @return
	 */
	public String getVersion();
}
