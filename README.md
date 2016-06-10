# Neo4j Sample Liferay Portlet


## Introduction

First place I am a developer, I work for Liferay and I love Neo4j. So I thought I could combine these things and develop something to learn and have fun. As I am still a newbie to Neo4j, in the (hopefully near) future I probably continue the development with new stuff, which is going to be more complex and professional.

But until that, this is just a small app that serves demonstration purposes how to use the brand new Liferay Blade development environment along with Neo4j.


## Usage

You can compile and deploy the portlet via using the official Liferay Blade CLI. Regarding the usage of Blade, please see the official [documentation] (https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-0/blade-cli).

Once you successfully installed and configure Blade you can deploy the portlet with the `blade gw clean deploy` command from the portlet root folder.


## Version History

- #### 1.0

  - The very fist version of the portlet. It supports only basic Cypher queries with plain text response display.
  - It uses embedded Neo4j JAVA language driver, since there is no available OSGi version yet
 
- #### 1.1

  - Liferay [neo4j-osgi-driver](http://repo1.maven.org/maven2/com/liferay/neo4j/neo4j-osgi-driver/1.0.0/) just released! The portlet now uses this dependency instead of embedding the driver.
  - Since the wrapper is open source and publicly available, please feel free to use it in your project as well: 

    Gradle: 
    
    ```
     compile 'com.liferay.neo4j:neo4j-osgi-driver:1.0.0'
    ```
    
    Maven: 
    
    ```
    <groupId>com.liferay.neo4j</groupId>
    <artifactId>neo4j-osgi-driver</artifactId>
    <version>1.0.0</version>
    ```


## Future plans

As we are progressing with some internal development I am planning to update this project with all the ideas that worth to share publicly.
 
So stay tuned, more stuff is coming! :smile: :wink: