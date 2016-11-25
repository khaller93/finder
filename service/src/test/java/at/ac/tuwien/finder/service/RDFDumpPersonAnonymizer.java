package at.ac.tuwien.finder.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class anonymizes the content of the given RDF dump.
 *
 * @author Kevin Haller
 */
public final class RDFDumpPersonAnonymizer {

    private static final String FAKE_PERSONS_CSV_PATH = "names/fakeNames.csv";
    private static final ArrayList<Person> fakePersonNames = new ArrayList<>();

    static {
        try (Reader fakeNamesReader = new InputStreamReader(
            RDFDumpPersonAnonymizer.class.getClassLoader()
                .getResourceAsStream(FAKE_PERSONS_CSV_PATH))) {
            CSVFormat.DEFAULT.withHeader().parse(fakeNamesReader).getRecords().forEach(
                record -> fakePersonNames.add(
                    new Person(record.get("FamilyName"), record.get("GivenName"),
                        record.get("Email"), record.get("TelephoneNumber"), record.get("Gender"))));
        } catch (IOException e) {
            throw new IllegalArgumentException(String
                .format("The fake names file (%s) could not be accessed.", FAKE_PERSONS_CSV_PATH));
        }
    }


    private static class Person {

        private String familyName;
        private String givenName;
        private String email;
        private String telephoneNumber;
        private String gender;

        public Person(String familyName, String givenName, String email, String telephoneNumber,
            String gender) {
            assert familyName != null;
            assert givenName != null;
            this.familyName = familyName;
            this.givenName = givenName;
            this.email = email;
            this.telephoneNumber = telephoneNumber;
            this.gender = gender;
        }

        public String getFamilyName() {
            return familyName;
        }

        public String getGivenName() {
            return givenName;
        }

        public String getEmail() {
            return email;
        }

        public String getTelephoneNumber() {
            return telephoneNumber;
        }

        public String getGender() {
            return gender;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Person person = (Person) o;
            return familyName.equals(person.familyName) && givenName.equals(person.givenName);

        }

        @Override
        public int hashCode() {
            int result = familyName.hashCode();
            result = 31 * result + givenName.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Person{" + "familyName='" + familyName + '\'' + ", givenName='" + givenName
                + '\'' + ", email='" + email + '\'' + ", telephoneNumber='" + telephoneNumber + '\''
                + ", gender='" + gender + '\'' + '}';
        }
    }

