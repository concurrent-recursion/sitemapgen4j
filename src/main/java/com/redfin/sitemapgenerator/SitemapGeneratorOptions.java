package com.redfin.sitemapgenerator;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

class SitemapGeneratorOptions extends
		AbstractSitemapGeneratorOptions<SitemapGeneratorOptions> {

	public SitemapGeneratorOptions(URL baseUrl, Path baseDir) {
		super(baseUrl, baseDir);
	}
	
	public SitemapGeneratorOptions(String baseUrl, Path baseDir) throws MalformedURLException {
		this(new URL(baseUrl), baseDir);
	}
	
	public SitemapGeneratorOptions(URL baseUrl) {
		super(baseUrl);
	}
	
	public SitemapGeneratorOptions(String baseUrl) throws MalformedURLException {
		super(new URL(baseUrl));
	}

}
