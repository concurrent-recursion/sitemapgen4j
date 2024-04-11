package com.redfin.sitemapgenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SitemapIndexGeneratorTest {

	private static final W3CDateFormat ZULU = new W3CDateFormat().withZone(ZoneOffset.UTC);

	private static final String INDEX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			String.format("<sitemapindex xmlns=\"%s\">\n", SitemapConstants.SITEMAP_NS_URI) +
			"  <sitemap>\n" + 
			"    <loc>https://www.example.com/sitemap1.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>https://www.example.com/sitemap2.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>https://www.example.com/sitemap3.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>https://www.example.com/sitemap4.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>https://www.example.com/sitemap5.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>https://www.example.com/sitemap6.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>https://www.example.com/sitemap7.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>https://www.example.com/sitemap8.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>https://www.example.com/sitemap9.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"  <sitemap>\n" + 
			"    <loc>https://www.example.com/sitemap10.xml</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"  </sitemap>\n" + 
			"</sitemapindex>";
	
	private static final String EXAMPLE = "https://www.example.com/";

	@Test
	void testTooManyUrls(@TempDir Path tempDir) throws Exception {
		SitemapIndexGenerator sig = new SitemapIndexGenerator.Options(EXAMPLE, tempDir).maxUrls(10).autoValidate(true).build();
		for (int i = 0; i < 9; i++) {
			sig.addUrl(EXAMPLE+i);
		}
		sig.addUrl(EXAMPLE+"9");
		assertThrows(RuntimeException.class, () -> sig.addUrl("https://www.example.com/just-one-more"), "too many URLs allowed");
	}
	@Test
	void testNoUrls(@TempDir Path tempDir) throws Exception {
		SitemapIndexGenerator sig = new SitemapIndexGenerator(EXAMPLE, tempDir);
		assertThrows(RuntimeException.class, sig::write, "Allowed write with no URLs");
	}
	
	@Test
	void testNoUrlsEmptyIndexAllowed(@TempDir Path tempDir) throws Exception {
		final Path sitemap = tempDir.resolve("sitemap.xml");
		SitemapIndexGenerator sig = new SitemapIndexGenerator.Options(EXAMPLE, sitemap).allowEmptyIndex(true).build();
		sig.write();
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				String.format("<sitemapindex xmlns=\"%s\">\n", SitemapConstants.SITEMAP_NS_URI) +
				"</sitemapindex>";
		String actual = Files.readString(sitemap);
		assertEquals(expected, actual);
		assertEquals(expected, sig.writeAsString());
	}
	
	@Test
	void testMaxUrls(@TempDir Path tempDir) throws Exception {
		final Path sitemap = tempDir.resolve("sitemap.xml");
		SitemapIndexGenerator sig = new SitemapIndexGenerator.Options(EXAMPLE, sitemap).autoValidate(true)
			.maxUrls(10).defaultLastMod(TestUtil.getEpochOffsetDateTime()).dateFormat(ZULU).build();
		for (int i = 1; i <= 9; i++) {
			sig.addUrl(EXAMPLE+"sitemap"+i+".xml");
		}
		sig.addUrl(EXAMPLE+"sitemap10.xml");
		sig.write();
		String actual = Files.readString(sitemap);
		assertEquals(INDEX, actual);
		assertEquals(INDEX, sig.writeAsString());
	}
	
	@Test
	void testOneUrl(@TempDir Path tempDir) throws Exception {
		final Path sitemap = tempDir.resolve("sitemap.xml");
		SitemapIndexGenerator sig = new SitemapIndexGenerator.Options(EXAMPLE, sitemap).dateFormat(ZULU).autoValidate(true).build();
		SitemapIndexUrl url = new SitemapIndexUrl(EXAMPLE+"index.html", TestUtil.getEpochOffsetDateTime());
		sig.addUrl(url);
		sig.write();
		String actual = Files.readString(sitemap);
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				String.format("<sitemapindex xmlns=\"%s\">\n", SitemapConstants.SITEMAP_NS_URI) +
				"  <sitemap>\n" + 
				"    <loc>https://www.example.com/index.html</loc>\n" + 
				"    <lastmod>1970-01-01</lastmod>\n" + 
				"  </sitemap>\n" + 
				"</sitemapindex>";
		assertEquals(expected, actual);
		assertEquals(expected, sig.writeAsString());
	}
	
	@Test
	void testAddByPrefix(@TempDir Path tempDir) throws IOException {
		final Path sitemap = tempDir.resolve("sitemap.xml");
		SitemapIndexGenerator sig = new SitemapIndexGenerator.Options(EXAMPLE, sitemap).autoValidate(true)
			.defaultLastMod(TestUtil.getEpochOffsetDateTime()).dateFormat(ZULU).build();
		sig.addUrls("sitemap", ".xml", 10);
		sig.write();
		String actual = Files.readString(sitemap);
		assertEquals(INDEX, actual);
		assertEquals(INDEX, sig.writeAsString());
	}
	
}
