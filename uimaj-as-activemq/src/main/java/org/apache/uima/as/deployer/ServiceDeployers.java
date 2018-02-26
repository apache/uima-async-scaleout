/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.uima.as.deployer;

import java.util.concurrent.CountDownLatch;

import org.apache.uima.as.deployer.direct.UimaAsDirectServiceDeployer;
import org.apache.uima.as.deployer.jms.UimaAsJmsServiceDeployer;

/*
 * Concrete Factory class which creates instances of UimaAsServiceDeployer based
 * type of protocol and provider. To make a new Deployer, add new protocol and
 * provider to the enums below, and instantiate your deployer in newDeployer()
 */
public class ServiceDeployers {
	public enum Protocol {
		JAVA("java"), JMS("jms");
		String protocol;

		Protocol(String dt) {
			protocol = dt;
		}

		public String get() {
			return protocol;
		}
	}

	public enum Provider {
		JAVA("java"), ACTIVEMQ("activemq");
		String provider;

		Provider(String provider) {
			this.provider = provider;
		}

		public String get() {
			return provider;
		}
	}
	/**
	 * Creates instance of a deployer for a given protocol and provider.
	 * 
	 * @param protocol
	 * @param provider
	 * @return - 
	 */
	public static UimaAsServiceDeployer newDeployer(Protocol protocol, Provider provider) {

		UimaAsServiceDeployer deployer = null;
		if (Protocol.JAVA.equals(protocol) && Provider.JAVA.equals(provider)) {
			deployer = new UimaAsDirectServiceDeployer(new CountDownLatch(1));
		} else if (Protocol.JMS.equals(protocol) && Provider.ACTIVEMQ.equals(provider)) {
			deployer = new UimaAsJmsServiceDeployer(new CountDownLatch(1));
		}
		return deployer;
	}
}
