Python BV SEO SDK using a simple CGI server
========

BV SEO SDK for Python implemented using [CGIHTTPServer](http://docs.python.org/2/library/cgihttpserver.html), a CGI-enabled version of [SimpleHTTPServer](http://docs.python.org/2/library/simplehttpserver.html#module-SimpleHTTPServer).

This example is very limited and to provide a general sense of how the BV SEO SDK may be implemented. It is **not** intended for use in a production environment, since it includes its own small CGI server, found in *http.py*.

How to use:
-----------

Start the simple CGI server:

    python http.py

This starts the simple CGI server listening on localhost, port 8000. Once it's running, access the BV SEO example:

    open http://localhost:8000/seo.py