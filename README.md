sctrcd-fx-web
=============

This project is a Java web application based on the Spring Framework. The intent is to provide simple examples of using Drools within a web application.

It's built with Maven, so from the command line you can:

Run a full build (including tests):

    mvn clean install

Run the web application in a Tomcat container:

    mvn tomcat7:run

Assuming that you have run it up in Tomcat, you can now validate IBANs. For instance:

    curl http://localhost:9090/iban/validate/GB29NWBK60161331926819
    
... will tell you that the IBAN is valid. At least it's valid from the perspective of a 
Mod-97 check and has the right kind of structure.

    curl http://localhost:9090/iban/validate/GB29NWBK60161331926819

... should tell you that it failed the Mod-97 check.
