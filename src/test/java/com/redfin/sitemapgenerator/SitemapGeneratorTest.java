package com.redfin.sitemapgenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.*;

class SitemapGeneratorTest {
	
	private static final String SITEMAP_PLUS_ONE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
		"<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" >\n" + 
		"  <url>\n" + 
		"    <loc>https://www.example.com/just-one-more</loc>\n" + 
		"  </url>\n" + 
		"</urlset>";
	private static final String SITEMAP1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
		"<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" >\n" + 
		"  <url>\n" + 
		"    <loc>https://www.example.com/0</loc>\n" + 
		"  </url>\n" + 
		"  <url>\n" + 
		"    <loc>https://www.example.com/1</loc>\n" + 
		"  </url>\n" + 
		"  <url>\n" + 
		"    <loc>https://www.example.com/2</loc>\n" + 
		"  </url>\n" + 
		"  <url>\n" + 
		"    <loc>https://www.example.com/3</loc>\n" + 
		"  </url>\n" + 
		"  <url>\n" + 
		"    <loc>https://www.example.com/4</loc>\n" + 
		"  </url>\n" + 
		"  <url>\n" + 
		"    <loc>https://www.example.com/5</loc>\n" + 
		"  </url>\n" + 
		"  <url>\n" + 
		"    <loc>https://www.example.com/6</loc>\n" + 
		"  </url>\n" + 
		"  <url>\n" + 
		"    <loc>https://www.example.com/7</loc>\n" + 
		"  </url>\n" + 
		"  <url>\n" + 
		"    <loc>https://www.example.com/8</loc>\n" + 
		"  </url>\n" + 
		"  <url>\n" + 
		"    <loc>https://www.example.com/9</loc>\n" + 
		"  </url>\n" + 
		"</urlset>";
	private static final String SITEMAP2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
		"<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" >\n" + 
		"  <url>\n" + 
		"    <loc>https://www.example.com/10</loc>\n" + 
		"  </url>\n" + 
		"  <url>\n" + 
		"    <loc>https://www.example.com/11</loc>\n" + 
		"  </url>\n" + 
		"  <url>\n" + 
		"    <loc>https://www.example.com/12</loc>\n" + 
		"  </url>\n" + 
		"  <url>\n" + 
		"    <loc>https://www.example.com/13</loc>\n" + 
		"  </url>\n" + 
		"  <url>\n" + 
		"    <loc>https://www.example.com/14</loc>\n" + 
		"  </url>\n" + 
		"  <url>\n" + 
		"    <loc>https://www.example.com/15</loc>\n" + 
		"  </url>\n" + 
		"  <url>\n" + 
		"    <loc>https://www.example.com/16</loc>\n" + 
		"  </url>\n" + 
		"  <url>\n" + 
		"    <loc>https://www.example.com/17</loc>\n" + 
		"  </url>\n" + 
		"  <url>\n" + 
		"    <loc>https://www.example.com/18</loc>\n" + 
		"  </url>\n" + 
		"  <url>\n" + 
		"    <loc>https://www.example.com/19</loc>\n" + 
		"  </url>\n" + 
		"</urlset>";

	
	@Test
	void testSimpleUrl(@TempDir Path tempDir) throws Exception {
		WebSitemapGenerator wsg = new WebSitemapGenerator("https://www.example.com", tempDir.resolve("simpleurl.xml"));
		wsg.addUrl("https://www.example.com/index.html");
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" >\n" + 
			"  <url>\n" + 
			"    <loc>https://www.example.com/index.html</loc>\n" + 
			"  </url>\n" + 
			"</urlset>";
		String sitemap = writeSingleSiteMap(wsg);
		assertEquals(expected, sitemap);
		
		TestUtil.isValidSitemap(sitemap);
	}
	
