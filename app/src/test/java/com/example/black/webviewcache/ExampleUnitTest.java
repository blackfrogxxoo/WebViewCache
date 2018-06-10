package com.example.black.webviewcache;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testIsImageUrl() {
        String url1 = "https://assets-cdn.github.com/images/modules/site/stories/customers/mapbox.png";
        assertTrue(Util.isImageUrl(url1));
        String url2 = "https://assets-cdn.github.com/images/modules/site/stories/customers/mapbox.png1";
        assertFalse(Util.isImageUrl(url2));
        String url3 = "https://www.sogou.com";
        assertFalse(Util.isImageUrl(url3));
    }
}