    /**
     * Anonymizes the persons of the given RDF test dump by changing the name to a random generated
     * one. These names mapped to the unique id of the person will be stored into a CSV file, so
     * that the same person are always anonymized with the same names. If the given CSV file already
     * exists, the already anonymized persons will be skipped.
     *
     * @param model             the model of the RDF dump.
     * @param anonPersonCSVPath the path to the anonymous CSV file.
     * @return true, if there are some changes, otherwise false.
     */
    private static Model anonymousPersons(final Model model, String anonPersonCSVPath)
        throws IOException {
        ValueFactory valueFactory = SimpleValueFactory.getInstance();
        Map<String, Person> nameMap = new HashMap<>();
        File anonPersonCSVFile = new File(anonPersonCSVPath);
        if (anonPersonCSVFile.exists()) {
            try (Reader configReader = new FileReader(anonPersonCSVFile)) {
                CSVFormat.DEFAULT.withHeader().parse(configReader).getRecords().forEach(
                    record -> nameMap.put(record.get("id"),
                        new Person(record.get("FamilyName"), record.get("GivenName"),
                            record.get("Email"), record.get("TelephoneNumber"),
                            record.get("Gender"))));
            }
        }
        Model responseModel = new LinkedHashModel(model);
        ArrayList<Person> fakePersonNamesCopy = new ArrayList<>(fakePersonNames);
        for (Resource resource : responseModel.filter(null, RDF.TYPE, FOAF.PERSON).subjects()) {
            if (!nameMap.containsKey(resource.stringValue())) {
                boolean fakeNameFound = false;
                while (!fakePersonNamesCopy.isEmpty() && !fakeNameFound) {
                    Person newPerson =
                        fakePersonNamesCopy.get((int) (Math.random() * fakePersonNamesCopy.size()));
                    if (!nameMap.values().contains(newPerson)) {
                        nameMap.put(resource.stringValue(), newPerson);
                        fakeNameFound = true;
                    }
                }
                if (!fakeNameFound) {
                    throw new IllegalStateException("No fake names left to assign.");
                }
            }
            Person newPerson = nameMap.get(resource.stringValue());
            if (responseModel.remove(resource, FOAF.NAME, null)) {
                responseModel.add(resource, FOAF.NAME, valueFactory.createLiteral(
                    newPerson.getGivenName() + " " + newPerson.getFamilyName()));
            }
            if (responseModel.remove(resource, FOAF.GIVEN_NAME, null)) {
                responseModel.add(resource, FOAF.GIVEN_NAME,
                    valueFactory.createLiteral(newPerson.getGivenName()));
            }
            if (responseModel.remove(resource, FOAF.FAMILY_NAME, null)) {
                responseModel.add(resource, FOAF.FAMILY_NAME,
                    valueFactory.createLiteral(newPerson.getFamilyName()));
            }
            if (responseModel.remove(resource, FOAF.MBOX, null)) {
                responseModel.add(resource, FOAF.MBOX,
                    valueFactory.createLiteral(newPerson.getEmail()));
            }
            if (responseModel.remove(resource, FOAF.GENDER, null)) {
                responseModel.add(resource, FOAF.GENDER,
                    valueFactory.createLiteral(newPerson.getGender()));
            }
        }
        // CSV config update.
        anonPersonCSVFile.createNewFile();
        try (Writer configWriter = new FileWriter(anonPersonCSVFile)) {
            CSVPrinter csvConfigPrinter =
                new CSVPrinter(configWriter, CSVFormat.DEFAULT.withHeader());
            csvConfigPrinter
                .printRecord("id", "GivenName", "FamilyName", "Email", "TelephoneNumber", "Gender");
            for (String resourceId : nameMap.keySet()) {
                Person currentPerson = nameMap.get(resourceId);
                csvConfigPrinter.printRecord(resourceId, currentPerson.getGivenName(),
                    currentPerson.getFamilyName(), currentPerson.getEmail(),
                    currentPerson.getTelephoneNumber(), currentPerson.getGender());
            }
        }
        return responseModel;
    }

    /**
     * Prints out the help for this command.
     *
     * @param programName the name of the program.
     */
    private static void printHelp(String programName) {
        System.out.println("Help:\n" + programName + " rdfDumpPath configPath");
    }

    /**
     * Executes the anonymizer on the given rdf dump (first argument) and uses the path to
     * the configuration file (second argument), to map already assigned fake names to the
     * corresponding person. The configuration file may not exist; in this case a new one will be
     * created. For each person for which no fake name has yet been generated, such a name will be
     * generated, and the model will be updated. The mapping is inserted into the given
     * configuration file.
     *
     * @param args 1st argument ... path to the rdf dump, 2nd argument ... path to configuration.
     */
    public static void main(String[] args) {
        System.out.println(Arrays.asList(args));
        if (args.length < 2) {
            printHelp(RDFDumpPersonAnonymizer.class.getSimpleName().toLowerCase());
            return;
        }
        File rdfDumpFile = new File(args[0]);
        if (!rdfDumpFile.exists()) {
            System.err.println(String
                .format("The RDF dump file (%s) does not exist.", rdfDumpFile.getAbsolutePath()));
        }
        Model rdfDumpModel = null;
        try (Reader rdfDumpReader = new FileReader(rdfDumpFile)) {
            rdfDumpModel = Rio.parse(rdfDumpReader, "", RDFFormat.TRIG);
        } catch (IOException e) {
            System.err.println(String
                .format("The RDF dump (%s) cannot be accessed.", rdfDumpFile.getAbsolutePath()));
        }
        if (rdfDumpModel != null) {
            try (Writer writer = new FileWriter(rdfDumpFile)) {
                Model m = anonymousPersons(rdfDumpModel, args[1]);
                Rio.write(m, writer, RDFFormat.TRIG);
            } catch (IOException e) {
                System.err.println(String.format("The RDF dump (%s) update could not be persisted.",
                    rdfDumpFile.getAbsolutePath()));
            }
        }
    }

}
