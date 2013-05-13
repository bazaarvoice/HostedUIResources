#!/usr/bin/env python

from flask import Flask, request

# Make sure bv is in your $PYTHONPATH
# In this case, I've symlinked bv.py from ../bvseosdk/bv.py
import bv

app = Flask(__name__)

@app.route("/")
def index():

    bvSeo = bv.BV(
        display_code='12325',
        product_id='product1',
        page_url=request.url,
        seo_key='agileville-78B2EF7DE83644CAB5F8C72F2D8C8491',
        bv_product='reviews',
        current_request_query_string=request.args,
        user_agent='msnbot'
    )

    return bvSeo.getSeoWithDisplay()

if __name__ == "__main__":
    # app.debug = True
    app.run()