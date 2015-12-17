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
import com.rafaelfiume.rainbow.support.SpringCommitsAndClosesTestTransactionTransactor;
import com.rafaelfiume.salume.db.DbApplication;
import com.rafaelfiume.salume.db.advisor.PersistentProductBase;
import com.rafaelfiume.salume.domain.MoneyDealer;
import com.rafaelfiume.salume.domain.Product;
import com.rafaelfiume.salume.domain.ProductBuilder;
import com.rafaelfiume.salume.domain.Reputation;
import com.rafaelfiume.salume.matchers.AbstractAdvisedProductMatcherBuilder;
import com.rafaelfiume.salume.matchers.AdvisedProductMatcher;
import org.hamcrest.Matcher;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.rafaelfiume.rainbow.acceptance.SalumeStackHostsResolution.supplierBaseUrl;
import static com.rafaelfiume.salume.domain.ProductBuilder.a;
import static com.rafaelfiume.salume.support.Xml.*;
import static java.lang.String.format;
import static javax.xml.xpath.XPathConstants.NODESET;
import static javax.xml.xpath.XPathConstants.NUMBER;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.jdbc.JdbcTestUtils.deleteFromTables;

@Notes("This is only to check all the apps are working well together. " +
        "For a more comprehensive acceptance test, see ProductAdvisorTest in Supplier.")
@ContextConfiguration(classes = DbApplication.class)
@Transactional
@RunWith(SpecRunner.class)
public class ProductAdvisorTest extends TestState implements WithCustomResultListeners {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    private ResponseEntity<String> response;

    private MoneyDealer moneyDealer;

    private final SpringCommitsAndClosesTestTransactionTransactor transactor = new SpringCommitsAndClosesTestTransactionTransactor();
    private JdbcTemplate jdbcTemplate;
    private PersistentProductBase productBase;

    @Autowired
    public void setMoneyDealer(MoneyDealer moneyDealer) {
        this.moneyDealer = moneyDealer;
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    public void setProductBase(PersistentProductBase productBase) {
        this.productBase = productBase;
    }

    @Test
    public void onlySuggestTraditionalProductsToExperts() throws Exception {
        given(theAvailableProductsAre(
                a("(Normal) Cheap Salume")                   .at("EUR 11,11").regardedAs("NORMAL")     .with("49,99").percentageOfFat(),
                a("(Normal) Light Salume")                   .at("EUR 29,55").regardedAs("NORMAL")     .with("31,00").percentageOfFat(),
                a("(Normal) Salume")                         .at("EUR 57,37").regardedAs("NORMAL")     .with("33,50").percentageOfFat(),
                a("(Traditional Less Expensive) Salume")     .at("EUR 41,60").regardedAs("TRADITIONAL").with("37,00").percentageOfFat(),
                and(a("(Traditional More Expensive) Premium").at("EUR 73,23").regardedAs("TRADITIONAL").with("38,00").percentageOfFat())));

        when(requestingBestOfferFor(aCustomerConsidered("Expert")));

        then(theFirstSuggestionForCustomer(), isThe("(Traditional Less Expensive) Salume") .at("EUR 41,60").regardedAs("TRADITIONAL").with("37,00").percentageOfFat());
        and(numberOfAdvisedProducts(), is(2));
    }

    private GivensBuilder theAvailableProductsAre(ProductBuilder... products) {
        return givens -> {
            transactor.perform(() -> {
                deleteFromTables(jdbcTemplate, "salumistore.products");

                for (ProductBuilder p : products) {
                    productBase.add(p.build(moneyDealer));
                }
            });

            return givens;
        };
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

    private StateExtractor<Node> theFirstSuggestionForCustomer() throws Exception {
        // TODO RF 20/10/2015 Extract it to a method in the abstract class
        capturedInputAndOutputs.add("Salume advice response from Supplier to customer", prettyPrint(xmlFrom(response.getBody())));

        return firstSuggestedProduct();
    }

    private StateExtractor<Node> firstSuggestedProduct() {
        return inputAndOutputs -> ((NodeList) xpath().evaluate("//product", xmlFrom(response.getBody()), NODESET)).item(0);
    }

    //
    // Decorator methods to make the test read well
    //

    protected <ItemOfInterest> TestState and(StateExtractor<ItemOfInterest> extractor, Matcher<? super ItemOfInterest> matcher) throws Exception {
        return then(extractor, matcher);
    }

    private ProductBuilder and(ProductBuilder p) {
        return p;
    }

    //
    // Matchers
    //

    private Matcher<Node> buy(String expected, @SuppressWarnings("unused") String salume) {
        return hasXPath("//product/name[text() = \"" + expected + "\"]");
    }

    private AdvisedProductMatcherBuilder isThe(String productName) {
        return AdvisedProductMatcherBuilder.isThe(moneyDealer, productName);
    }

    // TODO RF 17/12/2015 Duplicated from Salume-Acceptance-Test (import Salume-Supplier module???)
    static class AdvisedProductMatcherBuilder extends AbstractAdvisedProductMatcherBuilder<Node> {

        static AdvisedProductMatcherBuilder isThe(MoneyDealer moneyDealer, String expectedProduct) {
            return new AdvisedProductMatcherBuilder(moneyDealer, expectedProduct);
        }

        private AdvisedProductMatcherBuilder(MoneyDealer moneyDealer, String expectedProductName) {
            super(moneyDealer, expectedProductName);
        }

        @Override
        public AdvisedProductMatcher percentageOfFat() {
            final Product product = expectedProduct();
            return new AdvisedProductMatcher(
                    product.getName(),
                    moneyDealer().format(product.getPrice()),
                    ReputationRepresentation.of(product.getReputation()),
                    product.getFatPercentage());
        }
    }

    // TODO RF 17/12/2015 Duplicated from Salume-Supplier (import Salume-Supplier module //Move to Salume-Domain???)
    static class ReputationRepresentation {

        private ReputationRepresentation() {
            // Not intended to be instantiate
        }

        public static String of(Reputation reputation) {
            switch (reputation) {
                case NORMAL:      return "special";
                case TRADITIONAL: return "traditional";
            }

            throw new IllegalArgumentException(format("unknown reputation %s", reputation));
        }
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
