package org.apache.uima.aae.component;

public class CasMultiplierComponent implements CasMultiplierNature {
	boolean disableJCasCache;
	long initialFsHeapSize;
	int casPoolSize;
	boolean processParentLast;
	
	public CasMultiplierComponent(boolean disableJCasCache,long initialFsHeapSize,int casPoolSize,boolean processParentLast ) {
		this.disableJCasCache = disableJCasCache;
		this.initialFsHeapSize = initialFsHeapSize;
		this.casPoolSize = casPoolSize;
		this.processParentLast = processParentLast;
	}
	@Override
	public boolean disableJCasCache() {
		return false;
	}

	@Override
	public long getInitialFsHeapSize() {
		return 0;
	}

	@Override
	public int getPoolSize() {
		return 0;
	}

	@Override
	public boolean processParentLast() {
		return false;
	}

}
