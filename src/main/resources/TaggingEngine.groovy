import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpResponseStatus

def eventBus = vertx.eventBus
def log = container.logger

eventBus.registerHandler("TaggingEngine.query.exec") { message ->
    log.info(message.body)
    def queryExec = new JsonSlurper().parseText(message.body)
    def result = []
    10.times {
        result << [token:"foo-${it}", deviceType:'ios']
    }
    def json = new JsonBuilder()
    json {
        statusCode HttpResponseStatus.OK.code()
        statusMessage 'OK'
        content ( result )
    }
    message.reply(json.toString())
}
