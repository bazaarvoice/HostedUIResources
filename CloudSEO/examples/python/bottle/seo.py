#!/usr/bin/python

# required modules
import os, re, datetime, urllib2
from bottle import route, request, response, run

# Make sure bv is in your $PYTHONPATH
# In this case, I've symlinked bv.py from ../bvseosdk/bv.py
import bv

# define route and handler
@route('/')
def index():

    bvSeo = bv.BV(
        display_code='12325',
        product_id='product1',
        page_url=request.url,
        seo_key='agileville-78B2EF7DE83644CAB5F8C72F2D8C8491',
        bv_product='reviews',
        current_request_query_string=request.copy().query.dict,
        user_agent='msnbot'
    )

    return bvSeo.getSeoWithDisplay()

# initialize Bottle server
# run(host='localhost', port=8080, reloader=True)
