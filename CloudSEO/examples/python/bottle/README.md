Python BV SEO SDK using Bottle
========

BV SEO SDK for Python implemented using the [Bottle](http://bottlepy.org/) web framework, a simple, tiny WSGI-compliant web framework.

How to use:
-----------

There are two ways to run this SDK. One way is to execute seo.py as a standalone web service. This can be done by uncommenting line 142:

    run(host='localhost', port=8080, reloader=True)

Alternatively, you can initialize Bottle as a web server and import the BV SEO SDK module:

    python -m bottle -b localhost --debug --reload seo

**Note**: *seo* corresponds to the module name, in this case *seo.py*. You must run this command in the same directory as *seo.py* in order for Bottle to pickup the module.

Please note that both of the examples use debugging / auto-reloading and are intended for prototyping, *not* production.

Once Bottle is running, you can access the example on your localhost on port 8080:

    open http://localhost:8080/seo

*Note*: because Bottle uses request routing, you do not need the .py extension. Consult the Bottle documentation for more information.
