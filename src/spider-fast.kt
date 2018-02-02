package burp


class BurpExtender : IBurpExtender, IHttpListener {
    companion object {
        lateinit var cb: IBurpExtenderCallbacks
        val alwaysSpiderExtensions = setOf("htm", "html", "js", "css", "swf")
        val spiderMimeTypes = setOf("text/html", "text/plain", "application/javascript", "application/json", "application/x-shockwave-flash")
    }

    override fun registerExtenderCallbacks(callbacks: IBurpExtenderCallbacks) {
        cb = callbacks
        callbacks.registerHttpListener(this)
        callbacks.setExtensionName("Spider Fast")
    }

    override fun processHttpMessage(tool: Int, isRequest: Boolean, messageInfo: IHttpRequestResponse) {
        if(tool != IBurpExtenderCallbacks.TOOL_SPIDER) {
            return
        }

        if(isRequest) {
            val requestInfo = cb.helpers.analyzeRequest(messageInfo.httpService, messageInfo.request)
            val extension = requestInfo.url.path.substringAfterLast(".", "")
            if (Companion.alwaysSpiderExtensions.contains(extension)) {
                return
            }

            val headers = requestInfo.headers
            headers[0] = headers[0].replace("GET", "HEAD")
            messageInfo.request = cb.helpers.buildHttpMessage(headers, messageInfo.request.copyOfRange(requestInfo.bodyOffset, messageInfo.request.size))
        }

        else {
            val requestInfo = cb.helpers.analyzeRequest(messageInfo.request)
            if(!requestInfo.headers[0].startsWith("HEAD")) {
                return
            }

            val responseInfo = cb.helpers.analyzeResponse(messageInfo.response)
            var contentType: String? = null
            for(header in responseInfo.headers) {
                if(header.startsWith("Content-Type: ")) {
                    contentType = header.substringAfter("Content-Type: ").substringBefore(";")
                    break
                }
            }
            if(contentType == null || spiderMimeTypes.contains(contentType)) {
                val headers = requestInfo.headers
                headers[0] = headers[0].replace("HEAD", "GET")
                val newRequest = cb.helpers.buildHttpMessage(headers, messageInfo.request.copyOfRange(requestInfo.bodyOffset, messageInfo.request.size))
                messageInfo.response = cb.makeHttpRequest(messageInfo.httpService, newRequest).response
            }
        }
    }

}
