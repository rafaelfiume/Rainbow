package com.rafaelfiume.rainbow.acceptance.endtoend;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.yatspec.junit.Notes;
import com.googlecode.yatspec.junit.SpecResultListener;
import com.googlecode.yatspec.junit.SpecRunner;
import com.googlecode.yatspec.junit.WithCustomResultListeners;
import com.googlecode.yatspec.plugin.sequencediagram.ByNamingConventionMessageProducer;
import com.googlecode.yatspec.plugin.sequencediagram.SequenceDiagramGenerator;
import com.googlecode.yatspec.plugin.sequencediagram.SequenceDiagramMessage;
import com.googlecode.yatspec.plugin.sequencediagram.SvgWrapper;
import com.googlecode.yatspec.rendering.html.DontHighlightRenderer;
import com.googlecode.yatspec.rendering.html.HtmlResultRenderer;
import com.googlecode.yatspec.rendering.html.index.HtmlIndexRenderer;
import com.googlecode.yatspec.state.givenwhenthen.ActionUnderTest;
import com.googlecode.yatspec.state.givenwhenthen.StateExtractor;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.rafaelfiume.rainbow.acceptance.SalumeStackHostsResolution.supplierBaseUrl;
import static java.lang.System.getenv;
import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.StringUtils.trim;

@RunWith(SpecRunner.class)
public class StatusPageWalkingSkeletonTest extends TestState implements WithCustomResultListeners {

    public static final String STATUS_PAGE_URI = supplierBaseUrl() + "/status";

    @Notes("There's no need to check every single case scenario here.\n" +
            "This is a black box test meant to test how the apps that compose the system behave together.\n" +
            "We leave the more specific assertions to the acceptance tests in each app.\n" +
            "" +
            "Also note that there's no sad path.\n")
    @Test
    public void happyPath() throws Exception {
        // given Salume stack is up and running

        when(aClientRequestsSupplierStatusPage());

        then(theStatusOfTheApp(), is("OK"));
    }

    private ActionUnderTest aClientRequestsSupplierStatusPage() {
        return (givens, capturedInputAndOutputs) -> {
            this.statusPageResponse = new TestRestTemplate().getForEntity(getenv("SUPPLIER_STAGING_URL") + "/status", String.class);

            // this is what makes the sequence diagram magic happens
            capturedInputAndOutputs.add("Status Page request from client to Supplier", getenv("SUPPLIER_STAGING_URL") + "/status");

            return capturedInputAndOutputs;
        };
    }

    private StateExtractor<String> theStatusOfTheApp() {
        return inputAndOutputs -> {

            String body = this.statusPageResponse.getBody();

            capturedInputAndOutputs.add("Status Page response from Supplier to client", body);

            String firstLine = body.split(lineSeparator())[0];
            return trim(firstLine.split("is:")[1]);
        };
    }

    //////////////////// Test Infrastructure Stuff //////////////

    @After
    public void generateSequenceDiagram() {
        Sequence<SequenceDiagramMessage> messages = sequence(new ByNamingConventionMessageProducer().messages(capturedInputAndOutputs));
        capturedInputAndOutputs.add("Sequence Diagram", sequenceDiagramGenerator.generateSequenceDiagram(messages));
    }

    @Override
    public Iterable<SpecResultListener> getResultListeners() throws Exception {
        return sequence(
                new HtmlResultRenderer().
                        withCustomHeaderContent(SequenceDiagramGenerator.getHeaderContentForModalWindows()).
                        withCustomRenderer(SvgWrapper.class, new DontHighlightRenderer()),
                new HtmlIndexRenderer()).
                safeCast(SpecResultListener.class);
    }

    private SequenceDiagramGenerator sequenceDiagramGenerator;

    private ResponseEntity<String> statusPageResponse;

    @Before
    public void setUp() {
        this.sequenceDiagramGenerator = new SequenceDiagramGenerator();
    }

}
