package at.ac.tuwien.finder.service.vocabulary;

import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.dto.VocabularyResourceDto;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.dto.SimpleResourceDto;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.exception.ServiceException;
import at.ac.tuwien.finder.vocabulary.TUVS;
import at.ac.tuwien.finder.vocabulary.VocabularyManager;
import at.ac.tuwien.finder.vocabulary.exception.OntologyAccessException;

/**
 * This class is an implementation of {@link IService} that describes a specified, local vocabulary.
 *
 * @author Kevin Haller
 */
class DescribeVocabularyService implements IService {

    private String localVocabName;

    /**
     * Creates a new instance of {@link DescribeVocabularyService} for the local vocabulary with the
     * given name.
     *
     * @param localVocabName the name of the local vocabulary.
     */
    DescribeVocabularyService(String localVocabName) {
        assert localVocabName != null;
        this.localVocabName = localVocabName;
    }

    @Override
    public Dto execute() throws ServiceException {
        if (localVocabName.equals("spatial")) {
            try {
                return new VocabularyResourceDto(new IResourceIdentifier(TUVS.NS),
                    VocabularyManager.getInstance().getSpatialOntology());
            } catch (OntologyAccessException e) {
                throw new ServiceException(e);
            }
        } else {
            throw new ServiceException(
                String.format("There is no local vocabulary with the name '%s'.", localVocabName));
        }
    }
}
