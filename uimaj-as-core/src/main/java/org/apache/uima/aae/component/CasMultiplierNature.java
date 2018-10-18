package org.apache.uima.aae.component;

public interface CasMultiplierNature {
	boolean disableJCasCache();
	long getInitialFsHeapSize();
	int getPoolSize();
	boolean processParentLast();
}
