import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import io.netty.handler.codec.http.HttpHeaders

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

eventBus.registerHandler("UrbanAirship.segment.getAll") { message ->
    def client = vertx.createHttpClient(port: 443, SSL: true, trustAll: true, host: "go.urbanairship.com")
    def request = client.request("GET", "/api/segments/") { response ->
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
    request.headers.set(HttpHeaders.Names.AUTHORIZATION, authString)
    request.end()
}

eventBus.registerHandler("UrbanAirship.segment.get") { message ->
    def client = vertx.createHttpClient(port: 443, SSL: true, trustAll: true, host: "go.urbanairship.com")
    def request = client.request("GET", "/api/segments/${message.body}") { response ->
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
    request.headers.set(HttpHeaders.Names.AUTHORIZATION, authString)
    request.end()
}

eventBus.registerHandler("UrbanAirship.segment.create") { message ->
    String c = message.body
    log.info(c)
    def client = vertx.createHttpClient(port: 443, SSL: true, trustAll: true, host: "go.urbanairship.com")
    def request = client.request("POST", "/api/segments/") { response ->
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
    request.headers.set(HttpHeaders.Names.AUTHORIZATION, authString)
    request.headers.set(HttpHeaders.Names.CONTENT_LENGTH, c.length() as String)
    request.headers.set(HttpHeaders.Names.CONTENT_TYPE, "application/json")
    request.write(c)
    request.end()
}

eventBus.registerHandler("UrbanAirship.segment.delete") { message ->
    def client = vertx.createHttpClient(port: 443, SSL: true, trustAll: true, host: "go.urbanairship.com")
    def request = client.request("DELETE", "/api/segments/${message.body}") { response ->
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

eventBus.registerHandler("UrbanAirship.push") { message ->
    String c = message.body
    log.info(c)
    def client = vertx.createHttpClient(port: 443, SSL: true, trustAll: true, host: "go.urbanairship.com")
    def request = client.request("POST", "/api/push/validate") { response ->
        response.bodyHandler { body ->
            def responseJson = new JsonBuilder()
            responseJson {
                statusCode response.statusCode
                statusMessage response.statusMessage
                content body.toString()
            }
            message.reply(responseJson.toString())
        }
    }
    // parsing the client data
    def pushData = new JsonSlurper().parseText(c)
    def audience
    if(pushData.audienceType == "tag") {
        audience = [ tag:[pushData.audienceName] ]
    } else if(pushData.audienceType =="segment") {
        audience = [segment:pushData.audienceName]
    }
    def data = [
        audience:audience,
        device_types:pushData.deviceType,
        notification:[alert:pushData.alert]
    ]
    def json = new JsonBuilder(data)
    def content = json.toString()
    log.info(content)
    request.headers.set(HttpHeaders.Names.AUTHORIZATION, authString)
    request.headers.set(HttpHeaders.Names.CONTENT_LENGTH, content.length() as String)
    request.headers.set(HttpHeaders.Names.CONTENT_TYPE, "application/json")
    request.headers.set(HttpHeaders.Names.ACCEPT, "application/vnd.urbanairship+json; version=3;")
    request.write(content)
    request.end()
}

