def config = container.config

container.with {
    deployVerticle('WebServer.groovy')
    deployVerticle('UrbanAirshipClient.groovy')
    deployVerticle('TaggingEngine.groovy')

    // Deploy a MongoDB persistor module
    deployModule('io.vertx~mod-mongo-persistor~2.1.0', config.'mongo-persistor') { asyncResult ->
        if (asyncResult.succeeded) {
            // And when it's deployed run a script to load it with some reference
            // data for the demo
            //deployVerticle('WebServer.groovy')
            container.logger.info "Successfully connected to MongoDB"
        } else {
            container.logger.error "Failed to deploy mongo-persistor ${asyncResult.throwable}"
        }
    }

    deployModule('io.vertx~mod-mysql-postgresql~0.3.0-SNAPSHOT', config.'mysql-postgresql') { asyncResult ->
        if (asyncResult.succeeded) {
            // And when it's deployed run a script to load it with some reference
            // data for the demo
            //deployVerticle('WebServer.groovy')
            container.logger.info "Successfully connected to PostgreSQL DB"
        } else {
            container.logger.error "Failed to deploy mysql-postgresql ${asyncResult.throwable}"
        }
    }
}
