package org.apache.uima.aae.message;

import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;

public interface MessageBuilder {
	public MessageBuilder newGetMetaReplyMessage(Origin origin);
	public MessageBuilder newGetMetaRequestMessage(Origin origin);
	public MessageBuilder newProcessCASReplyMessage(Origin origin);
	public MessageBuilder newProcessCASRequestMessage(Origin origin);
	public MessageBuilder newCpCReplyMessage(Origin origin);
	public MessageBuilder newCpCRequestMessage(Origin origin);
	public MessageBuilder newReleaseCASRequestMessage(Origin origin);

	public MessageContext build() throws Exception;
	public MessageBuilder withSenderKey(String senderKey);
	public MessageBuilder withReplyDestination(Object replyToDestination);
	public MessageBuilder withPayload(int payload);
	public MessageBuilder withId(String id);
	public MessageBuilder withCasReferenceId(String id);
	public MessageBuilder withSequenceNo(long sequence);
	public MessageBuilder withParentCasReferenceId(String id);
	public MessageBuilder withMetadata(AnalysisEngineMetaData meta);
	
}
