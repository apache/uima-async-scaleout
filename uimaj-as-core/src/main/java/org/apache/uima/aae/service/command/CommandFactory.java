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
package org.apache.uima.aae.service.command;

import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.error.AsynchAEException;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.message.UimaAsMessage;

public class CommandFactory {
	// Can't instantiate this factory. Use static methods only
	private CommandFactory() {

	}
	public static UimaAsCommand newCommand(MessageContext mc, AnalysisEngineController controller)
			throws AsynchAEException {
		// Message type is either Request or Response
		int messageType = mc.getMessageIntProperty(AsynchAEMessage.MessageType);

		if (messageType == AsynchAEMessage.Request) {
			return newRequestCommand(mc, controller);
		} else if (messageType == AsynchAEMessage.Response) {
			return newResponseCommand(mc, controller);
		}

		return CommandBuilder.createNoOpCommand(mc, controller);
	}
	
	private static UimaAsCommand newRequestCommand(MessageContext mc, AnalysisEngineController controller)
			throws AsynchAEException {
		int command = mc.getMessageIntProperty(AsynchAEMessage.Command);
		UimaAsCommand command2Run;
		switch (command) {
		case AsynchAEMessage.Process:
			if (mc.propertyExists(AsynchAEMessage.CasSequence)) {
				command2Run = CommandBuilder.createProcessChildCasRequestCommand(mc, controller);
			} else {
				command2Run = CommandBuilder.createProcessInputCasRequestCommand(mc, controller);
			}
			break;
		case AsynchAEMessage.GetMeta:
			command2Run = CommandBuilder.createGetMetaRequestCommand(mc, controller);
			break;
		case AsynchAEMessage.CollectionProcessComplete:
			command2Run = CommandBuilder.createCollectionProcessCompleteRequestCommand(mc, controller);
			break;

		case AsynchAEMessage.ReleaseCAS:
			command2Run = CommandBuilder.createReleaseCASRequestCommand(mc, controller);
			break;
		default:
			command2Run = CommandBuilder.createNoOpCommand(mc, controller);
			break;
		}
		return command2Run;
	}

	private static UimaAsCommand newResponseCommand(MessageContext mc, AnalysisEngineController controller)
			throws AsynchAEException {
		int command = mc.getMessageIntProperty(AsynchAEMessage.Command);
		UimaAsCommand command2Run;
		switch (command) {
		case AsynchAEMessage.Process:
			if (mc.propertyExists(AsynchAEMessage.CasSequence)) {
				command2Run = CommandBuilder.createProcessChildCasResponseCommand(mc, controller);
			} else {
				command2Run = CommandBuilder.createProcessInputCasResponseCommand(mc, controller);
			}
			break;
		case AsynchAEMessage.GetMeta:
			command2Run = CommandBuilder.createGetMetaResponseCommand(mc, controller);
			break;

		case AsynchAEMessage.CollectionProcessComplete:
			command2Run = CommandBuilder.createCollectionProcessCompleteResponseCommand(mc, controller);
			break;
		case AsynchAEMessage.ServiceInfo:
			command2Run = CommandBuilder.createServiceInfoResponseCommand(mc, controller);
			break;
		default:
			command2Run = CommandBuilder.createNoOpCommand(mc, controller);
			break;
		}
		return command2Run;
	}



	private static class CommandBuilder {
		// Can't instantiate the builder. Use static calls only
		private CommandBuilder() {

		}
		static UimaAsCommand createProcessChildCasRequestCommand(MessageContext mc,
				AnalysisEngineController controller) {
			return new ProcessChildCasRequestCommand(mc, controller);
		}

		static UimaAsCommand createProcessInputCasRequestCommand(MessageContext mc,
				AnalysisEngineController controller) {
			return new ProcessInputCasRequestCommand(mc, controller);
		}

		static UimaAsCommand createProcessChildCasResponseCommand(MessageContext mc,
				AnalysisEngineController controller) {
			return new ProcessChildCasResponseCommand(mc, controller);
		}

		static UimaAsCommand createProcessInputCasResponseCommand(MessageContext mc,
				AnalysisEngineController controller) {
			return new ProcessInputCasResponseCommand(mc, controller);
		}

		static UimaAsCommand createGetMetaResponseCommand(MessageContext mc, AnalysisEngineController controller) {
			return new GetMetaResponseCommand(mc, controller);
		}

		static UimaAsCommand createGetMetaRequestCommand(MessageContext mc, AnalysisEngineController controller) {
			return new GetMetaRequestCommand(mc, controller);
		}

		static UimaAsCommand createReleaseCASRequestCommand(MessageContext mc, AnalysisEngineController controller) {
			return new ReleaseCASRequestCommand(mc, controller);
		}

		static UimaAsCommand createNoOpCommand(MessageContext mc, AnalysisEngineController controller) {
			return new NoOpCommand(mc, controller);
		}

		static UimaAsCommand createCollectionProcessCompleteRequestCommand(MessageContext mc,
				AnalysisEngineController controller) {
			return new CollectionProcessCompleteRequestCommand(mc, controller);
		}

		static UimaAsCommand createServiceInfoResponseCommand(MessageContext mc,
				AnalysisEngineController controller) {
			return new ProcessServiceInfoResponseCommand(mc, controller);
		}
		static UimaAsCommand createCollectionProcessCompleteResponseCommand(MessageContext mc,
				AnalysisEngineController controller) {
			return new CollectionProcessCompleteResponseCommand(mc, controller);
		}

	}
	
