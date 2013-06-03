Shi3ld for HTTP operations
===========

[Shi3ld for HTTP](http://wimmics.inria.fr/projects/shi3ld-ldp) is an access control module for enforcing authorization on triple stores. 
Shi3ld for HTTP protects HTTP operations on [Linked Data](linkeddata.org) and relies on attribute-based access policies.

### Features

* Authorization for r/w HTTP Methods on RDF resources
* RDF Resource-oriented
* Policy Language in RDF/SPARQL or RDF only
* Attribute-based
* "Context-aware" Policies

The policy vocabularies namespace documents are available at:
* [S4AC](http://ns.inria.fr/s4ac) - for modelling Access Policies.
* [PRISSMA](http://ns.inria.fr/prissma) - for modelling context client attributes. 

### Scenarios

Shi3ld for HTTP supports three different scenarios and are available in this repository branches:

* Shi3ld for [SPARQL Graph Store Protocol](http://www.w3.org/TR/sparql11-http-rdf-update/)
* Shi3ld for [Linked Data Platform](http://www.w3.org/TR/ldp-ucr/) (SPARQL-based)
* Shi3ld for [Linked Data Platform](http://www.w3.org/TR/ldp-ucr/) (SPARQL-less)

Scenarios are detailed in our paper [Access Control for HTTP Operations on Linked Data](http://hal.inria.fr/docs/00/81/50/67/PDF/eswc2013_shi3ld.pdf)
 

### Installation

All Shi3ld scenarios are Java server side modules that run in a java application server (e.g. Tomcat)

The `config.properties` property file needs to be customized with the policy storage path and the triple storage path. 

The Shi3ld-GSP scenario is compatible with the GSP-compliant [Fuseki SPARQL engine](http://jena.apache.org/documentation/serving_data/index.html) needs the Fuseki server URL and the Fuseki dataset name.

The Shi3ld-LDP scenarios embed the [Corese/KGRAM](http://wimmics.inria.fr/corese) RDF store and SPARQL processor.

### Testing

Shi3ld-HTTP can be tested with a [standalone client](http://wimmics.inria.fr/projects/shi3ld-ldp/shi3ld-test-client.zip) shipped with sample client attributes.

Sample Access Policies can be found [here](http://wimmics.inria.fr/projects/shi3ld-ldp/shi3ld-test-policies.zip).