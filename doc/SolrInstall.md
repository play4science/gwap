# Solr Installation

## Automatic installation
* run the `solr-deploy` ant target
* try accessing `http://localhost:8080/solr/`

## Manual installation
* Prerequisites: JBoss 4.2 and Solr 3.3
* Stop JBoss
* Modify libraries bundled in `examples/webapps/solr.war` (in `WEB-INF/lib`):
    * delete the beta version of velocity-tools-2.0 and add the non-beta version from [here](http://velocity.apache.org/download.cgi)
    * delete log4j-over-slf4j-1.6.1.jar slf4j-jdk14-1.6.1.jar
    * add slf4j-log4j12-1.6.1.jar from [here](http://www.slf4j.org)
    * important: the files must stay in solr.war, unpacking the whole stuff into the JBoss directory does not work
* Copy solr.war to the JBoss deploy directory
* copy `examples/solr` to the jboss home directory from where jboss is run, e.g. `/opt/jboss/bin`
* Note: the other methods for setting the solr home directory from [here](http://wiki.apache.org/solr/SolrJBoss) did not work, perhaps by using `-Dsolr.solr.home=/path/to/solr/home` in run.conf/run.sh?
* Start JBoss
