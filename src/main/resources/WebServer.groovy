import groovy.json.JsonBuilder
import org.vertx.groovy.core.buffer.Buffer
import org.vertx.groovy.core.http.RouteMatcher
import org.vertx.groovy.core.http.impl.DefaultHttpServer
import org.vertx.groovy.platform.Verticle
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder
import org.vertx.java.core.json.JsonObject
import org.vertx.java.core.json.JsonArray
import org.vertx.groovy.core.AsyncResult

RouteMatcher routeMatcher = new RouteMatcher()
def eventBus = vertx.eventBus
def log = container.logger

routeMatcher.get('/audit') { request ->
    def limit = request.params.pageSize as int
    def offset = ((request.params.page as int) - 1) * limit
    def json = [
            'action':'prepared',
            'statement':'SELECT * FROM push_audit LIMIT ? OFFSET ?',
            'values':[limit, offset]
    ]
    def jsonMsg = new JsonObject(json)
    eventBus.send('postgresql', jsonMsg) { message ->
        if(message.body.status == 'ok') {
            request.response.statusCode = 200
        } else {
            request.response.statusCode = 500
        }
        def audits = []
        message.body.results.each { row ->
            audits << [id:row[0], jobId:row[1], action:row[2], username:row[3], status:row[4], created:row[5],
                    request:row[6], response:row[7]]
        }
        request.response.putHeader('Content-Type', 'application/json; charset=utf-8')
        request.response.statusMessage = message.body.status
        request.response.putHeader('Content-Type', 'application/json; charset=utf-8')
        request.response.end(new JsonArray(audits, false).toString())
    }
}

routeMatcher.get('/audit/:id') { request ->
    def id = request.params.id as long
    def json = [
            'action':'prepared',
            'statement':'SELECT * FROM push_audit WHERE id = ?',
            'values':[id]
    ]
    def jsonMsg = new JsonObject(json)
    eventBus.send('postgresql', jsonMsg) { message ->
        if(message.body.status == 'ok') {
            request.response.statusCode = 200
        } else {
            request.response.statusCode = 500
        }
        def audits = []
        message.body.results.each { row ->
            audits << [id:row[0], jobId:row[1], action:row[2], username:row[3], status:row[4], created:row[5],
                    request:row[6], response:row[7]]
        }
        request.response.putHeader('Content-Type', 'application/json; charset=utf-8')
        request.response.statusMessage = message.body.status
        request.response.putHeader('Content-Type', 'application/json; charset=utf-8')
        request.response.end(new JsonArray(audits, false).toString())
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
container.logger.info("webserver running!")
