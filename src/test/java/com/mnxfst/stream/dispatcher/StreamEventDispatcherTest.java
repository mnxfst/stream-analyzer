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
package com.mnxfst.stream.dispatcher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;

/**
 * Test cases for {@link StreamEventDispatcher}
 * @author mnxfst
 * @since Jan 30, 2014
 */
public class StreamEventDispatcherTest {

	private ActorSystem actorSystem;
	
	@Before
	public void initialize() {
		this.actorSystem = ActorSystem.create("junit");
	}
	
	@After
	public void shutdown() {
		JavaTestKit.shutdownActorSystem(actorSystem);
	}
	
	
	@Test
	public void testBindEventSourcePipeline() {
		
		ActorRef lookup = actorSystem.actorOf(Props.create(StreamEventDispatcher.class), "testme");
		TestActorRef<StreamEventDispatcher> sed = TestActorRef.create(actorSystem, (Props.create(StreamEventDispatcher.class)));
		sed.underlyingActor().bindEventSourcePipeline("test", "wd");;
		System.out.println("hello");
		
	
//		TODO set up test scenario
	}
	
}
