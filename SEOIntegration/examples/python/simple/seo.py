#!/usr/bin/python

# required modules
import cgi, os, re, datetime, urllib2

# Make sure bv is in your $PYTHONPATH
# In this case, I've symlinked bv.py from ../bvseosdk/bv.py
import bv

# for debugging only!
import cgitb
cgitb.enable()

def index():

    # Convert fieldStorage to standard dict
    query = {}
    for key in cgi.FieldStorage().keys():
        query[key] = cgi.FieldStorage().getfirst(key)

    page_url = 'http://%s:%s%s?%s' % (os.environ['SERVER_NAME'], os.environ['SERVER_PORT'], os.environ['SCRIPT_NAME'], os.environ['QUERY_STRING'])

    return page_url

    bvSeo = bv.BV(
        display_code='12325',
        product_id='product1',
        page_url=page_url,
        seo_key='agileville-78B2EF7DE83644CAB5F8C72F2D8C8491',
        bv_product='reviews',
        current_request_query_string=query,
        user_agent='msnbot' # could be, for example, os.environ['HTTP_USER_AGENT']
    )

    return bvSeo.getSeoWithDisplay()

if __name__ == "__main__":
    print 'Content-Type: text/html\r\n\r\n'
    print index()
