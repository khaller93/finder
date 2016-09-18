package at.ac.tuwien.finder.datamanagement.mediation.spatial;

import at.ac.tuwien.finder.datamanagement.mediation.DataTransformer;
import at.ac.tuwien.finder.datamanagement.mediation.exception.DataAcquireException;
import at.ac.tuwien.finder.datamanagement.mediation.exception.DataTransformationException;
import at.ac.tuwien.finder.datamanagement.mediation.transformer.StringXSLTransformer;
import com.google.common.base.Function;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.openqa.selenium.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class represents an implementation of {@link SpatialDataAcquirer} and
 * {@link TISSFacilityCrawler}, which crawls for information about rooms of the Vienna University of
 * Technology that are exposed on TISS.
 *
 * @author Kevin Haller
 * @see <a href="https://tiss.tuwien.ac.at/events/roomSchedule.xhtml">TISS room schedule</a>.
 */
public class SeleniumTISSFacilityAcquirer
    implements SpatialDataAcquirer<String>, TISSFacilityCrawler {

    private static final Logger logger =
        LoggerFactory.getLogger(SeleniumTISSFacilityAcquirer.class);

    private static final String TISS_ROOM_XLST_STYLESHEET = "spatial/tissFacilityRdfTransform.xsl";

    private int CRAWLER_TIMEOUT = 4; //seconds
    private SimpleDateFormat SCHEDULE_TABLE_DATEFORMAT = new SimpleDateFormat("E dd.MM.yyyy");
    private WebDriver browser;

    private Date startCrawlingDate = Calendar.getInstance().getTime();
    private Date endCrawlingDate;

    /**
     * Creates a new acquirer for TISS rooms using selenium. The time range of the gathered data
     * will be from now til 6 month later.
     */
    public SeleniumTISSFacilityAcquirer() {
        HtmlUnitDriver htmlUnitDriver = new HtmlUnitDriver();
        htmlUnitDriver.setJavascriptEnabled(true);
        this.browser = htmlUnitDriver;
        Calendar endCrawlinGDateInstance = Calendar.getInstance();
        endCrawlinGDateInstance.add(Calendar.MONTH, 6);
        endCrawlingDate = endCrawlinGDateInstance.getTime();
    }

    /**
     * Creates a new acquirer for tiss rooms using selenium. The time range of the gathered data
     * will be from now til 6 month later.
     *
     * @param webDriver the web driver, which shall be used for crawling.
     */
    public SeleniumTISSFacilityAcquirer(WebDriver webDriver) {
        this.browser = webDriver;
        startCrawlingDate = Calendar.getInstance().getTime();
        Calendar endCrawlinGDateInstance = Calendar.getInstance();
        endCrawlinGDateInstance.add(Calendar.MONTH, 6);
        endCrawlingDate = endCrawlinGDateInstance.getTime();
    }

    /**
     * Creates a new acquirer for tiss rooms using selenium. The time range of the gathered data
     * will be from the given start date til the given end date.
     *
     * @param startCrawlingDate starting date of the time range.
     * @param endCrawlingDate   end date of the time range.
     */
    public SeleniumTISSFacilityAcquirer(Date startCrawlingDate, Date endCrawlingDate) {
        this();
        this.startCrawlingDate = startCrawlingDate;
        this.endCrawlingDate = endCrawlingDate;
    }

    /**
     * Creates a new acquirer for tiss rooms using selenium. The time range of the gathered data
     * will be from the given start date til the given end date.
     *
     * @param startCrawlingDate starting date of the time range.
     * @param endCrawlingDate   end date of the time range.
     * @param webDriver         the web driver, which shall be used for crawling.
     */
    public SeleniumTISSFacilityAcquirer(Date startCrawlingDate, Date endCrawlingDate,
        WebDriver webDriver) {
        this(webDriver);
        this.startCrawlingDate = startCrawlingDate;
        this.endCrawlingDate = endCrawlingDate;
    }

    @Override
    public DataTransformer<String> transformer() throws DataTransformationException {
        return new StringXSLTransformer(SeleniumTISSFacilityAcquirer.class.getClassLoader()
            .getResourceAsStream(TISS_ROOM_XLST_STYLESHEET), RDFFormat.RDFXML);
    }

    @Override
    public String acquire() throws DataAcquireException {
        Map<String, String> acquireMap = new HashMap<>();
        acquireMap.put("info",
            allBuildingsInformation() + scheduleOfAllRooms(startCrawlingDate, endCrawlingDate));
        return new StrSubstitutor(acquireMap, "%(", ")s").replace(TISS_FACILITY_XHTML_PROTOTYP);
    }

    @Override
    public Collection<String> allBuildingsIdentifiers() throws DataAcquireException {
        browser.get(TISS_ROOM_SCHEDULE_URL);
        try {
            new WebDriverWait(browser, CRAWLER_TIMEOUT).until(
                ExpectedConditions.elementToBeClickable(By.id("calendarForm:selectBuildingLb")));
            Collection<String> buildingsIdentifier = browser
                .findElements(By.xpath("//select[@id=\"calendarForm:selectBuildingLb\"]/option"))
                .stream().map(elem -> elem.getAttribute("value"))
                .collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
            logger.debug("allBuildingIdentifiers() -> {}", buildingsIdentifier);
            return buildingsIdentifier;
        } catch (Exception e) {
            logger.error("allBuildingIdentifiers() throws {}", e.getMessage());
            throw new DataAcquireException(e);
        }
    }

    @Override
    public Map<String, String> allBuildingsDescriptions() throws DataAcquireException {
        browser.get(TISS_ROOM_SCHEDULE_URL);
        try {
            new WebDriverWait(browser, CRAWLER_TIMEOUT).until(
                ExpectedConditions.elementToBeClickable(By.id("calendarForm:selectBuildingLb")));
            List<WebElement> buildingOptions =
                browser.findElement(By.id("calendarForm:selectBuildingLb"))
                    .findElements(By.tagName("option"));
            Map<String, String> buildingsMap = new HashMap<>();
            for (WebElement optionElement : buildingOptions) {
                buildingsMap.put(optionElement.getAttribute("value"), optionElement.getText());
            }
            return buildingsMap;
        } catch (Exception e) {
            logger.error("allBuildingIdentifiers() throws {}", e.getMessage());
            throw new DataAcquireException(e);
        }
    }

    @Override
    public String allBuildingsInformation() throws DataAcquireException {
        String responseSet = "";
        for (Map.Entry<String, String> buildingEntry : allBuildingsDescriptions().entrySet()) {
            Map<String, String> buildingInfoMap = new HashMap<>();
            buildingInfoMap.put("id", buildingEntry.getKey());
            buildingInfoMap.put("info", buildingEntry.getValue());
            buildingInfoMap.put("roomIds", allRoomIdentifiersOf(buildingEntry.getKey()).toString());
            responseSet += new StrSubstitutor(buildingInfoMap, "%(", ")s")
                .replace(TISS_BUILDING_XHTML_PROTOTYP);
        }
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("buildings", responseSet);
        return new StrSubstitutor(responseMap, "%(", ")s")
            .replace(TISS_BUILDING_SET_XHTML_PROTOTYP);
    }

    @Override
    public Collection<String> allRoomIdentifiersOf(String buildingTISSIdentifier)
        throws DataAcquireException {
        if (buildingTISSIdentifier == null) {
            throw new IllegalArgumentException(
                "The given buildingTISSIdentifier must not be null!");
        }
        browser.get(TISS_ROOM_SCHEDULE_URL + "?roomCode=ABL");
        try {
            // Click selection box of the buildings.
            WebElement buildingSelectElement = new WebDriverWait(browser, CRAWLER_TIMEOUT).until(
                ExpectedConditions.elementToBeClickable(By.id("calendarForm:selectBuildingLb")));
            WebElement roomSelectElement = new WebDriverWait(browser, CRAWLER_TIMEOUT)
                .until(ExpectedConditions.elementToBeClickable(By.id("calendarForm:selectRoomLb")));
            buildingSelectElement.click();
            // Select the building.
            Select buildingSelect = new Select(buildingSelectElement);
            buildingSelect.selectByValue(buildingTISSIdentifier);
            try {
                new WebDriverWait(browser, CRAWLER_TIMEOUT)
                    .until(ExpectedConditions.stalenessOf(roomSelectElement));
            } catch (TimeoutException t) {
                logger.error(t.getMessage());
            }
            Collection<String> roomsIdentifier =
                browser.findElement(By.id("calendarForm:selectRoomLb"))
                    .findElements(By.tagName("option")).stream()
                    .map(elem -> elem.getAttribute("value"))
                    .collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
            logger.debug("allRoomIdentifiersOf({}) -> {}", buildingTISSIdentifier, roomsIdentifier);
            return roomsIdentifier;
        } catch (Exception e) {
            logger.error("allRoomIdentifiersOf({}) throws {}", buildingTISSIdentifier,
                e.getMessage());
            throw new DataAcquireException(e);
        }
    }

    @Override
    public String scheduleOf(String roomTISSIdentifier, Date initialDate, Date endDate)
        throws DataAcquireException {
        if (roomTISSIdentifier == null || initialDate == null || endDate == null) {
            throw new IllegalArgumentException(String
                .format("The given parameters %s %s %s must not be null!",
                    roomTISSIdentifier == null ? "roomTissIdentifier" : "",
                    initialDate == null ? "initialDate" : "", endDate == null ? "endDate" : ""));
        }
        browser.get(TISS_ROOM_SCHEDULE_URL + String
            .format("?roomCode=%s&initialDate=%s", roomTISSIdentifier,
                new SimpleDateFormat("yyyyMMdd").format(initialDate.getTime())));
        Map<String, String> metaMap = new HashMap<>();
        // Header with the room details.
        WebElement headerElement = new WebDriverWait(browser, CRAWLER_TIMEOUT).until(
            (Function<WebDriver, WebElement>) browser -> browser
                .findElement(By.id("contentInner")));
        metaMap.put("title", headerElement.findElement(By.tagName("h1")).getText());
        WebElement subHeaderElement = new WebDriverWait(browser, CRAWLER_TIMEOUT).until(
            (Function<WebDriver, WebElement>) browser -> browser.findElement(By.id("subHeader")));
        metaMap.put("id", roomTISSIdentifier);
        Select buildingSelect = new Select(new WebDriverWait(browser, CRAWLER_TIMEOUT).until(
            (Function<WebDriver, WebElement>) browser -> browser
                .findElement(By.id("calendarForm:selectBuildingLb"))));
        metaMap.put("buildingId", buildingSelect.getFirstSelectedOption().getAttribute("value"));
        // Scanning the sub header
        Scanner subHeaderScanner =
            new Scanner(subHeaderElement.getText()).useDelimiter("Raumnummer:|Kapazität:");
        metaMap.put("location", subHeaderScanner.hasNext() ? subHeaderScanner.next() : "");
        metaMap.put("roomNumber", subHeaderScanner.hasNext() ? subHeaderScanner.next().trim() : "");
        metaMap
            .put("roomCapacity", subHeaderScanner.hasNext() ? subHeaderScanner.next().trim() : "");
        subHeaderScanner.close();
        logger.debug("Location: {} RoomNumber: {} Capacity: {}", metaMap.get("location"),
            metaMap.get("roomNumber"), metaMap.get("roomCapacity"));
        try {
            // Get the overview of the schedule in table form.
            WebElement calendarForm = new WebDriverWait(browser, CRAWLER_TIMEOUT).until(
                (Function<WebDriver, WebElement>) browser -> browser
                    .findElement(By.id("calendarForm")));
            new WebDriverWait(browser, CRAWLER_TIMEOUT).until(
                (Function<WebDriver, WebElement>) browser -> browser
                    .findElement(By.id("calendarForm:j_id_2o"))).click();
            try {
                new WebDriverWait(browser, CRAWLER_TIMEOUT)
                    .until(ExpectedConditions.stalenessOf(calendarForm));
            } catch (TimeoutException t) {
                logger.error(t.getMessage());
            }
            String scheduleXHTMl = "%s";
            do {
                // Scan the table of dates.
                WebElement scheduleTable = new WebDriverWait(browser, CRAWLER_TIMEOUT).until(
                    (Function<WebDriver, WebElement>) browser -> browser
                        .findElement(By.id("calendarForm:j_id_2s_3")));
                List<WebElement> tableDates = scheduleTable.findElements(By.xpath("//tbody/tr"));

                String currentScheduleXHTML = "";
                for (WebElement trElement : tableDates) {
                    if (!((String) ((JavascriptExecutor) browser)
                        .executeScript("return arguments[0].className;", trElement))
                        .contains("el_row")) {
                        continue;
                    }
                    currentScheduleXHTML += ((JavascriptExecutor) browser)
                        .executeScript("return arguments[0].outerHTML;", trElement) + "\n";
                    try {
                        initialDate = SCHEDULE_TABLE_DATEFORMAT
                            .parse(trElement.findElement(By.className("el_date_text")).getText());
                    } catch (Exception e) {
                        continue;
                    }
                }
                scheduleXHTMl = String.format(scheduleXHTMl, currentScheduleXHTML + "%s");
                // Next schedule page.
                WebElement nextSchedulePageLink =
                    browser.findElement(By.id("calendarForm:j_id_2s_3:j_id_2s_y"));
                if (nextSchedulePageLink.getAttribute("onclick") != null && initialDate
                    .before(endDate)) {
                    nextSchedulePageLink.click();
                    new WebDriverWait(browser, CRAWLER_TIMEOUT)
                        .until(ExpectedConditions.stalenessOf(scheduleTable));
                } else {
                    break;
                }
            } while (initialDate.before(endDate));
            metaMap.put("schedule_table", String.format(scheduleXHTMl, ""));
            String xhtmlResponse =
                new StrSubstitutor(metaMap, "%(", ")s").replace(TISS_ROOM_SCHEDULE_XHTML_PROTOTYP);
            logger.debug("scheduleOf({}, {}, {}) -> {}", roomTISSIdentifier, initialDate, endDate,
                xhtmlResponse.replaceAll("[\\n\\r]", " ").replaceAll(" ++", ""));
            return escape(xhtmlResponse);
        } catch (Exception e) {
            logger
                .error("scheduleOf({}, {}, {}) throws {}", roomTISSIdentifier, initialDate, endDate,
                    e.getMessage());
            System.out.println(String
                .format("scheduleOf(%s, %s, %s) throws %s", roomTISSIdentifier, initialDate,
                    endDate, e.getMessage()));
            System.out.println(">>> " + browser.getCurrentUrl());
            throw new DataAcquireException(e);
        }
    }

    @Override
    public String scheduleOfAllRooms(Date initialDate, Date endDate) throws DataAcquireException {
        String allRoomSchedulesXHTML = "%s";
        for (String buildingIdentifier : allBuildingsIdentifiers()) {
            for (String roomIdentifier : allRoomIdentifiersOf(buildingIdentifier)) {
                allRoomSchedulesXHTML = String.format(allRoomSchedulesXHTML,
                    scheduleOf(roomIdentifier, initialDate, endDate) + "%s");
            }
        }
        Map<String, String> valueMap = new HashMap<>();
        valueMap.put("rooms", String.format(allRoomSchedulesXHTML, ""));
        String xhtmlResponse =
            new StrSubstitutor(valueMap, "%(", ")s").replace(TISS_ROOM_SET_XHTML_PROTOTYP);
        logger.debug("scheduleOfAllRooms({}, {}) -> {}", initialDate, endDate,
            xhtmlResponse.replaceAll("[\\n\\r]", " ").replaceAll(" ++", ""));
        return xhtmlResponse;
    }

    /**
     * Returns a escaped version of the given xhtml string, so that it is valid.
     *
     * @param xhtml which shall be escaped.
     * @return the escaped xhtml string.
     */
    private String escape(String xhtml) {
        return xhtml.replace("&nbsp;", " ");
    }

    @Override
    public void close() throws Exception {
        browser.close();
    }
}
