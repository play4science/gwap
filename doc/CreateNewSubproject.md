# How to create a new subproject for the play4science platform

## 1. Creating directory structure
Create the same directory structure as in an existing subproject. The important directories are

*   `resources/artigo -> resources/SUBPROJECT` (copy this one)
*   `view/artigo -> view/SUBPROJECT` (create a folder and copy some of the necessary files)

## 2. Modifying WEB-INF files
Edit the `*.xml`-files in the `resources/SUBPROJECT/WEB-INF` folder so that all occurrences of _artigo'' are replaced by ''SUBPROJECT_ and make further changes that you think are necessary.

## 3. Modifying your build.properties
To deploy your project, define your subproject in build.properties for deployment.
