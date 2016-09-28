import org.junit.Ignore;
import org.junit.Test;
import play.twirl.api.Content;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Simple (JUnit) tests that can call all parts of a play app.
 * If you are interested in mocking a whole application, see the wiki for more details.
 */
public class ApplicationTest {

    @Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertEquals(2, a);
    }

    @Test
    public void renderIndexTemplate() {
        Content html = views.html.index.render();
        assertEquals("text/html", html.contentType());
        assertTrue(html.body().contains("Finder"));
    }

    @Test
    @Ignore
    public void renderDataPageTemplateOfBuildingResource_ok(){

    }

    @Test
    @Ignore
    public void renderDataPageTemplateOfUnknownBuildingResource_returnException(){

    }

}
