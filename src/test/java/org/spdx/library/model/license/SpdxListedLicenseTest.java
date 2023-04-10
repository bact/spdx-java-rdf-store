/**
 * Copyright (c) 2019 Source Auditor Inc.
 *
 * SPDX-License-Identifier: Apache-2.0
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.spdx.library.model.license;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.licenseTemplate.InvalidLicenseTemplateException;
import org.spdx.spdxRdfStore.OutputFormat;
import org.spdx.spdxRdfStore.RdfStore;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class SpdxListedLicenseTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
        super.setUp();
        DefaultModelStore.reset(new RdfStore(), "http://test.document.uri/1", new ModelCopyManager());
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCreate() throws InvalidSPDXAnalysisException, InvalidLicenseTemplateException {
		String name = "name";
		String id = "AFL-3.0";
		String text = "text";
		Collection<String> sourceUrls = new ArrayList<String>(Arrays.asList(new String[] {"source url1", "source url2"}));
		String notes = "notes";
		String standardLicenseHeader = "Standard license header";
		String template = "template";
		String licenseHtml = "<html>html</html>";
		SpdxListedLicense stdl = new SpdxListedLicense(name, id, text,
				sourceUrls, notes, standardLicenseHeader, template, true, true, licenseHtml, false, null);
		SpdxListedLicense compLic = new SpdxListedLicense(stdl.getModelStore(), stdl.getDocumentUri(), id, stdl.getCopyManager(), false);
		assertEquals(id, compLic.getLicenseId());
		assertEquals(text, compLic.getLicenseText());
		List<String> verify = stdl.verify();
		assertEquals(0, verify.size());
		verify = compLic.verify();
		assertEquals(0, verify.size());
		assertEquals(name, compLic.getName());
		assertEquals(sourceUrls.size(), compLic.getSeeAlso().size());
		
		assertEquals(notes, compLic.getComment());
		assertEquals(standardLicenseHeader, compLic.getStandardLicenseHeader());
		assertEquals(template, compLic.getStandardLicenseTemplate());
		assertTrue(compLic.isFsfLibre());
		assertTrue(compLic.isOsiApproved());
		assertFalse(compLic.isDeprecated());
	}
	
   public void testSerialize() throws InvalidSPDXAnalysisException, InvalidLicenseTemplateException, IOException {
        
       String name = "name";
        String id = "AFL-3.0";
        String text = "text";
        Collection<String> sourceUrls = new ArrayList<String>(Arrays.asList(new String[] {"source url1", "source url2"}));
        String notes = "notes";
        String standardLicenseHeader = "Standard license header";
        String template = "template";
        String licenseHtml = "<html>html</html>";
        String documentUri = "http://test.document.uri/2";
        RdfStore store = new RdfStore();
        ModelCopyManager copyManager = new ModelCopyManager();
        SpdxListedLicense stdl = new SpdxListedLicense(store, documentUri,
                id, copyManager, true);
        stdl.setLicenseText(text);
        stdl.getSeeAlso().addAll(sourceUrls);
        stdl.setComment(notes);
        stdl.setStandardLicenseTemplate(template);
        stdl.setLicenseTextHtml(licenseHtml);
        stdl.setName(name);
        stdl.setStandardLicenseHeader(standardLicenseHeader);
        stdl.setFsfLibre(true);
        stdl.setOsiApproved(true);
        stdl.setDeprecated(false);
        
        store.setOutputFormat(OutputFormat.JSON_LD);
        String jsonld;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            store.serialize(documentUri, outputStream);
            jsonld = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        }
        
        RdfStore compStore = new RdfStore();
        compStore.setOutputFormat(OutputFormat.JSON_LD);
        
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonld.getBytes(StandardCharsets.UTF_8))) {
            compStore.deSerialize(inputStream, true, documentUri);
        } 
        
        SpdxListedLicense compLic = new SpdxListedLicense(compStore, stdl.getDocumentUri(), id, null, false);
        assertEquals(id, compLic.getLicenseId());
        assertEquals(text, compLic.getLicenseText());
        List<String> verify = stdl.verify();
        assertEquals(0, verify.size());
        verify = compLic.verify();
        assertEquals(0, verify.size());
        assertEquals(name, compLic.getName());
        assertEquals(sourceUrls.size(), compLic.getSeeAlso().size());
        
        assertEquals(notes, compLic.getComment());
        assertEquals(standardLicenseHeader, compLic.getStandardLicenseHeader());
        assertEquals(template, compLic.getStandardLicenseTemplate());
        assertTrue(compLic.isFsfLibre());
        assertTrue(compLic.isOsiApproved());
        assertFalse(compLic.isDeprecated());
    }
	

	public void testSetComment() throws InvalidSPDXAnalysisException {

		String name = "name";
		String id = "AFL-3.0";
		String text = "text";
		Collection<String> sourceUrls = new ArrayList<String>(Arrays.asList(new String[] {"source url2", "source url3"}));
		String comments = "comments1";
		String comments2 = "comments2";
		String standardLicenseHeader = "Standard license header";
		String template = "template";
		SpdxListedLicense stdl = new SpdxListedLicense(name, id, text,
				sourceUrls, comments, standardLicenseHeader, template, true, false, null, false, null);
		SpdxListedLicense compLic = new SpdxListedLicense(stdl.getModelStore(), stdl.getDocumentUri(), id, stdl.getCopyManager(), false);
		assertEquals(comments, compLic.getComment());
		
		compLic.setComment(comments2);
		assertEquals(comments2, compLic.getComment());
		SpdxListedLicense compLic2 = new SpdxListedLicense(stdl.getModelStore(), stdl.getDocumentUri(), id, stdl.getCopyManager(), false);
		assertEquals(comments2, compLic2.getComment());

		List<String> verify = stdl.verify();
		assertEquals(0, verify.size());
		verify = compLic.verify();
		assertEquals(0, verify.size());
	}
	

	public void testSetFsfLibre() throws InvalidSPDXAnalysisException {

		String name = "name";
		String id = "DIFFERENT";
		String text = "text";
		Collection<String> sourceUrls = new ArrayList<String>(Arrays.asList(new String[] {"source url1", "source url2"}));
		String notes = "notes";
		String standardLicenseHeader = "Standard license header";
		String template = "template";
		String licenseHtml = "<html>html</html>";
		String deprecatedVersion = "3.2";
		SpdxListedLicense stdl = new SpdxListedLicense(name, id, text,
				sourceUrls, notes, standardLicenseHeader, template, false, false, licenseHtml, false, null);
		assertFalse(stdl.isFsfLibre());
		stdl.setFsfLibre(true);
		assertTrue(stdl.isFsfLibre());
		SpdxListedLicense compLic = new SpdxListedLicense(stdl.getModelStore(), stdl.getDocumentUri(), id, stdl.getCopyManager(), false);
		assertTrue(stdl.isFsfLibre());
		compLic.setFsfLibre(false);
		assertFalse(compLic.isFsfLibre());
		SpdxListedLicense compLic2 = new SpdxListedLicense(stdl.getModelStore(), stdl.getDocumentUri(), id, stdl.getCopyManager(), false);
		assertFalse(compLic2.isFsfLibre());
		List<String> verify = stdl.verify();
		assertEquals(0, verify.size());
		verify = compLic.verify();
		assertEquals(0, verify.size());
		
		// Test for null value
		SpdxListedLicense stdl2 = new SpdxListedLicense(name, id, text,
				sourceUrls, notes, standardLicenseHeader, template, false, null, licenseHtml, true, deprecatedVersion);
		assertTrue(stdl2.getFsfLibre() == null);
		assertFalse(stdl2.isFsfLibre());
		assertFalse(stdl2.isNotFsfLibre());
		SpdxListedLicense compLic3 = new SpdxListedLicense(stdl2.getModelStore(), stdl.getDocumentUri(), id, stdl.getCopyManager(), false);
		assertTrue(compLic3.getFsfLibre() == null);
		assertFalse(compLic3.isFsfLibre());
		assertFalse(compLic3.isNotFsfLibre());
		compLic3.setFsfLibre(false);
		assertFalse(compLic3.getFsfLibre() == null);
		assertFalse(compLic3.isFsfLibre());
		assertTrue(compLic3.isNotFsfLibre());
		SpdxListedLicense compLic4 = new SpdxListedLicense(stdl2.getModelStore(), stdl.getDocumentUri(), id, stdl.getCopyManager(), false);
		assertFalse(compLic4.getFsfLibre() == null);
		assertFalse(compLic4.isFsfLibre());		
		assertTrue(compLic4.isNotFsfLibre());
	}
	

	public void testSetDeprecated() throws InvalidSPDXAnalysisException {

		String name = "name";
		String id = "AFL-3.0";
		String text = "text";
		Collection<String> sourceUrls = new ArrayList<String>(Arrays.asList(new String[] {"source url2", "source url3"}));
		String comments = "comments1";
		String standardLicenseHeader = "Standard license header";
		String template = "template";
		SpdxListedLicense stdl = new SpdxListedLicense(name, id, text,
				sourceUrls, comments, standardLicenseHeader, template, true,null, null, false, null);
		stdl.setDeprecated(true);
		SpdxListedLicense compLic = new SpdxListedLicense(stdl.getModelStore(), stdl.getDocumentUri(), id, stdl.getCopyManager(), false);
		assertEquals(true, compLic.isDeprecated());
		
		compLic.setDeprecated(false);
		assertEquals(false, compLic.isDeprecated());
		SpdxListedLicense compLic2 = new SpdxListedLicense(stdl.getModelStore(), stdl.getDocumentUri(), id, stdl.getCopyManager(), false);
		assertEquals(false, compLic2.isDeprecated());
		List<String> verify = stdl.verify();
		assertEquals(0, verify.size());
		verify = compLic.verify();
		assertEquals(0, verify.size());
	}


	public void testSetText() throws InvalidSPDXAnalysisException {

		String name = "name";
		String id = "AFL-3.0";
		String text = "text";
		Collection<String> sourceUrls = new ArrayList<String>(Arrays.asList(new String[] {"source url2", "source url3"}));
		String notes = "notes";
		String standardLicenseHeader = "Standard license header";
		String template = "template";
		SpdxListedLicense stdl = new SpdxListedLicense(name, id, text,
				sourceUrls, notes, standardLicenseHeader, template, true,null, null, false, null);
		SpdxListedLicense compLic = new SpdxListedLicense(stdl.getModelStore(), stdl.getDocumentUri(), id, stdl.getCopyManager(), false);
		assertEquals(id, compLic.getLicenseId());
		assertEquals(text, compLic.getLicenseText());

		String newText = "new Text";
		compLic.setLicenseText(newText);
		assertEquals(newText, compLic.getLicenseText());
		SpdxListedLicense compLic2 = new SpdxListedLicense(stdl.getModelStore(), stdl.getDocumentUri(), id, stdl.getCopyManager(), false);
		assertEquals(newText, compLic2.getLicenseText());
		List<String> verify = stdl.verify();
		assertEquals(0, verify.size());	
		verify = compLic.verify();
		assertEquals(0, verify.size());	
	}
	

	public void testCopyOf() throws InvalidSPDXAnalysisException, InvalidLicenseTemplateException {

		String name = "name";
		String id = "AFL-3.0";
		String text = "text";
		Collection<String> sourceUrls = new ArrayList<String>(Arrays.asList(new String[] {"source url1", "source url2"}));
		String notes = "notes";
		String standardLicenseHeader = "Standard license header";
		String template = "template";
		String licenseHtml = "license HTML";
		String deprecatedVersion = "3.5";
		SpdxListedLicense stdl = new SpdxListedLicense(name, id, text,
				sourceUrls, notes, standardLicenseHeader, template, true, null, licenseHtml, true, deprecatedVersion);
		IModelStore store = new InMemSpdxStore();
		SpdxListedLicense lic2 = new SpdxListedLicense(store, SpdxConstants.LISTED_LICENSE_URL, id, stdl.getCopyManager(), true);
		lic2.copyFrom(stdl);

		assertEquals(id, lic2.getLicenseId());
		assertEquals(text, lic2.getLicenseText());
		assertEquals(notes, lic2.getComment());
		assertEquals(name, lic2.getName());
		assertTrue(compareCollectionContent(sourceUrls, lic2.getSeeAlso()));
		assertEquals(standardLicenseHeader, lic2.getStandardLicenseHeader());
		assertEquals(template, lic2.getStandardLicenseTemplate());
		assertTrue(lic2.getFsfLibre() == null);
		assertEquals(licenseHtml, lic2.getLicenseTextHtml());
		assertEquals(deprecatedVersion, lic2.getDeprecatedVersion());
		assertEquals(true, lic2.isDeprecated());
	}

	/**
	 * @param strings1
	 * @param strings2
	 * @return true if both arrays contain the same content independent of order
	 */
	private boolean compareCollectionContent(Collection<String> strings1,
			Collection<String> strings2) {
		if (strings1.size() != strings2.size()) {
			return false;
		}
		for (Object s1:strings1) {
			if (!strings2.contains(s1)) {
				return false;
			}
		}
		return true;
	}
	

	public void testEquivalent() throws InvalidSPDXAnalysisException {

		String name = "name";
		String name2 = "name2";
		String id = "AFL-3.0";
		String text = "text";
		String text2 = "text2";
		Collection<String> sourceUrls = new ArrayList<String>(Arrays.asList(new String[] {"source url1", "source url2"}));
		Collection<String> sourceUrls2 =  new ArrayList<String>(Arrays.asList(new String[] {"source url2"}));
		String notes = "notes";
		String notes2 = "notes2";
		String standardLicenseHeader = "Standard license header";
		String standardLicenseHeader2 = "Standard license header2";
		String template = "template";
		String template2 = "template2";
		
		SpdxListedLicense stdl = new SpdxListedLicense(name, id, text,
				sourceUrls, notes, standardLicenseHeader, template, true,null, null, false, null);
		assertTrue(stdl.equivalent(stdl));
		IModelStore store = new InMemSpdxStore();
		SpdxListedLicense stdl2 = new SpdxListedLicense(store, SpdxConstants.LISTED_LICENSE_URL, id, stdl.getCopyManager(), true);
		stdl2.setLicenseText(text2);
		stdl2.setName(name2);
		stdl2.setSeeAlso(sourceUrls2);
		stdl2.setComment(notes2);
		stdl2.setStandardLicenseHeader(standardLicenseHeader2);
		stdl2.setStandardLicenseTemplate(template2);
		assertTrue(stdl2.equivalent(stdl));
		
		SpdxListedLicense stdl3 = new SpdxListedLicense(store, SpdxConstants.LISTED_LICENSE_URL, "Apache-2.0", stdl.getCopyManager(), true);
		stdl3.setLicenseText(text);
		stdl3.setSeeAlso(sourceUrls);
		stdl3.setComment(notes);
		stdl3.setStandardLicenseHeader(standardLicenseHeader);
		stdl3.setStandardLicenseTemplate(template);
		
		assertTrue(stdl2.equivalent(stdl));
		assertFalse(stdl.equivalent(stdl3));
	}
	

	public void testSetHeaderTemplate() throws InvalidSPDXAnalysisException {

		String name = "name";
		String id = "AFL-3.0";
		String text = "text";
		Collection<String> sourceUrls = new ArrayList<String>(Arrays.asList(new String[] {"source url2", "source url3"}));
		String notes = "notes";
		String standardLicenseHeader = "Standard license header";
		String standardLicenseHeaderTemplate = "Standard license<<beginOptional>>optional<<endOptional>> header";
		String template = "template";
		SpdxListedLicense stdl = new SpdxListedLicense(name, id, text,
				sourceUrls, notes, standardLicenseHeader, template, true,null, null, false, null);
		stdl.setStandardLicenseHeaderTemplate(standardLicenseHeaderTemplate);
		assertEquals(standardLicenseHeaderTemplate, stdl.getStandardLicenseHeaderTemplate());
		SpdxListedLicense compLic = new SpdxListedLicense(stdl.getModelStore(), stdl.getDocumentUri(), id, stdl.getCopyManager(), false);
		assertEquals(standardLicenseHeaderTemplate, compLic.getStandardLicenseHeaderTemplate());
		
		String newHeaderTemplate = "New standard license template";
		compLic.setStandardLicenseHeaderTemplate(newHeaderTemplate);
		assertEquals(newHeaderTemplate, compLic.getStandardLicenseHeaderTemplate());
		SpdxListedLicense compLic2 = new SpdxListedLicense(stdl.getModelStore(), stdl.getDocumentUri(), id, stdl.getCopyManager(), false);
		assertEquals(newHeaderTemplate, compLic2.getStandardLicenseHeaderTemplate());
		List<String> verify = stdl.verify();
		assertEquals(0, verify.size());
		verify = compLic.verify();
		assertEquals(0, verify.size());
	}
	

	public void testSetHeaderTemplateHtml() throws InvalidSPDXAnalysisException, InvalidLicenseTemplateException {
		String name = "name";
		String id = "AFL-3.0";
		String text = "text";
		Collection<String> sourceUrls = new ArrayList<String>(Arrays.asList(new String[] {"source url2", "source url3"}));
		String notes = "notes";
		String standardLicenseHeader = "Standard license header";
		String standardLicenseHeaderTemplate = "Standard license<<beginOptional>>optional<<endOptional>> header";
		String template = "template";
		String standardLicenseHeaderHtml = "<h1>licenseHeader</h1>";
		String textHtml = "<h1>text</h1>";
		SpdxListedLicense stdl = new SpdxListedLicense(name, id, text,
				sourceUrls, notes, standardLicenseHeader, template, false, 
				true, textHtml, false, null);
		stdl.setStandardLicenseHeaderTemplate(standardLicenseHeaderTemplate);
		stdl.setLicenseHeaderHtml(standardLicenseHeaderHtml);
		assertEquals(textHtml, stdl.getLicenseTextHtml());
		assertEquals(standardLicenseHeaderHtml, stdl.getLicenseHeaderHtml());
		String newStandardLicenseHeaderHtml = "<h2>licenseHeader2</h2>";
		String newTextHtml = "<h2>text2</h2>";
		stdl.setLicenseTextHtml(newTextHtml);
		stdl.setLicenseHeaderHtml(newStandardLicenseHeaderHtml);
		assertEquals(newTextHtml, stdl.getLicenseTextHtml());
		assertEquals(newStandardLicenseHeaderHtml, stdl.getLicenseHeaderHtml());
	}
	
	public void testCrossRef() throws InvalidSPDXAnalysisException {
		String name = "name";
		String id = "AFL-3.0";
		String text = "text";
		String url1 = "http://url1";
		String url2 = "http://url2";
		Boolean isLive1 = true;
		Boolean isLive2 = false;
		Boolean isWayback1 = true;
		Boolean isWayback2 = false;
		Boolean isValid1 = true;
		Boolean isValid2 = false;
		String match1 = "true";
		String match2 = "false";
		String date1 = "today";
		String date2 = "tomorrow";
		Integer order1 = 1;
		Integer order2 = 2;
		Collection<String> sourceUrls = new ArrayList<String>(Arrays.asList(new String[] {url1, url2}));
		String notes = "notes";
		String standardLicenseHeader = "Standard license header";
		String template = "template";
		String licenseHtml = "<html>html</html>";
		SpdxListedLicense stdl = new SpdxListedLicense(name, id, text,
				sourceUrls, notes, standardLicenseHeader, template, true, true, licenseHtml, false, null);
		assertEquals(0, stdl.getCrossRef().size());
		CrossRef testCrossRef1 = stdl.createCrossRef(url1)
				.setLive(isLive1)
				.setMatch(match1)
				.setOrder(order1)
				.setTimestamp(date1)
				.setValid(isValid1)
				.setWayBackLink(isWayback1)
				.build();
		CrossRef testCrossRef2 = stdl.createCrossRef(url2)
				.setLive(isLive2)
				.setMatch(match2)
				.setOrder(order2)
				.setTimestamp(date2)
				.setValid(isValid2)
				.setWayBackLink(isWayback2)
				.build();
		stdl.getCrossRef().add(testCrossRef1);
		assertEquals(1, stdl.getCrossRef().size());
		for (CrossRef ref:stdl.getCrossRef()) {
			assertTrue(testCrossRef1.equivalent(ref));
			assertEquals(url1, testCrossRef1.getUrl().get());
		}
		stdl.getCrossRef().add(testCrossRef2);
		assertEquals(2, stdl.getCrossRef().size());
		for (CrossRef ref:stdl.getCrossRef()) {
			if (ref.getUrl().get().equals(url1)) {
				assertTrue(testCrossRef1.equivalent(ref));
			} else {
				assertTrue(testCrossRef2.equivalent(ref));
			}
		}
	}
}
