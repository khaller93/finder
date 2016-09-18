package at.ac.tuwien.finder.datamanagement.integration;

import java.util.Collection;

/**
 * This class represents an integration plan that specifies which {@link DataIntegrator},
 * {@link DataLinker}s and {@link DataCleanser}s to use for the integration.
 *
 * @author Kevin Haller
 */
public class IntegrationPlan implements AutoCloseable {

    private DataIntegrator dataIntegrator;
    private Collection<DataLinker> dataLinkers;
    private DataCleanser dataCleanser;

    /**
     * Creates a new integration plan that uses the given {@link DataIntegrator},
     * {@link DataLinker}s and {@link DataCleanser}s.
     *
     * @param dataIntegrator the data updater, which shall be used in this integration plan.
     * @param dataLinkers    the list of data linkers, which shall be used in this integration plan.
     * @param dataCleanser   the data cleanser, which shall be used int this integration plan.
     */
    public IntegrationPlan(DataIntegrator dataIntegrator, Collection<DataLinker> dataLinkers,
        DataCleanser dataCleanser) {
        this.dataIntegrator = dataIntegrator;
        this.dataLinkers = dataLinkers;
        this.dataCleanser = dataCleanser;
    }

    /**
     * Gets the data updater of this integration plan.
     *
     * @return the data updater of this integration plan.
     */
    public DataIntegrator getDataIntegrator() {
        return dataIntegrator;
    }

    /**
     * Gets the list of data linker of this integration plan.
     *
     * @return the list of data linker of this integration plan.
     */
    public Collection<DataLinker> getDataLinkers() {
        return dataLinkers;
    }

    /**
     * Gets the data cleanser of the integration plan.
     *
     * @return the data cleanser of the integration plan.
     */
    public DataCleanser getDataCleanser() {
        return dataCleanser;
    }

    @Override
    public void close() throws Exception {
        if (dataIntegrator != null) {
            dataIntegrator.close();
        }
        if (dataCleanser != null) {
            dataCleanser.close();
        }
        if (dataLinkers != null) {
            for (DataLinker dataLinker : dataLinkers) {
                dataLinker.close();
            }
        }
    }
}
