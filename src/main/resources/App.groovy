container.with {
    deployVerticle('WebServer.groovy')
    deployVerticle('UrbanAirshipClient.groovy')
    deployVerticle('TaggingEngine.groovy')
}
