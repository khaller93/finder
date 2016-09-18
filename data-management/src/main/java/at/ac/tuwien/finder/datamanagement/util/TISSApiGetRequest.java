package at.ac.tuwien.finder.datamanagement.util;

import at.ac.tuwien.finder.datamanagement.util.exception.TISSApiRequestFailedException;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * This class represents an HTTPClient that requests the TISS Restful API.
 *
 * @author Kevin Haller
 */
public class TISSApiGetRequest implements Callable<TISSApiResponse> {

    private static final Logger logger = LoggerFactory.getLogger(TISSApiGetRequest.class);

    public static final String TISS_API_URL = "https://tiss.tuwien.ac.at/api/";

    private HttpClientBuilder httpClientBuilder;
    private HttpUriRequest tissOrgApiRequest;

    /**
     * Creates a new http client that sends a request with the given apiCallString, which is
     * appended to the api url of tiss.
     * <p>
     * https://tiss.tuwien.ac.at/api/(:apiCallString)
     *
     * @param apiCallString the method of TISS, which shall be called.
     */
    public TISSApiGetRequest(String apiCallString) {
        tissOrgApiRequest = new HttpGet(TISS_API_URL + apiCallString);
        this.httpClientBuilder = HttpClientBuilder.create();
    }

    /**
     * Creates a new http client that sends a request with the given apiCallString, which is
     * appended to the api url of TISS.
     * <p>
     * https://tiss.tuwien.ac.at/api/(:apiCallString)
     *
     * @param apiCallString the method of TISS, which shall be called.
     * @param clientBuilder the client builder, which shall be used to create the new http client.
     */
    public TISSApiGetRequest(String apiCallString, HttpClientBuilder clientBuilder) {
        tissOrgApiRequest = new HttpGet(TISS_API_URL);
        this.httpClientBuilder = clientBuilder;
    }

    /**
     * Requests the the tiss api with the given method 'apiCallString' by using the given
     * client builder to create new http clients.
     *
     * @param clientBuilder the http builder, which shall be used to create a new http client.
     * @param apiCallString the method of the TISS api, which shall be called.
     * @return the response of the tiss api.
     * @throws TISSApiRequestFailedException if the request failed.
     */
    public static TISSApiResponse call(HttpClientBuilder clientBuilder, String apiCallString)
        throws TISSApiRequestFailedException {
        return call(clientBuilder, apiCallString, Collections.emptyMap());
    }

    /**
     * Requests the the tiss api with the given method 'apiCallString' by using the given
     * client builder to create new http clients.
     *
     * @param clientBuilder the http builder, which shall be used to create a new http client.
     * @param apiCallString the method of the TISS api, which shall be called.
     * @param parametersMap the parameters, which shall be added to the url.
     * @return the response of the tiss api.
     * @throws TISSApiRequestFailedException if the request failed.
     */
    public static TISSApiResponse call(HttpClientBuilder clientBuilder, String apiCallString,
        Map<String, String> parametersMap) throws TISSApiRequestFailedException {
        try {
            URIBuilder uriBuilder = new URIBuilder(TISS_API_URL + apiCallString);
            for (String parameterKey : parametersMap.keySet()) {
                uriBuilder.addParameter(parameterKey, parametersMap.get(parameterKey));
            }
            return get(new HttpGet(uriBuilder.build()), clientBuilder);
        } catch (URISyntaxException e) {
            throw new TISSApiRequestFailedException(e);
        }
    }

    /**
     * Sends a HTTP GET request to the given uri.
     *
     * @param tissOrgApiRequest the uri of the GET request.
     * @param clientBuilder     the http builder, which shall be used to create a new http client.
     * @return the response of the tiss api.
     * @throws TISSApiRequestFailedException if the request failed.
     */
    private static TISSApiResponse get(HttpUriRequest tissOrgApiRequest,
        HttpClientBuilder clientBuilder) throws TISSApiRequestFailedException {
        try (CloseableHttpClient httpClient = clientBuilder.build()) {
            try (CloseableHttpResponse tissOrgApiResponse = httpClient.execute(tissOrgApiRequest)) {
                if (tissOrgApiResponse.getStatusLine().getStatusCode() != 200) {
                    logger.error("The request was not successful. {}",
                        tissOrgApiResponse.getStatusLine());
                    throw new TISSApiRequestFailedException(String
                        .format("The request %s was not successful. The response code was %d.",
                            tissOrgApiRequest.getURI(),
                            tissOrgApiResponse.getStatusLine().getStatusCode()));
                }
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    tissOrgApiResponse.getEntity().writeTo(outputStream);
                    String response = new String(outputStream.toByteArray());
                    logger.debug("Tiss response received for {}: {}", tissOrgApiRequest.getURI(),
                        response.replaceAll("\\r|\\n", ""));
                    return new TISSApiResponse(response);
                }
            }
        } catch (IOException io) {
            throw new TISSApiRequestFailedException(io.getMessage());
        }
    }

    @Override
    public TISSApiResponse call() throws TISSApiRequestFailedException {
        return get(tissOrgApiRequest, httpClientBuilder);
    }
}

