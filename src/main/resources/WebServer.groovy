import groovy.json.JsonBuilder
import org.vertx.groovy.core.buffer.Buffer
import org.vertx.groovy.core.http.RouteMatcher
import org.vertx.groovy.core.http.impl.DefaultHttpServer
import org.vertx.groovy.platform.Verticle
import groovy.json.JsonSlurper

RouteMatcher routeMatcher = new RouteMatcher()
def eventBus = vertx.eventBus
def log = container.logger

// generic get all
routeMatcher.get('/internal/job/:entity/') { request ->
    def auth = request.headers.Authorization

    if(!auth) {
        log.info(auth)
        request.response.statusCode = 401
        request.response.statusMessage = 'Unauthorized'
        request.response.end('Unauthorized')
    } else {
        log.info(auth)
        String entity = request.params['entity']
    eventBus.send("UrbanAirship.${entity}.getAll", '') { message ->
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
    }}
}

// generic create
routeMatcher.post('/internal/job/:entity/') { request ->
    String entity = request.params['entity']
    def body = new Buffer(0)
    request.bodyHandler { buffer ->
        body.appendBuffer(buffer as Buffer)
        eventBus.send('UrbanAirship.${entity}.create', body.toString()) { message ->
            log.info "${System.currentTimeMillis()} ${message.body}"
            def json = new JsonSlurper().parseText(message.body)
            request.response.statusCode = json.statusCode
            request.response.statusMessage = json.statusMessage
            request.response.end(json.statusMessage)
        }
    }
}

// generic delete
routeMatcher.delete('/internal/job/:entity/:id') { request ->
    String entity = request.params['entity']
    eventBus.send("UrbanAirship.${entity}.delete", request.params.id) { message ->
        //log.info "${System.currentTimeMillis()} ${message.body}"
        def json = new JsonSlurper().parseText(message.body)
        request.response.statusCode = json.statusCode
        request.response.statusMessage = json.statusMessage
        request.response.end(json.statusMessage)
    }
}

// create tag
routeMatcher.put('/internal/job/tag/:tag') { request ->
    eventBus.send('UrbanAirship.tag.create', request.params.tag) { message ->
        //log.info "${System.currentTimeMillis()} ${message.body}"
        def json = new JsonSlurper().parseText(message.body)
        request.response.statusCode = json.statusCode
        request.response.statusMessage = json.statusMessage
        request.response.end(json.statusMessage)
    }
}

routeMatcher.post('/internal/job/query/exec') { request ->
    def body = new Buffer(0)
    request.bodyHandler { buffer ->
        body.appendBuffer(buffer as Buffer)
        log.info(body.toString())
        eventBus.send('TaggingEngine.query.exec', body.toString()) { message ->
            //log.info "${System.currentTimeMillis()} ${message.body}"
            def json = new JsonSlurper().parseText(message.body)
            request.response.statusCode = json.statusCode
            request.response.statusMessage = json.statusMessage
            request.response.end(message.body.toString())
        }
    }
}

routeMatcher.post('/internal/job/query/sample') { request ->
    def body = new Buffer(0)
    request.bodyHandler { buffer ->
        body.appendBuffer(buffer as Buffer)
        eventBus.send('TaggingEngine.query.exec', body.toString()) { message ->
            //log.info "${System.currentTimeMillis()} ${message.body}"
            def json = new JsonSlurper().parseText(message.body)
            request.response.statusCode = json.statusCode
            request.response.statusMessage = json.statusMessage
            request.response.end(message.body.toString())
        }
    }
}

routeMatcher.post('/internal/job/pushNotification') { request ->
    def body = new Buffer(0)
    request.bodyHandler { buffer ->
        body.appendBuffer(buffer as Buffer)
        eventBus.send('UrbanAirship.push', body.toString()) { message ->
            log.info "${System.currentTimeMillis()} ${message.body}"
            def responseJson = new JsonSlurper().parseText(message.body)
            request.response.statusCode = responseJson.statusCode
            request.response.statusMessage = responseJson.statusMessage
            request.response.end(message.body.toString())
        }
    }
}

routeMatcher.get('/internal/job/foo') { request ->
    def auth = request.headers.Authorization

    if(!auth) {
        log.info(auth)
        request.response.statusCode = 401
        request.response.statusMessage = 'Unauthorized'
        request.response.end('Unauthorized')
    } else {
        auth -= 'Basic '
        def user = new String(javax.xml.bind.DatatypeConverter.parseBase64Binary(auth)).split(':')[0]
        log.info("$auth ${user}")
        if(user != 'foo') {
            request.response.statusCode = 401
            request.response.statusMessage = 'Unauthorized'
            request.response.end('Unauthorized')
        } else {
            request.response.statusCode = 200
            request.response.statusMessage = 'OK'
            request.response.end('OK')
        }
    }
}

// serve all files from directory jobs
routeMatcher.getWithRegEx("^\\/internal/jobs\\/.*") { req ->
    req.response.sendFile(req.path.substring(1))
}

DefaultHttpServer server = vertx.createHttpServer()
server.requestHandler(routeMatcher.asClosure())

vertx.createSockJSServer(server).bridge(prefix: '/eventbus', [[:]], [[:]])

server.listen(8080)
container.logger.info("Jobs webserver running!")
