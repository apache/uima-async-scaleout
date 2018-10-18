package org.apache.uima.aae.component;

public class ComponentCasPool {
	boolean disableJCasCache;
	int initialHeapSize=1000;
	int poolSize=1;
	public ComponentCasPool(boolean disableJCasCache, int initialHeapSize, int poolSize) {
		this.disableJCasCache = disableJCasCache;
		this.initialHeapSize = initialHeapSize;
		this.poolSize = poolSize;
	}
	public boolean isDisableJCasCache() {
		return disableJCasCache;
	}
	public int getInitialHeapSize() {
		return initialHeapSize;
	}
	public int getPoolSize() {
		return poolSize;
	}
	
}
