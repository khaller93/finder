package at.ac.tuwien.finder.dto.spatial;

import at.ac.tuwien.finder.dto.AbstractResourceDto;
import at.ac.tuwien.finder.dto.Dto;
import org.outofbits.opinto.annotations.RdfProperty;
import org.outofbits.opinto.annotations.RdfsClass;

import java.util.Collection;

/**
 * This class is an implemantion of {@link Dto} that represents an address.
 *
 * @author Kevin Haller
 */
@RdfsClass("http://www.w3.org/ns/locn#Address")
public class AddressDto extends AbstractResourceDto {

    private String fullAddress;
    private String postalCode;
    private Collection<String> postalName;
    private String thoroughfare;
    private String locatorDesignator;
    private String locatorName;

    private String adminUnitL1;
    private String adminUnitL2;

    public String getFullAddress() {
        return fullAddress;
    }

    @RdfProperty("http://www.w3.org/ns/locn#fullAddress")
    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    @RdfProperty(value = "http://www.w3.org/ns/locn#postCode")
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Collection<String> getPostalName() {
        return postalName;
    }

    @RdfProperty(value = "http://www.w3.org/ns/locn#postName")
    public void setPostalName(Collection<String> postalNames) {
        this.postalName = postalNames;
    }

    public String getThoroughfare() {
        return thoroughfare;
    }

    @RdfProperty("http://www.w3.org/ns/locn#thoroughfare")
    public void setThoroughfare(String thoroughfare) {
        this.thoroughfare = thoroughfare;
    }

    public String getAdminUnitL1() {
        return adminUnitL1;
    }

    @RdfProperty("http://www.w3.org/ns/locn#adminUnitL1")
    public void setAdminUnitL1(String adminUnitL1) {
        this.adminUnitL1 = adminUnitL1;
    }

    public String getAdminUnitL2() {
        return adminUnitL2;
    }

    @RdfProperty("http://www.w3.org/ns/locn#adminUnitL2")
    public void setAdminUnitL2(String adminUnitL2) {
        this.adminUnitL2 = adminUnitL2;
    }

    public String getLocatorDesignator() {
        return locatorDesignator;
    }

    @RdfProperty("http://www.w3.org/ns/locn#locatorDesignator")
    public void setLocatorDesignator(String locatorDesignator) {
        this.locatorDesignator = locatorDesignator;
    }

    public String getLocatorName() {
        return locatorName;
    }

    @RdfProperty("http://www.w3.org/ns/locn#locatorName")
    public void setLocatorName(String locatorName) {
        this.locatorName = locatorName;
    }
}
