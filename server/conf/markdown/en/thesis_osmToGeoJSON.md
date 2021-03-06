Comparing Open Street Map Data and GeoJSON
==========================================

## OSM Data

Open Street Map data is structured into `node`s, `way`s and `relation`s. Additionally, it stores meta 
information as `tag`s.

Let's look at these elements individually:

### `node`

A `node` is the smallest entity, the "atom" of OSM. It represents precisely one point on the surface 
of the earth. Besides its id, a node contains at least latitude and longitude.

`node`s are used for different purposes. For one, the shape of ways is defined by its `node`. Another
purpose is the designation of points of interest and other features. For example, a town might be 
designated by a `node` in its center.

`node`s can contain `tag`s. If a `node` is used to designate a feature, then it must have `tags` describing
the feature. If it is used as part of a `way`, this is not necessary but still possible.

A `node` can also be a member of a `relation`.

### `way`

`way`s are ordered collections of `node`s. They represent not only roads, but also rivers and other linear
features like boundaries of just about anything (e.g. a building). Thus, they are polylines and can form 
polygons. What they represent is determined by their `tag`s.

### `relation`

`relation` is even more general than `way`. A `relation` can be any group of at least 
two other elements (`node`s, `way`s or `relation`s). For example, several roads (`way`s) can form a route relation.
Or the boundary of of Lower Austria is represented by a multipolygon relation with a hole (Vienna).

### `tag`

`tag`s are crucial in defining what a `node`, `way` or a `relation` really *is*. While the basic elements
define the geometry of *something*, `tag`s describe it.

`tag`s are basically key-value-pairs. Some examples include:

* `route=bus` designates a `relation` as a bus route.
* `highway=residential` designates a `way` as a highway to residential areas.
* `name=London Bridge` gives a name to a `way` or `relation`.
* `traffic_sign=city_limit` designates a `node` as the point where a specific traffic sign stands.

The dictionary of `tag`s is defined by convention rather than fixed.

## GeoJSON

GeoJSON is format for largely the same purpose as OSM. However, where OSM data dumps are intended to
be imported into a relational database and thus flat, GeoJSON contains more structure to begin with.

Let's look at the example on the front page of the [official site](http://geojson.org):


    {
      "type": "Feature",
    
      "geometry": {
        "type": "Point",
        "coordinates": [125.6, 10.1]
      },
      "properties": {
        "name": "Dinagat Islands"
      }
    }


If we run this through some [converter](https://github.com/aaronlidman/osm-and-geojson) to OSM, we get this:

    <?xml version="1.0" encoding="UTF-8"?>
	<osm version="0.6" generator="github.com/aaronlidman/osm-and-geojson">
		<node id="-1" lat="10.1" lon="125.6" changeset="false">
			<tag k="name" v="Dinagat Islands"/>
		</node>
	</osm>

There are some similarities here, and the single JSON record corresponds to a single node. However, one must 
not overlook that this is a very simple case because the "geometry" in GeoJSON can have other "type"s besides 
"Point" as well. Changing the "type", to something more complex, we would have to use a `way` or `relation` to 
represent this entity as OSM data.

We also note that there is no meaningful id, but id is a required attribute. This is OSM is meant to be a 
globally unique database, while GeoJSON just describes geographical data without reference to a bigger context.

In GeoJSON, geometry is described with a different vocabulary, namely:

* Point
* MultiPoint
* LineString
* MultiLineString
* Polygon
* MultiPolygon
* Geometry

## Converting OSM Data to GeoJSON

Many converters from OSM to GeoJSON exist. Some examples include:

* [OSM & GeoJSON](https://github.com/aaronlidman/osm-and-geojson), a JavaScript module able to convert in both
directions, used above.
* [osmtogeojson](http://tyrasd.github.io/osmtogeojson/), a JavaScript module converting one direction only.
* [OSM2GEO.js](https://gist.github.com/tecoholic/1396990), yet another JavaScript module.

However, converting from one format to another is not trivial. If we compare the geometrical vocabulary of OSM 
and GeoJSON, we note that GeoJSON's vocabulary is richer, i.e. it has words for some forms where OSM has none.
For example, we can describe the boundaries of Vienna as a polygon, but OSM knows nothing about polygons.

While we *can* form a polygon from ways and relations, it is not clear to see that they *do* in fact form a polygon.
This is the reason why such converters advertise themselves with phrases like "true polygon detection".

## A JSON format closer to OSM

My goal is not to write yet another converter to GeoJSON as this would have several drawbacks:

* We do not immediately know the correspondence of an element here to an element there. This makes it hard to use
  changesets.
* It does nothing towards our goal of representing OSM data as closely as possible in the couch db.

In contrast to this, JSON itself is isomorphic to XML, so we can easily just convert OSM XML to JSON directly. Let's
take a look at the output of the [Overpass API](http://overpass-api.de/):

    {
      "type": "way",
      "id": 1,
      "nodes": [
        10,
        11,
        12
      ],
      "tags": {
        "highway": "tertiary",
        "name": "Main Street"
      }
    }

This is straightforward. However, for any practical purposes it is hardly good enough to store in a CouchDB. There is
no spatial information stored in the way, and we cannot join it *inside the database*. Of course, one might choose
to receive above piece of data from a query and then do three more queries to retrieve nodes 10, 11 and 12. Only after
that we know *where* this specific way is and can do something with it, e.g. draw it on a map or even decide whether
to include it in a result set or not.

However, this is impractical for spatial queries. Just imagine looking up each and every point on the whole earth 
just to know if they are within 100 km of Vienna! Perhaps we do not need spatial queries but rather query ways like:
"give me the entire highway A1 and everything related to it." Even if we can find an application where questions such
as this are useful, it's still not efficient: We must allow for HTTP roundtrip times for each member of every relation
and every way involved! Assuming an average roundtrip time of perhaps 50ms, this would amount to almost a minute seconds
for a relation with 1000 subqueries involved. Parallel querying can mitigate some of that cost, but we really want to
retrieve a set of results in a single (or at least very few) roundtrips.

For the purpose of this project, I propose three different extensions of this format, which gives us four stages:

### Stage 0

This is the above format implemented, so I can measure its real performance penalty.

Everything is saved into the database as-is. In some cases additional http roundtrips have to be 
performed in order to answer a query satisfactorily. Some questions can not be answered at all.

### Stage 1

Nodes know which ways they belong to. This way, we can have views depicting complete ways including their
geographical location without needing additional roundtrips. It is not yet determined whether spatial
indexing works.

### Stage 3

Ways unfolding. Node ids are replaced by their respective nodes in ways.

### Stage 2

Full unfolding. Ways and relations are augmented with geographical data.
