package org.apache.uima.aae.error;

import java.util.Map;

import org.apache.uima.aae.service.delegate.AnalysisEngineDelegate;
import org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType;
import org.apache.uima.resourceSpecifier.GetMetadataErrorsType;
import org.apache.uima.resourceSpecifier.ProcessCasErrorsType;

public class Thresholds {
	public enum Action { TERMINATE, CONTINUE, DISABLE};
	
 	public static Threshold newThreshold(Action action) {
		Threshold t = new Threshold();
		t.setAction(action.name().toLowerCase());
		t.setContinueOnRetryFailure(false);
		t.setMaxRetries(0);
		t.setThreshold(1);
		t.setWindow(0);
		return t;
	}
  	public static Threshold newThreshold() {
  		return newThreshold(Action.TERMINATE);
  		/*
		Threshold t = new Threshold();
		t.setAction("terminate");
		t.setContinueOnRetryFailure(false);
		t.setMaxRetries(0);
		t.setThreshold(1);
		t.setWindow(0);
		return t;
*/
	}
	public static Threshold getThreshold(String action, int maxRetries ) {
		Threshold t1 = newThreshold();
		t1.setAction(action);
		t1.setContinueOnRetryFailure(false);
		t1.setMaxRetries(maxRetries);
		t1.setThreshold(1);
		
		return t1;
	}
	   public static Threshold getThresholdFor(ProcessCasErrorsType processCasErrors) {
	    	Threshold t;
	    	if ( processCasErrors == null ) {
	        	t = newThreshold();
	    	} else {
	        	t = getThreshold(processCasErrors.getThresholdAction(), processCasErrors.getMaxRetries() );
	        	t.setThreshold(processCasErrors.getThresholdCount());
	        	t.setContinueOnRetryFailure(Boolean.valueOf(processCasErrors.getContinueOnRetryFailure()));
	        	t.setWindow(processCasErrors.getThresholdWindow());
	    	}
	    	return t;
	    }
	   public static Threshold getThresholdFor(GetMetadataErrorsType metaErrors) {
	    	Threshold t;
	    	if ( metaErrors == null ) {
	    		t = newThreshold();
	    	} else {
	    		//metaErrors.get
	    		t = getThreshold(metaErrors.getErrorAction(), metaErrors.getMaxRetries() );
	    		
	    	}
	    	return t;
	    }
	   public static Threshold getThresholdFor(CollectionProcessCompleteErrorsType cpcErrors) {
	    	Threshold t;
	    	if ( cpcErrors == null ) {
	    		t = newThreshold();
	    	} else {
	    		t = getThreshold(cpcErrors.getAdditionalErrorAction(), 0);
	    	}
	    	return t;
	    }
		public static void addDelegateErrorThreshold(AnalysisEngineDelegate delegate, GetMetadataErrorsType errorHandler, Map<String, Threshold> thresholdMap) {
			if ( errorHandler != null ) {
				Threshold t = getThresholdFor(errorHandler);
				delegate.setGetMetaTimeout(errorHandler.getTimeout());
				thresholdMap.put(delegate.getKey(), t);
			} else {
				thresholdMap.put(delegate.getKey(), newThreshold(Action.TERMINATE));
			}
		}
		public static void addDelegateErrorThreshold(AnalysisEngineDelegate delegate, ProcessCasErrorsType errorHandler, Map<String, Threshold> thresholdMap) {
			if ( errorHandler != null ) {
				Threshold t = getThresholdFor(errorHandler);
				delegate.setProcessTimeout(errorHandler.getTimeout());
				thresholdMap.put(delegate.getKey(), t);
			} else {
				thresholdMap.put(delegate.getKey(), newThreshold(Action.TERMINATE));
			}
		}
		public static void addDelegateErrorThreshold(AnalysisEngineDelegate delegate, CollectionProcessCompleteErrorsType  errorHandler, Map<String, Threshold> thresholdMap) {
			if ( errorHandler != null ) {
				Threshold t = getThresholdFor(errorHandler);
				delegate.setProcessTimeout(errorHandler.getTimeout());
				thresholdMap.put(delegate.getKey(), t);
			} else {
				thresholdMap.put(delegate.getKey(), newThreshold(Action.CONTINUE));
			}
		}

}
