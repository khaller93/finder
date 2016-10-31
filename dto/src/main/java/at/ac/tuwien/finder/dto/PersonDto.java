package at.ac.tuwien.finder.dto;

import org.outofbits.opinto.annotations.RdfProperty;
import org.outofbits.opinto.annotations.RdfsClass;

/**
 * This class represents a person dto.
 *
 * @author Kevin Haller
 */
@RdfsClass("foaf:Person")
public class PersonDto extends AbstractResourceDto {

    private String name;
    private String familyName;
    private String givenName;
    private String title;
    private String gender;
    private RoomDto roomDto;

    public String getName() {
        return name;
    }

    @RdfProperty(value = "foaf:name", datatype = "xs:string")
    public void setName(String name) {
        this.name = name;
    }

    public String getFamilyName() {
        return familyName;
    }

    @RdfProperty(value = "foaf:familyName", datatype = "xs:string")
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    @RdfProperty(value = "foaf:givenName", datatype = "xs:string")
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getTitles() {
        return title;
    }

    @RdfProperty(value = "foaf:title", datatype = "xs:string")
    public void setTitles(String titles) {
        this.title = titles;
    }

    public String getGender() {
        return gender;
    }

    @RdfProperty(value = "foaf:gender", datatype = "xs:string")
    public void setGender(String gender) {
        this.gender = gender;
    }

    public RoomDto getRoom() {
        return roomDto;
    }

    @RdfProperty(value = "http://www.w3.org/ns/org#basedAt")
    public void setRoom(RoomDto roomDto) {
        this.roomDto = roomDto;
    }
}