	@Test
	void testTwoUrl(@TempDir Path tempDir) throws Exception {
		WebSitemapGenerator wsg = new WebSitemapGenerator("https://www.example.com", tempDir.resolve("sitemap.xml"));
		wsg.addUrls("https://www.example.com/index.html", "https://www.example.com/index2.html");
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" >\n" + 
			"  <url>\n" + 
			"    <loc>https://www.example.com/index.html</loc>\n" + 
			"  </url>\n" + 
			"  <url>\n" + 
			"    <loc>https://www.example.com/index2.html</loc>\n" + 
			"  </url>\n" + 
			"</urlset>";
		String sitemap = writeSingleSiteMap(wsg);
		assertEquals(expected, sitemap);
		
		TestUtil.isValidSitemap(sitemap);
	}
	
	@Test
	void testAllUrlOptions(@TempDir Path tempDir) throws Exception {
		WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", tempDir).dateFormat(W3CDateFormat.AUTO.withZone(ZoneOffset.UTC)).autoValidate(true).build();
		WebSitemapUrl url = new WebSitemapUrl.Options("https://www.example.com/index.html")
			.changeFreq(ChangeFreq.DAILY).lastMod(TestUtil.getEpochOffsetDateTime()).priority(1.0).build();
		wsg.addUrl(url);
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			String.format("<urlset xmlns=\"%s\" >\n", SitemapConstants.SITEMAP_NS_URI) +
			"  <url>\n" + 
			"    <loc>https://www.example.com/index.html</loc>\n" + 
			"    <lastmod>1970-01-01</lastmod>\n" + 
			"    <changefreq>daily</changefreq>\n" + 
			"    <priority>1.0</priority>\n" + 
			"  </url>\n" + 
			"</urlset>";
		String sitemap = writeSingleSiteMap(wsg);
		assertEquals(expected, sitemap);

		TestUtil.isValidSitemap(sitemap);
	}
	
	@Test
	void testBadUrl(@TempDir Path tempDir) throws Exception {
		WebSitemapGenerator wsg = new WebSitemapGenerator("https://www.example.com", tempDir);
		assertThrows(RuntimeException.class, () -> wsg.addUrl("https://example.com/index.html"), "wrong domain allowed to be added");
	}
	
	@Test
	void testSameDomainDifferentSchemeOK(@TempDir Path tempDir) throws Exception {
		WebSitemapGenerator wsg = new WebSitemapGenerator("https://www.example.com", tempDir);
			
		wsg.addUrl("https://www.example.com/index.html");
		
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				String.format("<urlset xmlns=\"%s\" >\n", SitemapConstants.SITEMAP_NS_URI) +
				"  <url>\n" + 
				"    <loc>https://www.example.com/index.html</loc>\n" + 
				"  </url>\n" + 
				"</urlset>";
		String sitemap = writeSingleSiteMap(wsg);
		assertEquals(expected, sitemap);
		
		TestUtil.isValidSitemap(sitemap);
	}
	
	@Test
	void testDoubleWrite(@TempDir Path tempDir) throws Exception {
		WebSitemapGenerator wsg = new WebSitemapGenerator("https://www.example.com", tempDir);
		wsg.addUrl("https://www.example.com/index.html");
		writeSingleSiteMap(wsg);

		assertThrows(RuntimeException.class, wsg::write, "Double-write is not allowed");
	}
	
	@Test
	void testEmptyWrite(@TempDir Path tempDir) throws Exception {
		WebSitemapGenerator wsg = new WebSitemapGenerator("https://www.example.com", tempDir);
		assertThrows(RuntimeException.class, wsg::write, "Empty write is not allowed");
	}
	
	@Test
	void testSuffixPresent(@TempDir Path tempDir) throws MalformedURLException {
		WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", tempDir).suffixStringPattern("01").build();
        wsg.addUrl("https://www.example.com/url1");
        wsg.addUrl("https://www.example.com/url2");
		List<Path> files = wsg.write();
		assertEquals("sitemap01.xml", files.get(0).getFileName().toString(), "Sitemap has a suffix now");
	}
	
	@Test
	void testNullSuffixPassed(@TempDir Path tempDir) throws MalformedURLException {
        WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", tempDir).suffixStringPattern("").build();
        wsg.addUrl("https://www.example.com/url1");
        wsg.addUrl("https://www.example.com/url2");
        List<Path> files = wsg.write();
        assertEquals("sitemap.xml", files.get(0).getFileName().toString(), "Sitemap has a suffix now");
    }
	
