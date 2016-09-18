package at.ac.tuwien.finder.datamanagement.mediation.organizational;

import at.ac.tuwien.finder.datamanagement.mediation.exception.DataAcquireException;
import at.ac.tuwien.finder.datamanagement.util.TISSApiGetRequest;
import at.ac.tuwien.finder.datamanagement.util.exception.TISSApiRequestFailedException;
import at.ac.tuwien.finder.taskmanagement.TaskManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

/**
 * This is an implementation of {@link TISSOrganizationCrawler} using the RESTFul Api of TISS.
 *
 * @author Kevin Haller
 * @see <a href="https://tiss.tuwien.ac.at/api/dokumentation">https://tiss.tuwien.ac.at/api/dokumentation</a>
 */
public class RestTISSOrganizationCrawler implements TISSOrganizationCrawler {

    private static final Logger logger = LoggerFactory.getLogger(RestTISSOrganizationCrawler.class);

    private static final String ORGANIZATION_NUMBER_API_CALL = "org_unit/number/";
    private static final String ORGANIZATION_OID_API_CALL = "org_unit/oid/";

    private TaskManager taskManager = TaskManager.getInstance();

    private Map<String, String> defaultParameterMap = Collections.emptyMap();

    private DocumentBuilderFactory documentFactory;
    private HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

    /**
     * Create an new crawler for the tiss organization using the restful api of TISS.
     */
    public RestTISSOrganizationCrawler() {
        documentFactory = DocumentBuilderFactory.newInstance();
    }

    /**
     * Create an new crawler for the tiss organization using the restful api of TISS. The given
     * parameters are added to every call to the tiss api.
     */
    public RestTISSOrganizationCrawler(Map<String, String> defaultParameterMap) {
        documentFactory = DocumentBuilderFactory.newInstance();
        this.defaultParameterMap = defaultParameterMap;
    }

    @Override
    public Document traverseOrganizationInformationByNumber(String number)
        throws DataAcquireException {
        logger.debug("Traverse the organization with the number {}", number);
        return traverseOrganizationInformation(getOrganizationInformationByNumber(number));
    }

    @Override
    public Document traverseOrganizationInformationWithNumberCodes(
        Collection<String> listOfNumberCodes) throws DataAcquireException {
        logger.debug("Traverse all organizations, which have one of the following number codes {}.",
            listOfNumberCodes);
        if (listOfNumberCodes == null) {
            throw new IllegalArgumentException("The given list of numbers must not be null.");
        }
        CompletionService<Document> completionService =
            new ExecutorCompletionService<>(taskManager.threadPool());
        for (String numberCode : listOfNumberCodes) {
            completionService.submit(() -> traverseOrganizationInformationByNumber(numberCode));
        }
        return prepareResponseForOrganizationsInformation(completionService,
            listOfNumberCodes.size());
    }

    @Override
    public Document traverseOrganizationInformationById(String oid) throws DataAcquireException {
        logger.info("Traverse the organization with the organizational id {}", oid);
        return traverseOrganizationInformation(getOrganizationInformationById(oid));
    }

    @Override
    public Document getOrganizationInformationByIds(Collection<String> listOfIds)
        throws DataAcquireException {
        logger.debug(
            "Gets teh information of all organizations, which have one of the following oids {}.",
            listOfIds);
        if (listOfIds == null) {
            throw new IllegalArgumentException("The given list of oids must not be null.");
        }
        CompletionService<Document> completionService =
            new ExecutorCompletionService<>(taskManager.threadPool());
        for (String oid : listOfIds) {
            completionService.submit(() -> getOrganizationInformationById(oid));
        }
        return prepareResponseForOrganizationsInformation(completionService, listOfIds.size());
    }

    @Override
    public Document traverseOrganizationInformationWithIds(Collection<String> listOfIds)
        throws DataAcquireException {
        logger.debug("Traverse all organizations, which have one of the following oids {}.",
            listOfIds);
        if (listOfIds == null) {
            throw new IllegalArgumentException("The given list of oids must not be null.");
        }
        CompletionService<Document> completionService =
            new ExecutorCompletionService<>(taskManager.threadPool());
        for (String oid : listOfIds) {
            completionService.submit(() -> traverseOrganizationInformationById(oid));
        }
        return prepareResponseForOrganizationsInformation(completionService, listOfIds.size());
    }

