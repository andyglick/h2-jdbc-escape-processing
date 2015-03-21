H2 JDBC Escape Processing
=========================

the H2 team introduced a change at release 1.4.181 and after the change if H2 is running in client server mode
then both the server and the client must be running versions of H2 greater than 1.4.181 or JDBC escape processing
expressions aren't handled correctly

as an example the following query fails if there is a version mismatch between the client and the server

    SELECT { TS '2015-03-21 12:12:12.12' }

the query works correctly if the versions match

In our project environment we have been using a plugin net.xeric.maven:h2-maven-plugin:1.0-SNAPSHOT to execute the H2 server, and
when we went to upgrade our H2 version it didn't occur to us that if we didn't update the version of H2 being used by the plugin then we
would be committing the mismatched version error.

So we realized that we had caused the H2 problem in our own environment. It occurred to me that it ought to be possible to reconfigure the plugin to use a
different version of the H2 jar in the active Maven pom without making any changes to the plugin. (see plugin specification below)

This repo works out some of the details of our experiments around this

There are 6 test classes, there is one each for { D '2015-03-21' }, { T '12:12:12.12' }, { TS '2015-03-21 12:12:12.12' },
and there is a test that runs in the Maven test phase whose Java classname ends with "*Test" and another which runs in the
Maven integration-test phase whose Java classname ends with "*IT"

In the case of the test classes whose name ends with "*Test" using the JUnit @BeforeClass and @AfterClass annotations a class level
startup and shutdown method run and then stop the database server which runs in the JUnit test class as the test that is

The tests whose class names end with "*IT" depend upon the Maven pre-integration-test phase to start an H2 server running, the server is
shutdown in the post-integration-test-phase.

In all test classes a connection is made to the server database before the test methods run which configures a named database in the server context.
Each test case which runs against a server executes a jdbc connect string which has had IFEXISTS=TRUE appended to it, so server connections will only be made
if an existing connection with the same connection parameters has already been established.

      <plugin>
        <groupId>net.xeric.maven</groupId>
        <artifactId>h2-maven-plugin</artifactId>
        <dependencies>
          <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>${h2.version}</version>
          </dependency>
        </dependencies>
        <configuration>
          <args>
            <arg>-tcp</arg>
            <arg>-tcpAllowOthers</arg>
          </args>
        </configuration>
        <executions>
          <execution>
            <id>h2-start</id>
            <phase>pre-integration-test</phase>
            <goals>
              <goal>h2-start</goal>
            </goals>
          </execution>
          <execution>
            <id>h2-stop</id>
            <phase>post-integration-test</phase>
            <goals>
              <goal>h2-stop</goal>
            </goals>
          </execution>
        </executions>
      </plugin>