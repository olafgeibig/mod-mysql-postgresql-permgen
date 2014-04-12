import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpResponseStatus

def eventBus = vertx.eventBus
def log = container.logger

eventBus.registerHandler("TaggingEngine.query.exec") { message ->
    log.info(message.body)
    def queryExec = new JsonSlurper().parseText(message.body)
    def rows = []
    10.times {
        rows << [id:1207560631, userId:1207560631, pushToken:"33cb2b2f351d2d699a9aece996e38aec8041bf00d9ccfaae92cb75ba0a373f65",
                deviceUserAgent:'tvsmiles-app: android v1.1.7-61qq', createdAt:new java.util.Date()]
    }
    def result =[count:123, rows:rows]
    def json = new JsonBuilder()
    json {
        statusCode HttpResponseStatus.OK.code()
        statusMessage 'OK'
        content ( result )
    }
    message.reply(json.toString())
}
