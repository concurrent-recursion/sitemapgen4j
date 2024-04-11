package com.redfin.sitemapgenerator;

import org.xmlunit.builder.Input;
import org.xmlunit.validation.Languages;
import org.xmlunit.validation.ValidationResult;
import org.xmlunit.validation.Validator;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUtil {
    private TestUtil(){}


    public static void isValidSitemap(String xml) throws IOException {
        Validator validator = Validator.forLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
        try(InputStream is = TestUtil.class.getResourceAsStream("sitemap.xsd")) {
            validator.setSchemaSource(new StreamSource(is));ValidationResult vr = validator.validateInstance(Input.fromString(xml).build());
            assertTrue(vr.isValid(), vr.getProblems().toString());
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public static OffsetDateTime getEpochOffsetDateTime() {
        return OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);
    }
}
