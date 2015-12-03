Vivo Collaboration Service
=================

An application to track and report on which Vivo posts users are currently editing to facilitate collaborative working.

The Vivo Collaboration Service is a Scala application using the Scalatra framework.

Usage
-----
 - To run the Vivo Collaboration Service:

        sbt run
        
Testing
-------
 - To run unit tests
        
        sbt test
        
 - To run cucumber tests, you must run Mongo in the background. In separate tabs do:
 
        mongod
        sbt cucumber

Deployment
----------

- Commits will trigger a deploy to _int_ via [Jenkins](https://cps-jenkins-master.cloud.bbc.co.uk/view/vivo-collaboration/).
- Deployments to further environments can then be managed using [Cosmos](https://admin.live.bbc.co.uk/cosmos/component/vivo-collaboration).
- The app can then be accessed at https://vivo-collaboration.int.api.bbci.co.uk.
