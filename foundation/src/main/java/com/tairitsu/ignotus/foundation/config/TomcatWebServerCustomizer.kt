package com.tairitsu.ignotus.foundation.config

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.stereotype.Component
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer as OriginalTomcatConnectorCustomizer

@Component
class TomcatWebServerCustomizer : WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    override fun customize(factory: TomcatServletWebServerFactory) {
        factory.addConnectorCustomizers(OriginalTomcatConnectorCustomizer { connector ->
            connector.setProperty("relaxedQueryChars", "[]")
        })
    }
}
