package org.apache.uima.aae.definition.connectors;

public interface ListenerCallback {
	public void onInitializationError(Exception e);
	public boolean failedInitialization();
	public Exception getException();
}
