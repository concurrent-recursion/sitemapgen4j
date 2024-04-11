package com.redfin.sitemapgenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GoogleNewsSitemapUrlTest {
	

	
	@Test
	void testSimpleUrl(@TempDir Path tempDir) throws Exception {
		GoogleNewsSitemapGenerator wsg = GoogleNewsSitemapGenerator.builder("https://www.example.com", tempDir)
			.dateFormat(W3CDateFormat.SECOND.withZone(ZoneOffset.UTC)).build();
		GoogleNewsSitemapUrl url = new GoogleNewsSitemapUrl("https://www.example.com/index.html", TestUtil.getEpochOffsetDateTime(), "Example Title", "The Example Times", "en");
		wsg.addUrl(url);
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			String.format("<urlset xmlns=\"%s\" xmlns:%s=\"%s\" >\n",
					SitemapConstants.SITEMAP_NS_URI, SitemapConstants.GOOGLE_NEWS_NS, SitemapConstants.GOOGLE_NEWS_NS_URI) +
			"  <url>\n" + 
			"    <loc>https://www.example.com/index.html</loc>\n" +
			"    <news:news>\n" + 
			"      <news:publication>\n" +
			"        <news:name>The Example Times</news:name>\n" +
			"        <news:language>en</news:language>\n" +
			"      </news:publication>\n" +
			"      <news:publication_date>1970-01-01T00:00:00Z</news:publication_date>\n" +
			"      <news:title>Example Title</news:title>\n" +
			"    </news:news>\n" + 
			"  </url>\n" + 
			"</urlset>";
		String sitemap = writeSingleSiteMap(wsg);
		assertEquals(expected, sitemap);
	}
	
	@Test
	void testKeywords(@TempDir Path tempDir) throws Exception {
		GoogleNewsSitemapGenerator wsg = GoogleNewsSitemapGenerator.builder("https://www.example.com", tempDir)
			.dateFormat(W3CDateFormat.SECOND.withZone(ZoneOffset.UTC)).build();
		GoogleNewsSitemapUrl url = new GoogleNewsSitemapUrl.Options("https://www.example.com/index.html", TestUtil.getEpochOffsetDateTime(), "Example Title", "The Example Times", "en")
			.keywords("Klaatu", "Barrata", "Nicto")
			.build();
		wsg.addUrl(url);
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			String.format("<urlset xmlns=\"%s\" xmlns:%s=\"%s\" >\n",
					SitemapConstants.SITEMAP_NS_URI, SitemapConstants.GOOGLE_NEWS_NS, SitemapConstants.GOOGLE_NEWS_NS_URI) +
			"  <url>\n" + 
			"    <loc>https://www.example.com/index.html</loc>\n" +
			"    <news:news>\n" + 
			"      <news:publication>\n" +
			"        <news:name>The Example Times</news:name>\n" +
			"        <news:language>en</news:language>\n" +
			"      </news:publication>\n" +
			"      <news:publication_date>1970-01-01T00:00:00Z</news:publication_date>\n" +
			"      <news:title>Example Title</news:title>\n" +
			"      <news:keywords>Klaatu, Barrata, Nicto</news:keywords>\n" +
			"    </news:news>\n" + 
			"  </url>\n" + 
			"</urlset>";
		String sitemap = writeSingleSiteMap(wsg);
		assertEquals(expected, sitemap);
	}

	@Test
	void testGenres(@TempDir Path tempDir) throws Exception {
		GoogleNewsSitemapGenerator wsg = GoogleNewsSitemapGenerator.builder("https://www.example.com", tempDir)
			.dateFormat(W3CDateFormat.SECOND.withZone(ZoneOffset.UTC)).build();
		GoogleNewsSitemapUrl url = new GoogleNewsSitemapUrl.Options("https://www.example.com/index.html", TestUtil.getEpochOffsetDateTime(), "Example Title", "The Example Times", "en")
			.genres("persbericht")
			.build();
		wsg.addUrl(url);
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			String.format("<urlset xmlns=\"%s\" xmlns:%s=\"%s\" >\n",
					SitemapConstants.SITEMAP_NS_URI, SitemapConstants.GOOGLE_NEWS_NS, SitemapConstants.GOOGLE_NEWS_NS_URI) +
			"  <url>\n" +
			"    <loc>https://www.example.com/index.html</loc>\n" +
			"    <news:news>\n" +
			"      <news:publication>\n" +
			"        <news:name>The Example Times</news:name>\n" +
			"        <news:language>en</news:language>\n" +
			"      </news:publication>\n" +
			"      <news:genres>persbericht</news:genres>\n" +
			"      <news:publication_date>1970-01-01T00:00:00Z</news:publication_date>\n" +
			"      <news:title>Example Title</news:title>\n" +
			"    </news:news>\n" +
			"  </url>\n" +
			"</urlset>";
		String sitemap = writeSingleSiteMap(wsg);
		assertEquals(expected, sitemap);
	}
	
	private String writeSingleSiteMap(GoogleNewsSitemapGenerator wsg) throws IOException {
		List<Path> files = wsg.write();
		assertEquals(1, files.size(), "Too many files: " + files.toString());
		assertEquals("sitemap.xml", files.get(0).getFileName().toString(), "Sitemap misnamed");
		return Files.readString(files.get(0));
	}
}
