/**
 * This javascript file contains methods for transforming
 * geometry.
 *
 * @author Kevin Haller
 */

function FinderMapInformation(center, geometries) {

    this.center = center;
    this.geometries = geometries;

    /**
     * Transforms the geometries to features that can be
     * placed on a open layer 3 map.
     *
     * @returns features that has been transformed from
     * geometries of this map information object.
     */
    this.transformGeometriesToFeatures = function () {
        var format = new ol.format.WKT();
        return this.geometries.map(function (geometry) {
            return (format.readFeature(geometry, {
                dataProjection: 'EPSG:4326',
                featureProjection: 'EPSG:3857'
            }));
        });
    }
}