	/* ------------------------------------------------------------------ */
	/* ------------------------------------------------------------------ */
	/* ------------------------------------------------------------------ */
	/* ------------------------------------------------------------------ */
	/* ------------------------------------------------------------------ */
	/* ------------------------------------------------------------------ */
	
	
	
	
	/*
	public static UimaAsCommand newCommand(UimaAsMessage mc, AnalysisEngineController controller)
			throws AsynchAEException {
		// Message type is either Request or Response
		int messageType = mc.getMessageIntProperty(AsynchAEMessage.MessageType);

		if (messageType == AsynchAEMessage.Request) {
			return newRequestCommand(mc, controller);
		} else if (messageType == AsynchAEMessage.Response) {
			return newResponseCommand(mc, controller);
		}

		return CommandBuilder.createNoOpCommand(mc, controller);
	}
	private static UimaAsCommand newRequestCommand(MessageContext mc, AnalysisEngineController controller)
			throws AsynchAEException {
		int command = mc.getMessageIntProperty(AsynchAEMessage.Command);
		UimaAsCommand command2Run;
		switch (command) {
		case AsynchAEMessage.Process:
			if (mc.propertyExists(AsynchAEMessage.CasSequence)) {
				command2Run = CommandBuilder.createProcessChildCasRequestCommand(mc, controller);
			} else {
				command2Run = CommandBuilder.createProcessInputCasRequestCommand(mc, controller);
			}
			break;
		case AsynchAEMessage.GetMeta:
			command2Run = CommandBuilder.createGetMetaRequestCommand(mc, controller);
			break;
		case AsynchAEMessage.CollectionProcessComplete:
			command2Run = CommandBuilder.createCollectionProcessCompleteRequestCommand(mc, controller);
			break;

		case AsynchAEMessage.ReleaseCAS:
			command2Run = CommandBuilder.createReleaseCASRequestCommand(mc, controller);
			break;
		default:
			command2Run = CommandBuilder.createNoOpCommand(mc, controller);
			break;
		}
		return command2Run;
	}

	private static UimaAsCommand newResponseCommand(MessageContext mc, AnalysisEngineController controller)
			throws AsynchAEException {
		int command = mc.getMessageIntProperty(AsynchAEMessage.Command);
		UimaAsCommand command2Run;
		switch (command) {
		case AsynchAEMessage.Process:
			if (mc.propertyExists(AsynchAEMessage.CasSequence)) {
				command2Run = CommandBuilder.createProcessChildCasResponseCommand(mc, controller);
			} else {
				command2Run = CommandBuilder.createProcessInputCasResponseCommand(mc, controller);
			}
			break;
		case AsynchAEMessage.GetMeta:
			command2Run = CommandBuilder.createGetMetaResponseCommand(mc, controller);
			break;

		case AsynchAEMessage.CollectionProcessComplete:
			command2Run = CommandBuilder.createCollectionProcessCompleteResponseCommand(mc, controller);
			break;
		case AsynchAEMessage.ServiceInfo:
			command2Run = CommandBuilder.createServiceInfoResponseCommand(mc, controller);
			break;
		default:
			command2Run = CommandBuilder.createNoOpCommand(mc, controller);
			break;
		}
		return command2Run;
	}



	private static class CommandBuilder {
		// Can't instantiate the builder. Use static calls only
		private CommandBuilder() {

		}
		static UimaAsCommand createProcessChildCasRequestCommand(MessageContext mc,
				AnalysisEngineController controller) {
			return new ProcessChildCasRequestCommand(mc, controller);
		}

		static UimaAsCommand createProcessInputCasRequestCommand(MessageContext mc,
				AnalysisEngineController controller) {
			return new ProcessInputCasRequestCommand(mc, controller);
		}

		static UimaAsCommand createProcessChildCasResponseCommand(MessageContext mc,
				AnalysisEngineController controller) {
			return new ProcessChildCasResponseCommand(mc, controller);
		}

		static UimaAsCommand createProcessInputCasResponseCommand(MessageContext mc,
				AnalysisEngineController controller) {
			return new ProcessInputCasResponseCommand(mc, controller);
		}

		static UimaAsCommand createGetMetaResponseCommand(MessageContext mc, AnalysisEngineController controller) {
			return new GetMetaResponseCommand(mc, controller);
		}

		static UimaAsCommand createGetMetaRequestCommand(MessageContext mc, AnalysisEngineController controller) {
			return new GetMetaRequestCommand(mc, controller);
		}

		static UimaAsCommand createReleaseCASRequestCommand(MessageContext mc, AnalysisEngineController controller) {
			return new ReleaseCASRequestCommand(mc, controller);
		}

		static UimaAsCommand createNoOpCommand(MessageContext mc, AnalysisEngineController controller) {
			return new NoOpCommand(mc, controller);
		}

		static UimaAsCommand createCollectionProcessCompleteRequestCommand(MessageContext mc,
				AnalysisEngineController controller) {
			return new CollectionProcessCompleteRequestCommand(mc, controller);
		}

		static UimaAsCommand createServiceInfoResponseCommand(MessageContext mc,
				AnalysisEngineController controller) {
			return new ProcessServiceInfoResponseCommand(mc, controller);
		}
		static UimaAsCommand createCollectionProcessCompleteResponseCommand(MessageContext mc,
				AnalysisEngineController controller) {
			return new CollectionProcessCompleteResponseCommand(mc, controller);
		}

	}
*/
}
