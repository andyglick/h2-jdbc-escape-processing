H2 JDBC Escape Processing
=========================

the H2 team introduced a change at release 1.4.181 and after the change if H2 is running in client server mode
then both the server and the client must be running versions of H2 greater than 1.4.181 or JDBC escape processing expressions aren't handled correctly

as an example the following query fails if there is a version mismatch between the client and the server

    SELECT { TS '2015-03-21 12:12:12.12' }

the query continues to work correctly if the versions match

In our project environment we have been using a plugin to execute the H2 server, and when we went to upgrade our H2 version it
didn't occur to us that if we didn't update the version of H2 being used by the plugin then we would be committing the mismatched
version error

This repo works out some of the details of our experiments around this