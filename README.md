#Spider Fast

This Burp extension increased the speed of Spider in some circumstances. Spider can be particularly slow when there are
large downloads on a site; perhaps PDF or EXE files. Downloading those files does not help Spider find additional links.
Spider Fast restricts Spider to downloading the following types:

| Extension | MIME type                     |
|-----------|-------------------------------|
| htm, html | text/html                     |
| txt       | text/plain                    |
| js        | application/javascript        |
|           | application/json              |
| swf       | application/x-shockwave-flash |

If a Spider request does not have a known extension, it is converted to a HEAD request. If the response has a known MIME
type, the GET request is then issued.