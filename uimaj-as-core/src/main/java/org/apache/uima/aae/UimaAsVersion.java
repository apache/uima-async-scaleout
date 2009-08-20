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

package org.apache.uima.aae;

/**
 * This class is used to define current UIMA AS version
 * 
 */
public class UimaAsVersion {

  
  /**
   * Gets the major version number of the UIMA AS implementation.
   * 
   * @return the major version number
   */
  public short getMajorVersion() {
    return 2; // major version
  }

  /**
   * Gets the minor version number of the UIMA AS implementation.
   * 
   * @return the minor version number
   */
  public short getMinorVersion() {
    return 3; // minor version
  }

  /**
   * Gets the build revision number of the UIMA AS implementation.
   * 
   * @return the build revision number
   */
  public short getBuildRevision() {
    return 0; // build revision
  }

  public String getVersionString() {
    return "" + getMajorVersion() + "." + getMinorVersion() + "." + getBuildRevision();
  }
}