	@Test
	void testTooManyUrls(@TempDir Path tempDir) throws Exception {
		WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", tempDir).allowMultipleSitemaps(false).build();
		for (int i = 0; i < SitemapConstants.MAX_URLS_PER_SITEMAP; i++) {
			wsg.addUrl("https://www.example.com/"+i);
		}
		assertThrows(RuntimeException.class, () -> wsg.addUrl("https://www.example.com/just-one-more"), "too many URLs allowed");
	}
	
	@Test
	void testMaxUrlsPlusOne(@TempDir Path tempDir) throws Exception {
		WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", tempDir).autoValidate(true).maxUrls(10).build();
		for (int i = 0; i < 9; i++) {
			wsg.addUrl("https://www.example.com/"+i);
		}
		wsg.addUrl("https://www.example.com/9");
		wsg.addUrl("https://www.example.com/just-one-more");
		String actual = Files.readString(tempDir.resolve( "sitemap1.xml"));
		assertEquals(SITEMAP1, actual, "sitemap1 didn't match");
		List<Path> files = wsg.write();
		assertEquals(2, files.size());
		assertEquals("sitemap1.xml", files.get(0).getFileName().toString(), "First sitemap was misnamed");
		assertEquals("sitemap2.xml", files.get(1).getFileName().toString(), "Second sitemap was misnamed");
		actual = Files.readString(files.get(1));
		assertEquals(SITEMAP_PLUS_ONE, actual, "sitemap2 didn't match");
		
		TestUtil.isValidSitemap(actual);
	}
	
	@Test
	void testMaxUrls(@TempDir Path tempDir) throws Exception {
		WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", tempDir).autoValidate(true).maxUrls(10).build();
		for (int i = 0; i < 9; i++) {
			wsg.addUrl("https://www.example.com/"+i);
		}
		wsg.addUrl("https://www.example.com/9");
		String actual = writeSingleSiteMap(wsg);
		assertEquals(SITEMAP1, actual, "sitemap didn't match");
		
		TestUtil.isValidSitemap(actual);
	}
	
	@Test
	void testMaxUrlsTimesTwo(@TempDir Path tempDir) throws Exception {
		WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", tempDir).autoValidate(true).maxUrls(10).build();
		for (int i = 0; i < 19; i++) {
			wsg.addUrl("https://www.example.com/"+i);
		}
		wsg.addUrl("https://www.example.com/19");
		List<Path> files = wsg.write();
		
		assertEquals(2, files.size());
		assertEquals("sitemap1.xml", files.get(0).getFileName().toString(), "First sitemap was misnamed");
		assertEquals("sitemap2.xml", files.get(1).getFileName().toString(), "Second sitemap was misnamed");
		
		String actual = Files.readString(files.get(0));
		assertEquals(SITEMAP1, actual, "sitemap1 didn't match");
		
		TestUtil.isValidSitemap(actual);
		
		actual = Files.readString(files.get(1));
		assertEquals(SITEMAP2, actual, "sitemap2 didn't match");
		
		TestUtil.isValidSitemap(actual);
	}
	
	@Test
	void testMaxUrlsTimesTwoPlusOne(@TempDir Path tempDir) throws Exception {
		WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", tempDir).autoValidate(true).maxUrls(10).build();
		for (int i = 0; i < 19; i++) {
			wsg.addUrl("https://www.example.com/"+i);
		}
		wsg.addUrl("https://www.example.com/19");
		wsg.addUrl("https://www.example.com/just-one-more");
		List<Path> files = wsg.write();
		
		assertEquals(3, files.size());
		assertEquals("sitemap1.xml", files.get(0).getFileName().toString(), "First sitemap was misnamed");
		assertEquals("sitemap2.xml", files.get(1).getFileName().toString(), "Second sitemap was misnamed");
		assertEquals("sitemap3.xml", files.get(2).getFileName().toString(), "Third sitemap was misnamed");
		
		String expected = SITEMAP1;
		String actual = Files.readString(files.get(0));
		assertEquals(expected, actual, "sitemap1 didn't match");
		
		TestUtil.isValidSitemap(actual);
		
		expected = SITEMAP2;
		actual = Files.readString(files.get(1));
		assertEquals(expected, actual, "sitemap2 didn't match");
		
		TestUtil.isValidSitemap(actual);
		
		expected = SITEMAP_PLUS_ONE;
		actual = Files.readString(files.get(2));
		assertEquals(expected, actual, "sitemap3 didn't match");
		
		TestUtil.isValidSitemap(actual);
	}
	
