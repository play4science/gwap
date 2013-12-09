# How to create a single-player game

## 1. Controller bean for the game
Create a new class extending _gwap.game.AbstractGameSessionBean_, e.g.:
    
    @Name("mitStatementLabelerBean")
    @Scope(ScopeType.CONVERSATION)
    public class StatementLabelerBean extends AbstractGameSessionBean {
        @Override
        public void startGameSession() {
            startGameSession("mitStatementLabeler");
        }
    }
    
Note that the argument for the _startGameSession_ method is the name of the GameType to be created and thus should exist like this in the database table for gametypes.

## 2. Page flow
Clone an existing pageflow which uses such a controller bean, e.g., `/resources/imageLabeler.jpdl.xml` and edit

* name of the pageflow (top-level xml element)
* view-id's
* action expressions
* pageflow itself (if needed, look at other `*.jpdl.xml`-files)

And register pageflow in `/resources/[SUBPROJECT-NAME]/WEB-INF/components.xml`, e.g.:
    
    <bpm:jbpm>
        <!-- ... other definitions ... -->
        <bpm:pageflow-definitions>
            <value>[PAGEFLOW-FILE, e.g. imageLabeler.jpdl.xml]</value>
        </bpm:pageflow-definitions>
    </bpm:jbpm>
    

## 3. Views
Create the view which is mapped to the _start-page_ element in the pageflow, let's call it `imageLabeler.xhtml` and edit it according to your needs.
Furthermore, create a `imageLabeler.page.xml` (must be named using the same base name) in the same folder with the content:
    
    <?xml version="1.0" encoding="UTF-8"?>
    <page xmlns="http://jboss.com/products/seam/pages"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://jboss.com/products/seam/pages http://jboss.com/products/seam/pages-2.2.xsd">
    
         <begin-conversation pageflow="[NAME-OF-PAGEFLOW, e.g. imageLabeler]" join="true"/>
    
    </page>
    
And you're done. Test it!


## 4. Further examples
If you need further advice, look at the following files:
    
    /src/hot/gwap/mit/StatementLabelerBean.java
    /resources/mitStatementLabeler.jpdl.xml
    /resources/metropolitalia/WEB-INF/components.xml
    /view/metropolitalia/statementLabeler.xhtml
    /view/metropolitalia/statementLabelerScoring.xhtml
    
or ask :)
