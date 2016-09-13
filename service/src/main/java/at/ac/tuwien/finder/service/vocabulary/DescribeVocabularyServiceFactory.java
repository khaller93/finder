package at.ac.tuwien.finder.service.vocabulary;

import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.exception.RDFSerializableException;

import java.net.URI;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is an implementation of {@link IServiceFactory} that manages  to describe
 * vocabularies.
 *
 * @author Kevin Haller
 */
class DescribeVocabularyServiceFactory implements IServiceFactory {

    private String localVocabName;

    DescribeVocabularyServiceFactory(String localVocabName) {
        assert localVocabName != null;
        this.localVocabName = localVocabName;
    }


    @Override
    public IService getService(URI parent, Scanner pathScanner, Map<String, String> parameterMap)
        throws RDFSerializableException {
        if (pathScanner.hasNext()) {
            throw new IRIUnknownException(String.format("No service is assigned to '%s'.",
                parent.resolve(pathScanner.next()).toString()));
        }
        return new DescribeVocabularyService(localVocabName);
    }
}
