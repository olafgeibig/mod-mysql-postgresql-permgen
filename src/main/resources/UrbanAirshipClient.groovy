import groovy.json.JsonBuilder

def eventBus = vertx.eventBus
def log = container.logger
def secret = 'Abx_aSWnQBeD_8lyf5aoQA:Xtx4EA5cQPqxyGHSOQawLA'
//def secret = 'AcOTEs2RRh6mPL0KuqNNBA:9XFMndxJQcmz_hG8QgBEKA'

def encoded = javax.xml.bind.DatatypeConverter.printBase64Binary(secret.getBytes("UTF-8"))
def authString = "Basic $encoded"

/**
 * Gets the tags from Urban Airship. Responds with a json:
 * <code>
 * {
 *     statusCode the urban airship response status code
 *     statusMessage the urban airship response status message
 *     content the urban airship response body
 * }
 * </code>
 * @param message ignored
 * @return json
 */
eventBus.registerHandler("UrbanAirship.tag.getAll") { message ->
    def client = vertx.createHttpClient(port: 443, SSL: true, trustAll: true, host: "go.urbanairship.com")
    def request = client.request("GET", "/api/tags/") { response ->
        response.bodyHandler { body ->
            def json = new JsonBuilder()
            json {
                statusCode response.statusCode
                statusMessage response.statusMessage
                content body.toString()
            }
            message.reply(json.toString())
        }
    }
    request.headers.set('Authorization', authString)
    request.end()
}

/**
 * Creates a tag at Urban Airship. Responds with a json:
 * <code>
 * {
 *     statusCode the urban airship response code
 *     statusMessage the urban airship status message
 * }
 * </code>
 * @param message the tag
 * @return response json
 */
eventBus.registerHandler("UrbanAirship.tag.create") { message ->
    def client = vertx.createHttpClient(port: 443, SSL: true, trustAll: true, host: "go.urbanairship.com")
    def request = client.request("PUT", "/api/tags/${message.body}") { response ->
        response.bodyHandler { body ->
            def json = new JsonBuilder()
            json {
                statusCode response.statusCode
                statusMessage response.statusMessage
                content body.toString()
            }
            message.reply(json.toString())
        }
    }
    request.headers.set('Authorization', authString)
    request.end()
}

/**
 * Deletes a tag at Urban Airship. Responds with a json:
 * <code>
 * {
 *     statusCode the urban airship response code
 *     statusMessage the urban airship status message
 * }
 * </code>
 * @param message the tag
 * @return response json
 */
eventBus.registerHandler("UrbanAirship.tag.delete") { message ->
    def client = vertx.createHttpClient(port: 443, SSL: true, trustAll: true, host: "go.urbanairship.com")
    def request = client.request("DELETE", "/api/tags/${message.body}") { response ->
        response.bodyHandler { body ->
            def json = new JsonBuilder()
            json {
                statusCode response.statusCode
                statusMessage response.statusMessage
            }
            message.reply(json.toString())
        }
    }
    request.headers.set('Authorization', authString)
    request.end()
}
