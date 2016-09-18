<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet [
        <!ENTITY spatial "http://finder.tuwien.ac.at/spatial/">
        <!ENTITY organizational "http://finder.tuwien.ac.at/organizational/">
        <!ENTITY tuvs "http://finder.tuwien.ac.at/vocab/spatial#">
        <!ENTITY org "http://www.w3.org/ns/org#" >
        <!ENTITY foaf "http://xmlns.com/foaf/0.1/" >
        <!ENTITY xsd "http://www.w3.org/2001/XMLSchema#" >
        <!ENTITY rdfs "http://www.w3.org/2000/01/rdf-schema#" >
        <!ENTITY rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#" >
        ]>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="&tuvs;"
                xmlns:tuvs="&tuvs;"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:foaf="&foaf;"
                xmlns:org="&org;"
                xmlns:tiss="https://tiss.tuwien.ac.at/api/schemas/tiss/v11"
                xmlns:tissPerson="https://tiss.tuwien.ac.at/api/schemas/person/v14"
>

    <xsl:output indent="yes" method="xml"/>

    <xsl:template match="persons">
        <rdf:RDF xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                 xmlns:tuvs="&tuvs;"
                 xmlns:rdf="&rdf;"
                 xmlns:foaf="&foaf;"
                 xmlns:org="&org;"
        >
            <xsl:apply-templates select="//tissPerson:person"/>
            <xsl:apply-templates select="//tissPerson:room"/>
        </rdf:RDF>
    </xsl:template>

    <xsl:template match="//tissPerson:person">
        <xsl:variable name="personId" select="concat('OID',@oid)"/>
        <xsl:variable name="firstName" select="tissPerson:firstname"/>
        <xsl:variable name="lastName" select="tissPerson:lastname"/>
        <xsl:variable name="title"
                      select="normalize-space(string-join((tissPerson:preceding_titles, tissPerson:postpositioned_titles), ' '))"/>
        <foaf:Person rdf:about="&organizational;person/id/{$personId}">
            <xsl:if test="$title != ''">
                <foaf:title rdf:datatype="&xsd;string">
                    <xsl:value-of select="$title"/>
                </foaf:title>
            </xsl:if>
            <foaf:name rdf:datatype="&xsd;string">
                <xsl:value-of select="string-join(($firstName, $lastName),' ')"/>
            </foaf:name>
            <foaf:givenName rdf:datatype="&xsd;string">
                <xsl:value-of select="$firstName"/>
            </foaf:givenName>
            <foaf:familyName rdf:datatype="&xsd;string">
                <xsl:value-of select="$lastName"/>
            </foaf:familyName>
            <xsl:choose>
                <xsl:when test="tissPerson:gender='M'">
                    <foaf:gender rdf:datatype="&xsd;string">male</foaf:gender>
                </xsl:when>
                <xsl:when test="tissPerson:gender='W'">
                    <foaf:gender rdf:datatype="&xsd;string">female</foaf:gender>
                </xsl:when>
            </xsl:choose>
            <xsl:for-each select="tissPerson:employee/tissPerson:employment">
                <xsl:variable name="orgunitId" select="concat('OID',tissPerson:organisational_unit/@oid)"/>
                <org:memberOf rdf:resource="&organizational;organization/id/{$orgunitId}"/>
                <xsl:for-each select="tissPerson:room">
                    <xsl:if test="tissPerson:room_code != ''">
                        <org:basedAt rdf:resource="&spatial;room/id/{tissPerson:room_code}"/>
                    </xsl:if>
                </xsl:for-each>
            </xsl:for-each>
        </foaf:Person>
    </xsl:template>

    <xsl:template match="//tissPerson:room">
        <xsl:if test="tissPerson:room_code != ''">
            <tuvs:Room rdf:about="&spatial;room/id/{tissPerson:room_code}">
                <rdf:type rdf:resource="&tuvs;OfficeRoom"/>
                <tuvs:roomCode>
                    <xsl:value-of select="tissPerson:room_code"/>
                </tuvs:roomCode>
            </tuvs:Room>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
