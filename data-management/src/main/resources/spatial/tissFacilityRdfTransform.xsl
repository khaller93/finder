<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet [
        <!ENTITY base "http://finder.tuwien.ac.at/vocab/spatial#">
        <!ENTITY tuvs "http://finder.tuwien.ac.at/vocab/spatial#">
        <!ENTITY spatial "http://finder.tuwien.ac.at/spatial/">
        <!ENTITY event "http://finder.tuwien.ac.at/event/">
        <!ENTITY rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#">
        <!ENTITY rdfs "http://www.w3.org/2000/01/rdf-schema#">
        <!ENTITY schema "http://schema.org/">
        <!ENTITY dc "http://purl.org/dc/elements/1.1/">
        <!ENTITY oo "http://purl.org/openorg/">
        ]>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="&base;"
                xmlns:tuvs="&tuvs;"
                xmlns:ffunction="http://finder.tuwien.ac.at/function/"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:oo="http://purl.org/openorg/"
                xmlns:schema="http://schema.org/"
>
    <xsl:output indent="yes" method="xml"/>

    <xsl:template match="tiss_facility">
        <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                 xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                 xmlns:tuvs="&tuvs;"
                 xmlns:oo="http://purl.org/openorg/"
                 xmlns:schema="http://schema.org/"
        >
            <xsl:apply-templates select="buildingSet/building"/>
            <xsl:apply-templates select="roomSet/room"/>
            <xsl:apply-templates select="roomSet/room/schedule_table" mode="INIT"/>
        </rdf:RDF>
    </xsl:template>

    <!-- Transforms the building xml data into rdf/xml. The format of this section look like this:
         <building>
             <id>Unique code of the building.</id>
             <info>Information about the building.</info>
             <rooms>The tiss ids of the rooms, which are assigned to this building.</rooms>
         </building>
         The room codes are fetched by searching for the corresponding room entry in the room set,
         where the room code is specified.
    -->
    <xsl:template match="buildingSet/building">
        <xsl:variable name="buildingId" select="id"/>
        <xsl:variable name="infoString" select="info"/>
        <xsl:variable name="rooms" select="replace(rooms,'\[|\]','')"/>
        <Building rdf:about="&spatial;building/id/{replace($buildingId, '[^\w]+', '')}">
            <xsl:variable name="roomSet" select="/tiss_facility/roomSet"/>
            <xsl:for-each select="tokenize($rooms, ',')">
                <xsl:variable name="roomId" select="normalize-space(.)"/>
                <xsl:variable name="roomNumber" select="$roomSet/room[id=$roomId]/roomNumber"/>
                <xsl:variable name="roomResourceId"
                              select="replace(if($roomNumber != '') then($roomNumber) else (concat('tiss_', $roomId)), '[^\w]+', '')"/>
                <tuvs:containsBuildingUnit
                        rdf:resource="&spatial;room/id/{$roomResourceId}"/>
            </xsl:for-each>
        </Building>
    </xsl:template>

    <!-- Transforms the room xhmtl data into rdf/xml. The format of rooms look like this:
         <room>
            <title>The name of the room.</title>
            <id>The tiss id of the room.</id>
            <buildingId>The code of the building.</buildingId>
            <location>Information about how to access the room.</location>
            <roomNumber>The code of the room.</roomNumber>
            <roomCapacity>The capacity of the room.</roomCapacity>
            <schedule_table>The table of events.</schedule_table>
         </room>
    -->
    <xsl:template match="roomSet/room">
        <xsl:variable name="roomId" select="normalize-space(replace(id,'[ /\\\.\*]+',''))"/>
        <xsl:variable name="roomNumber"
                      select="normalize-space(replace(roomNumber,'[ /\\\.\*]+',''))"/>
        <xsl:variable name="roomResourceId"
                      select="replace(if($roomNumber != '') then($roomNumber) else (concat('tiss_', $roomId)),'[^\w]+','')"/>
        <rdf:Description rdf:about="&spatial;room/id/{$roomResourceId}">
            <xsl:variable name="roomTitle"
                          select="normalize-space(tokenize(title,' *- *')[2])"/>
            <xsl:choose>
                <xsl:when test="contains($roomTitle, 'Seminarraum')">
                    <rdf:type rdf:resource="&tuvs;SeminarRoom"/>
                </xsl:when>
                <xsl:when test="contains($roomTitle, 'Sem.R.')">
                    <rdf:type rdf:resource="&tuvs;SeminarRoom"/>
                </xsl:when>
                <xsl:when test="contains($roomTitle, 'Hörsaal')">
                    <rdf:type rdf:resource="&tuvs;Auditorium"/>
                </xsl:when>
                <xsl:otherwise>
                    <rdf:type rdf:resource="&tuvs;Room"/>
                </xsl:otherwise>
            </xsl:choose>
            <rdfs:label>
                <xsl:value-of select="$roomTitle"/>
            </rdfs:label>
            <xsl:if test="$roomNumber != ''">
                <tuvs:roomCode>
                    <xsl:value-of select="$roomNumber"/>
                </tuvs:roomCode>
            </xsl:if>
            <oo:access>
                <xsl:value-of select="normalize-space(location)"/>
            </oo:access>
            <xsl:if test="buildingId != ''">
                <tuvs:isBuildingUnitOf
                        rdf:resource="&spatial;building/id/{normalize-space(buildingId)}"/>
            </xsl:if>
            <tuvs:capacity rdf:datatype="http://www.w3.org/2001/XMLSchema#integer">
                <xsl:value-of select="normalize-space(roomCapacity)"/>
            </tuvs:capacity>
        </rdf:Description>
    </xsl:template>

    <!-- Transforms the schedule table into rdf/xml events. -->
    <xsl:template match="roomSet/room/schedule_table" mode="INIT">
        <xsl:variable name="roomNumber"
                      select="preceding-sibling::roomNumber"/>
        <xsl:variable name="roomId"
                      select="preceding-sibling::id"/>
        <xsl:variable name="roomResourceId" select="replace(if($roomNumber != '') then($roomNumber) else (concat('tiss_', $roomId)),'[^\w]+','')"/>
        <xsl:for-each select="tr[contains(@class,'el_row') and child::th]">
            <!-- Date of the current table -->
            <xsl:variable name="date" select="normalize-space(th)"/>
            <xsl:variable name="dateYear" select="tokenize($date,' |\.')[4]"/>
            <xsl:variable name="dateMonth" select="tokenize($date,' |\.')[3]"/>
            <xsl:variable name="dateDay" select="tokenize($date,' |\.')[2]"/>
            <!-- Iterate over all events of this date -->
            <xsl:variable name="preceding_siblings"
                          select="following-sibling::tr[contains(@class,'el_row') and (child::th or position() = last())][1]|following-sibling::tr[contains(@class,'el_row') and (child::th or position() = last())][1]/preceding-sibling::tr[contains(@class,'el_row')]"/>
            <xsl:for-each
                    select=".|following-sibling::tr[contains(@class,'el_row')][count(.|$preceding_siblings)=count($preceding_siblings) and not(child::th)]">
                <!-- Start and end date of the event -->
                <xsl:variable name="timeRange"
                              select="normalize-space(td[contains(@class,'el_time')])"/>
                <xsl:variable name="timeStart" select="tokenize($timeRange,' *- *')[1]"/>
                <xsl:variable name="timeEnd" select="tokenize($timeRange,' *- *')[2]"/>
                <!-- Event meta information -->
                <xsl:variable name="eventName"
                              select="normalize-space(td[contains(@class,'el_title')])"/>
                <xsl:variable name="eventDescription"
                              select="normalize-space(td[contains(@class,'el_description')])"/>
                <xsl:variable name="eventId" select="concat($roomResourceId,'-',replace($date, '[^\d]+', ''),'-',
                    replace($timeRange, '[^\d]+', ''),'-',ffunction:base64Encode(concat($eventName, $eventDescription), true()))"/>
                <schema:Event rdf:about="&event;id/{$eventId}">
                    <schema:name>
                        <xsl:value-of select="$eventName"/>
                    </schema:name>
                    <schema:description>
                        <xsl:value-of select="$eventDescription"/>
                    </schema:description>
                    <schema:location rdf:resource="&spatial;room/id/{$roomResourceId}"/>
                    <schema:startDate rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime">
                        <xsl:value-of
                                select="concat(string-join(($dateYear,$dateMonth,$dateDay),'-'),'T',string-join(($timeStart,'00'),':'),'+01:00')"/>
                    </schema:startDate>
                    <schema:endDate rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime">
                        <xsl:value-of
                                select="concat(string-join(($dateYear,$dateMonth,$dateDay),'-'),'T',string-join(($timeEnd,'00'),':'),'+01:00')"/>
                    </schema:endDate>
                </schema:Event>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>

