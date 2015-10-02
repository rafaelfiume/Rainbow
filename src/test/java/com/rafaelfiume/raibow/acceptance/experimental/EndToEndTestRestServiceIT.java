package com.rafaelfiume.raibow.acceptance.experimental;

import com.googlecode.totallylazy.Sequence;
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
import com.rafaelfiume.raibow.RaibowApplication;
import com.rafaelfiume.raibow.acceptance.experimental.config.ShutdownJettyTestExecutionListener;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import static com.googlecode.totallylazy.Sequences.sequence;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.parseMediaType;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

@Ignore // Testing experimental feature. Disabled for now.
@RunWith(SpecRunner.class)
@SpringApplicationConfiguration(classes = RaibowApplication.class)
@WebIntegrationTest("debug=true")
@TestExecutionListeners(
        listeners = ShutdownJettyTestExecutionListener.class,
        mergeMode = MERGE_WITH_DEFAULTS
)
public class EndToEndTestRestServiceIT extends TestState implements WithCustomResultListeners {

    public static final String RAINBOW_END_TO_END_TEST_URI = "http://localhost:8080/rainbow/test/end-to-end/";

    public static final MediaType TEXT_PLAIN_CHARSET_UTF8 = parseMediaType("text/plain;charset=utf-8");

    private SequenceDiagramGenerator sequenceDiagramGenerator;

    private ResponseEntity<String> statusPageResponse;

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Before
    public void setUp() {
        this.sequenceDiagramGenerator = new SequenceDiagramGenerator();
    }

    @Test
    public void happyPath() throws Exception {
       // given Salume stack is working fine

        when(aClientRequestsAnEndToEndTestRequest());

        then(theContentType(), is(TEXT_PLAIN_CHARSET_UTF8));
        then(theStatusPage(), hasHttpStatusCode(OK));
        then(endToEndTestResponse(), is("OK"));
    }

    private ActionUnderTest aClientRequestsAnEndToEndTestRequest() {
        return (givens, capturedInputAndOutputs) -> {
            this.statusPageResponse = new TestRestTemplate().getForEntity(RAINBOW_END_TO_END_TEST_URI, String.class);

            // this is what makes the sequence diagram magic happens
            capturedInputAndOutputs.add("End-to-end test request from client to Rainbow", RAINBOW_END_TO_END_TEST_URI);

            return capturedInputAndOutputs;
        };
    }

    private StateExtractor<HttpStatus> theStatusPage() {
        return inputAndOutputs -> {
            // this is what makes the sequence diagram magic happens
            capturedInputAndOutputs.add("End-to-end test response from Rainbow to client", statusPageResponse.getBody());

            return this.statusPageResponse.getStatusCode();
        };
    }

    private StateExtractor<String> endToEndTestResponse() {
        return inputAndOutputs -> this.statusPageResponse.getBody();
    }

    private StateExtractor<MediaType> theContentType() {
        return inputAndOutputs -> statusPageResponse.getHeaders().getContentType();
    }

    private Matcher<HttpStatus> hasHttpStatusCode(HttpStatus expected) {
        return new TypeSafeMatcher<HttpStatus>() {
            @Override
            protected boolean matchesSafely(HttpStatus result) {
                return expected == result;
            }

            @Override
            public void describeTo(Description description) {
                description.appendValue(expected);
            }
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

}
