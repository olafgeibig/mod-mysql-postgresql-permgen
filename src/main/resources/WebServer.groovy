import groovy.json.JsonBuilder
import org.vertx.groovy.core.http.RouteMatcher
import org.vertx.groovy.core.http.impl.DefaultHttpServer
import org.vertx.groovy.platform.Verticle
import groovy.json.JsonSlurper

RouteMatcher routeMatcher = new RouteMatcher()
def eventBus = vertx.eventBus
def log = container.logger

routeMatcher.get('/jobs/tag') { request ->
    eventBus.send('UrbanAirship.tag.getAll', '') { message ->
        //log.info "${System.currentTimeMillis()} ${message.body}"
        def json = new JsonSlurper().parseText(message.body)
        request.response.putHeader('Content-Type', 'application/json; charset=utf-8')
        request.response.statusCode = json.statusCode
        request.response.statusMessage = json.statusMessage
        def body = ''
        if(request.response.statusCode == 200) {
            body = new JsonSlurper().parseText(json.content)
        } else {
            body = json.content
        }
        def responseJson = new JsonBuilder()
        responseJson {
            statusCode json.statusCode
            statusMessage json.statusMessage
            content body
        }
        request.response.end(responseJson.toString())
    }
}

routeMatcher.put('/jobs/tag/:tag') { request ->
    eventBus.send('UrbanAirship.tag.create', request.params.tag) { message ->
        //log.info "${System.currentTimeMillis()} ${message.body}"
        def json = new JsonSlurper().parseText(message.body)
        request.response.statusCode = json.statusCode
        request.response.statusMessage = json.statusMessage
        request.response.end(json.statusMessage)
    }
}

routeMatcher.delete('/jobs/tag/:tag') { request ->
    eventBus.send('UrbanAirship.tag.delete', request.params.tag) { message ->
        //log.info "${System.currentTimeMillis()} ${message.body}"
        def json = new JsonSlurper().parseText(message.body)
        request.response.statusCode = json.statusCode
        request.response.statusMessage = json.statusMessage
        request.response.end(json.statusMessage)
    }
}

// serve all files from directory jobs
routeMatcher.getWithRegEx("^\\/jobs\\/.*") { req ->
    req.response.sendFile(req.path.substring(1))
}

DefaultHttpServer server = vertx.createHttpServer()
server.requestHandler(routeMatcher.asClosure())

vertx.createSockJSServer(server).bridge(prefix: '/eventbus', [[:]], [[:]])

server.listen(8080)
container.logger.info("Jobs webserver running!")
