elasticsearch-hotsearch
=======================

An ElasticSearch Plugin for showing hot searches around the world.

* plugin name: elasticsearch-hotsearch
* Features: hot search viewer via web browser
* Currently support: elasticsearch versions 1.3.0 (maybe 1.3.X) and Oracke Java 7 or above.

#### Build

* Execute "mvn package" in this project top directory (called PROJECT_HOME).
  * Then, the plugin package will be generated in PROJECT_HOME/target/release/elasticsearch-index-inspector-[VERSION].zip
* For developer, as above, you can execute "mvn eclipse:eclipse" and then import it as an Eclipse project.

#### Idea
What are people searching about movies or other things? Show them on the world map!!!

#### Implementation
##### Data
Generate the query logs automatically!

Keywords DATETIME

##### Front End
Use Javasript to show the movie searches as different colors according to each City(Given the name), when hover on it or click on it, show details of the city.
