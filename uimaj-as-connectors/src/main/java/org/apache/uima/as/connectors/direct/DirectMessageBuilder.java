package org.apache.uima.as.connectors.direct;

import org.apache.uima.aae.definition.connectors.UimaAsConsumer;
import org.apache.uima.aae.definition.connectors.UimaAsConsumer.ConsumerType;
import org.apache.uima.aae.definition.connectors.UimaAsEndpoint;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageBuilder;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.message.Origin;
import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.apache.uima.as.client.DirectMessage;
import org.apache.uima.as.client.DirectMessageContext;

public class DirectMessageBuilder implements MessageBuilder {
		private DirectMessage message;
		private String id;
		private UimaAsEndpoint endpoint;
		
		protected DirectMessageBuilder(UimaAsEndpoint endpoint ) {
			this.endpoint = endpoint;
		}
		private MessageBuilder newMessage(int command, int msgType, Origin origin) {
			message = new DirectMessage().
					withCommand(command).
					withMessageType(msgType).
					withOrigin(origin);
			return this;
		}

		public MessageContext build() {
			return new DirectMessageContext(message,id,id);
		}

		public DirectMessageBuilder withSenderKey(String senderKey) {
			message.withDelegateKey(senderKey);
			return this;
		}
		public DirectMessageBuilder withReplyDestination(Object replyToDestination) {
			message.withReplyDestination(replyToDestination);
			return this;
		}
		public DirectMessageBuilder withPayload(int payload) {
			message.withPayload(payload);
			return this;
		}

		@Override
		public MessageBuilder withId(String id) {
			message.withEndpointName(id);
			return this;
		}
		@Override
		public MessageBuilder withCasReferenceId(String casReferenceId) {
			message.withCasReferenceId(casReferenceId);
			return this;
		}
		@Override
		public MessageBuilder withParentCasReferenceId(String parentCasReferenceId) {
			message.withParentCasReferenceId(parentCasReferenceId);
			return this;
		}
		@Override
		public MessageBuilder withMetadata(AnalysisEngineMetaData meta) {
			message.withMetadata(meta);
			return this;
		}
		@Override
		public MessageBuilder withSequenceNo(long sequence) {
			message.withSequenceNumber(sequence);
			return this;
		}

		@Override
		public MessageBuilder newGetMetaReplyMessage(Origin origin) {
			return newMessage(AsynchAEMessage.GetMeta, AsynchAEMessage.Response, origin);
		}

		@Override
		public MessageBuilder newGetMetaRequestMessage(Origin origin) {
			UimaAsConsumer consumer =
					endpoint.getConsumer(origin.getName(), ConsumerType.GetMetaResponse);
			return newMessage(AsynchAEMessage.GetMeta, AsynchAEMessage.Request, origin).withReplyDestination(consumer);
		}

		@Override
		public MessageBuilder newProcessCASReplyMessage(Origin origin) {
			return newMessage(AsynchAEMessage.Process, AsynchAEMessage.Response, origin);
		}

		@Override
		public MessageBuilder newProcessCASRequestMessage(Origin origin) {
			UimaAsConsumer consumer =
					endpoint.getConsumer(origin.getName(), ConsumerType.ProcessCASResponse);
			return newMessage(AsynchAEMessage.Process, AsynchAEMessage.Request, origin).withReplyDestination(consumer);
		}

		@Override
		public MessageBuilder newCpCReplyMessage(Origin origin) {
			return newMessage(AsynchAEMessage.CollectionProcessComplete, AsynchAEMessage.Response, origin);
		}

		@Override
		public MessageBuilder newCpCRequestMessage(Origin origin) {
			UimaAsConsumer consumer =
					endpoint.getConsumer(origin.getName(), ConsumerType.CpcResponse);
			return newMessage(AsynchAEMessage.CollectionProcessComplete, AsynchAEMessage.Request, origin).withReplyDestination(consumer);
		}

		@Override
		public MessageBuilder newReleaseCASRequestMessage(Origin origin) {
			return newMessage(AsynchAEMessage.ReleaseCAS, AsynchAEMessage.Request, origin);
		}


}
