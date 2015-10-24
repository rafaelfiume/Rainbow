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
import com.googlecode.yatspec.state.givenwhenthen.GivensBuilder;
import com.googlecode.yatspec.state.givenwhenthen.StateExtractor;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.rafaelfiume.rainbow.acceptance.SalumeStackHostsResolution.supplierBaseUrl;
import static javax.xml.xpath.XPathConstants.NODESET;
import static javax.xml.xpath.XPathConstants.NUMBER;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.is;

@Notes("This is only to check all the apps are working well together. " +
        "For a more comprehensive acceptance test, see ProductAdvisorTest in Supplier.")
@RunWith(SpecRunner.class)
public class ProductAdvisorTest extends TestState implements WithCustomResultListeners {

    private ResponseEntity<String> response;

    @Test
    public void onlySuggestTraditionalProductsToExperts() throws Exception {
        given(availableProductsAre(cheap(), light(), traditional(), andPremium()));

        when(requestingBestOfferFor(aCustomerConsidered("Expert")));

        then(numberOfAdvisedProducts(), is(2)); // because there are only two products considered traditional in the db

        then(adviseCustomerTo(), buy("Traditional Salume", salume()));
    }

    private ActionUnderTest requestingBestOfferFor(final String profile) {
        return (givens, capturedInputAndOutputs1) -> {
            // TODO RF 20/10/2015 Extract the server address to a method in the abstract class

            this.response = new TestRestTemplate().getForEntity(advisorUrl(profile), String.class);

            // TODO RF 20/10/2015 Extract it to a method in the abstract class
            capturedInputAndOutputs.add("Salume advice request from customer to Supplier", advisorUrl(profile));

            return capturedInputAndOutputs;
        };
    }

    private String advisorUrl(String profile) {
        return supplierBaseUrl() + "/advise/for/" + profile;
    }

    private StateExtractor<Integer> numberOfAdvisedProducts() {
        return inputAndOutputs -> ((Double)
                xpath().evaluate("count(//product)", xmlFrom(response.getBody()), NUMBER)
        ).intValue();
    }

    private StateExtractor<Node> adviseCustomerTo() throws Exception {
        // TODO RF 20/10/2015 Extract it to a method in the abstract class
        capturedInputAndOutputs.add("Salume advice response from Supplier to customer", prettyPrint(xmlFrom(response.getBody())));

        return firstSuggestedProduct();
    }

    private StateExtractor<Node> firstSuggestedProduct() {
        return inputAndOutputs -> ((NodeList) xpath().evaluate("//product", xmlFrom(response.getBody()), NODESET)).item(0);
    }

    //
    // Matchers
    //

    private Matcher<Node> buy(String expected, @SuppressWarnings("unused") String salume) {
        return hasXPath("//product/name[text() = \"" + expected + "\"]");
    }

    //
    // Decorator methods to make the test read well
    //

    private String aCustomerConsidered(String profile) {
        return profile;
    }

    private String salume() {
        return "";
    }

    // TODO RF 24/10/2015 Data being retrieved from staging db, which means it depends on some specific values to run
    // This is quite fragile, but I'll can live with that for a while.
    private GivensBuilder availableProductsAre(Object... products) {
        return givens -> {
            // Data added using sql scripts. See: 01.create-table.sql

            // Consider doing something like the following in the future...
            // DatabaseUtilities.cleanAll();
            // DatabaseUtilities.addProducts(products);

            return givens;
        };
    }

    private Object cheap() {
        return null;
    }

    private Object light() {
        return null;
    }

    private Object traditional() {
        return null;
    }

    private Object andPremium() {
        return null;
    }

    //
    // Xml related
    //

    private static Document xmlFrom(String xml) throws Exception {
        Document xmlDoc = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(new InputSource(new StringReader(xml)));

        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(new DOMSource(xmlDoc), new DOMResult());
        return xmlDoc;
    }

    private static String prettyPrint(Node xml) throws Exception {
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        final Writer out = new StringWriter();
        transformer.transform(new DOMSource(xml), new StreamResult(out));
        return out.toString();
    }

    private XPath xpath() {
        return XPathFactory.newInstance().newXPath();
    }

    //////////////////// Test Infrastructure Stuff //////////////

    private SequenceDiagramGenerator sequenceDiagramGenerator;

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

    @Before
    public void setUp() {
        this.sequenceDiagramGenerator = new SequenceDiagramGenerator();
    }

}
