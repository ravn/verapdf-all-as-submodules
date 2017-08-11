# NOTE: BITROTTED! GIT PROJECTS NO LONGER HANG TOGETHER AT THE POINTS STORED HERE..

# newspaper-all-as-submodules
newspaper project with all components as submodules for easier handling

Important:

Note: For now all commands work on TRA's Ubuntu 15.10 box.  Adapt as
appropriate for other platforms.  Two issues prohibit compiling with
Java 8 - increased security with wsimport, and more restrictive javadoc.
Note that OpenJDK 7 has certificate problems with some maven repositories.

* Fedora Commons artifacts are hosted on DuraSpace, not in Maven Central!
* Do not use Maven 3.3.3 - there appears to be a bug triggered by domsutil-webservice-common
* Ninestars QA tool not active, as it is linked against an older snapshot which would
  make the tree unnecessarily complex and is unimportant to the purpose of this project.
* For Ubuntu, download Java 7 from Oracle, and increase PermGen size.

    export JAVA_HOME=$HOME/gnu/jdk1.7.0_79/
    export MAVEN_OPTS="-Xmx512m -XX:MaxPermSize=128m"

* For OS X, download the latest Java 7 from http://www.azul.com/downloads/zulu/zulu-mac/
(and the corresponding CCK from http://www.azul.com/products/zulu/cck-downloads/), and increase PermGen size.


    export JAVA_HOME=/Users/ravn/gnu/zulu7.13.0.1-jdk7.0.95-macosx_x64
    export MAVEN_OPTS="-Xmx512m -XX:MaxPermSize=128m"


* When compiling with OpenJDK, open `sbforge-parent/reference/bitrepository-core/src/main/java/org/bitrepository/common/utils/FileSizeUtils.java`
and comment out the unused import in line 24:


    // import sun.nio.ch.DevPollSelectorProvider;


To avoid hitting the defunct repository pulled in by fedora and to avoid sbforge.org consider
adding these lines to /etc/hosts

    # do not download from sbforge or repo.aduna-software.org
    127.0.0.1 sbforge.org repo.aduna-software.org

For now run these commands to get a buildable tree:

    perl repos.pl | sh -

(A shell script is generated to mirror all repositories to the git/ folder, and
create local clones from this.  This mean that multiple versions of the same
github repository are only transferred once).


Install missing artifacts:
--

    mvn install:install-file -Dfile=not-in-central/jta-1.0.1B.jar -DgroupId=javax.transaction -DartifactId=jta -Dversion=1.0.1B  -Dpackaging=jar
    mvn install:install-file -Dfile=not-in-central/postgresql-9.2-1002.jdbc4.jar -DgroupId=postgresql -DartifactId=postgresql -Dversion=9.2-1002.jdbc4  -Dpackaging=jar

Work around repository configurations:
---

Work around repository configurations in sources by ensuring the following snippet
is present in $HOME/.m2/settings.xml (which overrides sbforge-nexus entries
with a pointer to Duraspace where the Fedora Commons artifacts are available)
(See http://stackoverflow.com/a/20520713/53897)

    <settings>
    <profiles>
    <profile>
       <id>duraspace-as-sbforge</id>

       <!--Enable snapshots for the built in central repo to direct -->
       <!--all requests to nexus via the mirror -->
       <repositories>
	      <repository>
	        <id>sbforge-nexus</id>
            <name>Duraspace Thirdparty Maven Repository</name>
            <url>https://m2.duraspace.org/content/repositories/thirdparty</url>
	        <releases><enabled>true</enabled></releases>
	        <snapshots><enabled>false</enabled></snapshots>
	      </repository>
       </repositories>
    </profile>
    </profiles>
    <activeProfiles>
      <activeProfile>duraspace-as-sbforge</activeProfile>
    </activeProfiles>
    </settings>

IntelliJ:
---

Invalidate and restart, before opening up the source project.  Disable
"dockerintegrationtests" profile and disable the test button before
actually invoking maven from inside IntelliJ.

Eclipse:
---

Importing existing maven projects in Eclipse 4.5.2 triggers a known bug, and 
importing fails.  

Netbeans:
----
Remember to set netbeans_jdkhome to a Java 7 JDK before launching Netbeans.
In the Tools->Options->Java->Maven panel set "Global Execution Options" to

    -DskipTests -P!dockerintegrationtests -P!RedisIntegrationTests -P!PostgresIntegrationTests


Command line:
---

    mvn -q -e -Dmaven.compiler.fork=true -DskipTests '-P!dockerintegrationtests' '-P!RedisIntegrationTests' '-P!PostgresIntegrationTests' -Dintegration.test.newspaper.properties=`pwd`/empty.properties clean install

# verapdf-all-as-submodules
