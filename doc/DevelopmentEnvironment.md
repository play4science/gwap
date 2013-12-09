# Setting up the GWAP Development Environment

## Eclipse & JBoss

-   Download Eclipse Java EE:
    [http://www.eclipse.org/downloads/](http://www.eclipse.org/downloads/)
-   Unzip Eclipse and run it
-   Help -> Eclipse Marketplace
    -   Search for "JBoss Tools"
    -   Select and install: Hibernate Tools, JBoss Tools JSF, JBoss
        Tools RichFaces, JBossAS Tools, Seam Tools
    -   Restart Eclipse when prompted to do so
-   Download JBoss 4.2:
    [http://www.jboss.org/jbossas/downloads/](http://www.jboss.org/jbossas/downloads/)
-   Extract and remember the directory where you extracted it (e.g.`/opt/jboss` or `C:\\jboss`)
-   File -> Import -> Project from GIT: `https://github.com/play4science/gwap.git`
-   Select "Find projects [..]"
-   Window -> Show view -> Ant, add (Button on top right of this view) `build.xml`
-   Only for OS X:
    -   In project settings, set `Default Encoding` to `UTF-8`
    -   In Preferences, in Java -> Installed JREs -> select used JDK ->
        Edit -> Default VM Arguments: `-Dfile.encoding=UTF-8`
-   Ant targets (doubleclick in Ant-View):
    -   `local-settings` (enter the JBoss directory from above)
    -   `prepare-jboss`
    -   `buildtest`
-   In the projects view right-click on "gwap" -> refresh (the red exclamation mark is hopefully gone now)
-   Run Ant target `deploy`
-   In the "Servers"-view:
    -   Create JBoss 4.2 Runtime Server (Community Version!) and select the JBoss directory
    -   Do __not__ add the gwap-project to the configured projects
-   Run JBoss in debug-mode

## PostgreSQL Database

-   Install PostgreSQL (9.1 or later)
-   Create database (psql command prompt)

        create user gwap password 'gwap';
        create database gwap owner gwap;

-   Load database schema and data into database
    -   Possibility 1: Load existing dump in PostgreSQL

			psql -h localhost -U gwap gwap < DUMP.sql

    -   Possibility 2: Let JBoss create the tables
        -   edit the file: `resources/META-INF/persistence-dev.xml`:

                <property name="hibernate.hbm2ddl.auto" value="create-drop"/>

        -   `ant redeploy`
        -   start JBoss
        -   in `persistence-dev.xml` change to `value="update"`
        -   `ant redeploy`

-   Update path in table "source", e.g.:

        UPDATE source SET
        url='[JBoss-directory]/server/default/deploy/GWAP.ear/artigo.war/'
        WHERE id=1;

## Solr (Search)

-   Deploy Solr: `ant solr-deploy`
-   Open the following to create the search index (default credentials: "solr" / "solr"):
    -    [http://localhost:8080/solr/artigo/dataimport?command=full-import](http://localhost:8080/solr/artigo/dataimport?command=full-import)
    -    [http://localhost:8080/solr/metropolitalia/dataimport?command=full-import](http://localhost:8080/solr/metropolitalia/dataimport?command=full-import)

## Basic Commands for Deployment

-   Update server with new files (xhtml, js, css): `ant deploy`
-   Update server with new Java files: `ant redeploy`
-   Change which projects are deployed: `build.properties`
-   Default URL for deployment:
    -   [http://localhost:8080/artigo](http://localhost:8080/artigo)
    -   [http://localhost:8080/metropolitalia](http://localhost:8080/metropolitalia)
    -   [http://localhost:8080/elearning](http://localhost:8080/elearning)
    -   ... (by the name of the subproject)