	@Test
	void testGzip(@TempDir Path tempDir) throws Exception {
		WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", tempDir)
			.gzip(true).build();
		for (int i = 0; i < 9; i++) {
			wsg.addUrl("https://www.example.com/"+i);
		}
		wsg.addUrl("https://www.example.com/9");
		List<Path> files = wsg.write();
		assertEquals(1, files.size(), "Too many files: " + files.toString());
		assertEquals("sitemap.xml.gz", files.get(0).getFileName().toString(), "Sitemap misnamed");
		Path file = files.get(0);
		StringBuilder sb = new StringBuilder();
		try {
			InputStream fileStream = Files.newInputStream(file);
			GZIPInputStream gzipStream = new GZIPInputStream(fileStream);
			InputStreamReader reader = new InputStreamReader(gzipStream);
			int c;
			while ((c = reader.read()) != -1) {
				sb.append((char)c);
			}
			reader.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Files.delete(file);
		String actual = sb.toString();
		assertEquals(SITEMAP1, actual, "sitemap didn't match");
		TestUtil.isValidSitemap(actual);
	}
	
	@Test
	void testBaseDirIsNullThrowsNullPointerException() throws Exception {
		WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", null).autoValidate(true).maxUrls(10).build();
		wsg.addUrl("https://www.example.com/index.html");
		Exception e = null;
		try {
			wsg.write();
		} catch (Exception ex) {
			e = ex;
		}
        assertInstanceOf(NullPointerException.class, e);
		assertEquals("To write to files, baseDir must not be null", e.getMessage(), "Correct exception was not thrown");
	}
	
	@Test
	void testWriteAsStringsMoreThanOneString() throws Exception {
		WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", null).autoValidate(true).maxUrls(10).build();
		for (int i = 0; i < 9; i++) {
			wsg.addUrl("https://www.example.com/"+i);
		}
		wsg.addUrl("https://www.example.com/9");
		wsg.addUrl("https://www.example.com/just-one-more");
		List<String> siteMapsAsStrings = wsg.writeAsStrings();
		assertEquals(SITEMAP1, siteMapsAsStrings.get(0), "First string didn't match");
		assertEquals(SITEMAP_PLUS_ONE, siteMapsAsStrings.get(1), "Second string didn't match");
	}
	
	@Test
	void testWriteEmptySitemap(@TempDir Path tempDir) throws Exception {
		WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", tempDir).allowEmptySitemap(true).build();
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				String.format("<urlset xmlns=\"%s\" >\n", SitemapConstants.SITEMAP_NS_URI) +
				"</urlset>";
		String sitemap = writeSingleSiteMap(wsg);
		assertEquals(expected, sitemap);
	}
	
	@Test
	void testMaxUrlsAllowingEmptyDoesNotWriteExtraSitemap(@TempDir Path tempDir) throws Exception {
		WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", tempDir).allowEmptySitemap(true).maxUrls(10).build();
		for (int i = 0; i < 10; i++) {
			wsg.addUrl("https://www.example.com/"+i);
		}
		String sitemap = writeSingleSiteMap(wsg);
		assertEquals(SITEMAP1, sitemap);
	}
	
	private String writeSingleSiteMap(WebSitemapGenerator wsg) throws IOException {
		List<Path> files = wsg.write();
		assertEquals(1, files.size(), "Too many files: " + files);
		assertEquals("sitemap.xml", files.get(0).getFileName().toString(), "Sitemap misnamed");
		return Files.readString(files.get(0));
	}
}
