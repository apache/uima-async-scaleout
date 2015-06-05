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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.impl.Serialization;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLParser;

public class WarmUpDataProvider {
	InputChannel inputChannel;
	AnalysisEngineController controller;
	private String inputFileName;
	private FileInputStream fis;
	private ZipInputStream zis;
	private ZipEntry nextEntry;
	private int docSeq;
	private boolean readingXmiFormat;
	private TypeSystem inputTS;

	public static void main(String[] args) {

	}

	public WarmUpDataProvider(String inputFileName)
			throws IOException {
		this.inputFileName = inputFileName;
		fis = new FileInputStream(new File(inputFileName));
		zis = new ZipInputStream(new BufferedInputStream(fis, 1024 * 100));
		docSeq = 0;
	}

	public boolean hasNext() throws AnalysisEngineProcessException {
		try {
			nextEntry = zis.getNextEntry();
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
		return (nextEntry != null) ? true : false;
	}

	public CAS next(CAS cas) throws Exception {
		if (0 == docSeq) {
			if (nextEntry.getName().equals("typesystem.xml")) {
				getTypesystem();
				readingXmiFormat = false;
			} else {
				readingXmiFormat = true;
			}
		} else {
			if (nextEntry.getName().equals("typesystem.xml")) {
				throw new AnalysisEngineProcessException(new RuntimeException(
						"typesystem.xml entry found in the middle of input zipfile "
								+ inputFileName));
			}
		}
		byte[] buff = new byte[10000];
		int bytesread;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			while (-1 != (bytesread = zis.read(buff))) {
				baos.write(buff, 0, bytesread);
			}
			ByteArrayInputStream bis = new ByteArrayInputStream(
					baos.toByteArray());
			if (readingXmiFormat) {
				XmiCasDeserializer.deserialize(bis, cas, true);
			} else {
				Serialization.deserializeCAS(cas, bis, inputTS, null);
			}
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
		docSeq++;
		return cas;
	}

	private void getTypesystem() throws AnalysisEngineProcessException {
		byte[] buff = new byte[10000];
		int bytesread;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			while (-1 != (bytesread = zis.read(buff))) {
				baos.write(buff, 0, bytesread);
			}
			ByteArrayInputStream bis = new ByteArrayInputStream(
					baos.toByteArray());
			// Get XML parser from framework
			XMLParser xmlParser = UIMAFramework.getXMLParser();
			// Parse type system descriptor
			TypeSystemDescription tsDesc = xmlParser
					.parseTypeSystemDescription(new XMLInputSource(
							(InputStream) bis, null));
			// Use type system description to create CAS and get the type system
			// object
			inputTS = CasCreationUtils.createCas(tsDesc, null, null)
					.getTypeSystem();
			// advance to first input CAS
			nextEntry = zis.getNextEntry();
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

}
