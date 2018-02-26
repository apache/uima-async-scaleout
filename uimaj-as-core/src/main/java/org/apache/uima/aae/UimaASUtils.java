package org.apache.uima.aae;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UimaASUtils {
	public static boolean isAbsolutePath(String filePath) {
		return new File(filePath).isAbsolute();
	}
	public static String getBaseDir(String filePath) {
		// match the end of the path. An example:
		// /user/path/foo.xml
		// the pattern matches /foo.xml
		String pattern = "/[^/]*?\\.[xX][mM][lL]";
	    Pattern r = Pattern.compile(pattern);

	    // Now create matcher object.
	    Matcher m = r.matcher(filePath);
	    if (m.find( )) {
	    	// strip from a given path the filename and
	    	// return parent path.
	    	return filePath.replace(m.group(0),"");
	    }
	    return null;
	}
	public static String replaceBackslashesWithForwardSlashes(String filePath) {
		return filePath.replace("\\", "/");
	}
	public static String fixPath( String parentPath, String childPath ) {
		// operate on a copy of childPath. In case the childPath is absolute
		// path we will return the original passed in as an arg
		String adjustedPath = childPath;
		
    	if ( childPath.startsWith("file:")) {
    		adjustedPath = childPath.substring(6);
    	}
    	adjustedPath = UimaASUtils.replaceBackslashesWithForwardSlashes(adjustedPath);
    	if ( !UimaASUtils.isAbsolutePath(adjustedPath)) {
    		// relative path to the enclosing descriptor
    		String baseDir = UimaASUtils.getBaseDir(parentPath);
    		adjustedPath = baseDir+'/'+adjustedPath;
    	} else {
    		adjustedPath = childPath;
    	}
    	return adjustedPath;
	}
}