    /**
     * Prepares the response document containing the information of various organizations and
     * their sub-organization. The given completion service contains all the future responses, which
     * in total have to be as many as the given request count (otherwise the method will block).
     *
     * @param completionService completion service, which contains all the future responses.
     * @param requestCount      the number of requests that has been submitted to the completion service.
     * @return the response document.
     * @throws DataAcquireException if the document can not be created (unlikely).
     */
    private Document prepareResponseForOrganizationsInformation(
        CompletionService<Document> completionService, int requestCount)
        throws DataAcquireException {
        try {
            Document responseDocument = documentFactory.newDocumentBuilder().newDocument();
            Element organizationsRoot = responseDocument.createElement("organizations");
            for (int i = 0; i < requestCount; i++) {
                try {
                    Future<Document> receivedDocumentFuture = completionService.take();
                    Document receivedDocument = receivedDocumentFuture.get();
                    NodeList tuviennaNodes = receivedDocument.getElementsByTagName("tuvienna");
                    for (int n = 0; n < tuviennaNodes.getLength(); n++) {
                        organizationsRoot
                            .appendChild(responseDocument.importNode(tuviennaNodes.item(n), true));
                    }
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("{}", e);
                }
            }
            responseDocument.appendChild(organizationsRoot);
            return responseDocument;
        } catch (ParserConfigurationException e) {
            logger
                .error("The document builder for returning the response can not be established. {}",
                    e);
            throw new DataAcquireException(e);
        }
    }

    /**
     * The information of the organization in the root element as well as all the
     * descendants (sub-organization) of the given root organization as flatten xml document.
     *
     * @param headOrgUnit the root element of the traversal.
     * @return the information of the organization in the root element as well as all the
     * descendants (sub-organization) of the given root organization as flatten xml document.
     * @throws DataAcquireException if the document can not be created (unlikely).
     */
    private Document traverseOrganizationInformation(Document headOrgUnit)
        throws DataAcquireException {
        CompletionService<Document> completionService =
            new ExecutorCompletionService<>(taskManager.threadPool());
        try {
            Document responseDocument = documentFactory.newDocumentBuilder().newDocument();
            Element organisations = responseDocument.createElement("organizations");
            responseDocument.appendChild(organisations);
            Stack<Document> subOrganizationStack = new Stack<>();
            subOrganizationStack.push(headOrgUnit);
            while (!subOrganizationStack.empty()) {
                Document currentDoc = subOrganizationStack.pop();
                organisations
                    .appendChild(responseDocument.importNode(currentDoc.getFirstChild(), true));
                NodeList orgRefNodeList = currentDoc.getElementsByTagName("org_ref");
                // Fetch all sub-organizations.
                int submittedTasks = 0;
                for (int i = 0; i < orgRefNodeList.getLength(); i++) {
                    Element orgRefNode = (Element) orgRefNodeList.item(i);
                    String oid = orgRefNode.getAttribute("oid");
                    if (!oid.isEmpty()) {
                        logger.debug("Detected the sub-organization with the organization id {}",
                            oid);
                        completionService.submit(() -> getOrganizationInformationById(oid));
                        submittedTasks++;
                    } else {
                        String code = orgRefNode.getAttribute("code");
                        if (!code.isEmpty()) {
                            logger.debug("Detected the sub-organization with the number {}", code);
                            completionService
                                .submit(() -> getOrganizationInformationByNumber(code));
                            submittedTasks++;
                        } else {
                            continue;
                        }
                    }
                }
                // Push sub-organizations to the stack.
                for (int i = 0; i < submittedTasks; i++) {
                    try {
                        Future<Document> subOrgDocumentCallback = completionService.take();
                        Document subOrgDocument = subOrgDocumentCallback.get();
                        subOrganizationStack.push(subOrgDocument);
                    } catch (InterruptedException | ExecutionException e) {
                        logger.error(e.getMessage());
                        throw new DataAcquireException(e);
                    }
                }
            }
            return responseDocument;
        } catch (ParserConfigurationException e) {
            throw new DataAcquireException("The building of the xml response failed.");
        }
    }

    @Override
    public Document getOrganizationInformationByNumber(String number) throws DataAcquireException {
        logger.debug("Get the organization with the number {}", number);
        try {
            return TISSApiGetRequest
                .call(httpClientBuilder, ORGANIZATION_NUMBER_API_CALL + number, defaultParameterMap)
                .xmlDocument();
        } catch (TISSApiRequestFailedException e) {
            throw new DataAcquireException(e);
        }
    }

    @Override
    public Document getOrganizationInformationById(String oid) throws DataAcquireException {
        logger.debug("Get the organization with the organization id {}", oid);
        try {
            return TISSApiGetRequest
                .call(httpClientBuilder, ORGANIZATION_OID_API_CALL + oid, defaultParameterMap)
                .xmlDocument();
        } catch (TISSApiRequestFailedException e) {
            throw new DataAcquireException(e);
        }
    }

    @Override
    public void close() throws Exception {
        taskManager.close();
    }
}
