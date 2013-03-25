import os, re, datetime, urllib2

class BV:

    seo_contents = ''

    def __init__(
            self,
            product_id,
            display_code,
            bv_product,
            page_url,
            seo_key,
            user_agent,
            current_request_query_string = {},
            product_or_category = 'product',
            staging = True,
            hosted_display = False,
            timeout_ms = 1000,
            bot_regex_string = '(msnbot|googlebot|teoma|bingbot|yandexbot|yahoo)',
            cache_enabled = True
        ):

        self.PRODUCT_ID             = product_id
        self.DISPLAY_CODE           = display_code
        self.BV_PRODUCT             = bv_product
        self.PAGE_URL               = page_url
        self.PRODUCT_OR_CATEGORY    = product_or_category
        self.SEO_KEY                = seo_key
        self.STAGING                = staging
        self.HOSTED_DISPLAY         = hosted_display
        self.TIMEOUT_MS             = timeout_ms
        self.USER_AGENT             = user_agent
        self.BOT_REGEX_STRING       = re.compile(bot_regex_string, re.I)
        self.QUERY_STRING           = current_request_query_string
        self.seo_contents           = self.seo(cache_enabled=cache_enabled)

    # define getter method
    def getSeo(self, cache_enabled=True, withDisplay=True):
        if withDisplay:
            return '%s%s' % (self.seo(cache_enabled=cache_enabled), self.inject_js())
        return self.seo(cache_enabled=cache_enabled)

    # convenience function
    def getSeoWithDisplay(self):
        return self.getSeo(withDisplay=True)

    # get page number using query dictionary object
    def getPageNumber(self, query):
        page_number = 1

        try:
            page_number = query['bvpage']
        except:
            try:
                bvrrp = query['bvrrp']
                # some frameworks provide a list of values for the same key
                if isinstance(bvrrp, list):
                    bvrrp = bvrrp[0]
                page_number_search = re.search(r'\/(\d+?)\/[^\/]+$', bvrrp)
                if page_number_search is not None:
                    page_number = page_number_search.group(1)
            except:
                pass

        return page_number

    # where the real work gets done
    def seo(self, cache_enabled=True):

        if self.seo_contents != '' and cache_enabled is True:
            return self.seo_contents

        # Bot or not: only return non-empty string for bots
        if self.BOT_REGEX_STRING.search(self.USER_AGENT):

            # parse query string
            if isinstance(self.QUERY_STRING, dict):
                query = self.QUERY_STRING
            else:
                raise ValueError, "Query string data must be passed as a dictionary object."

            page_number = self.getPageNumber(query)

            # determine request endpoint (staging or production)
            if self.STAGING:
                endpoint_domain = 'seo-stg.bazaarvoice.com'
            else:
                endpoint_domain = 'seo.bazaarvoice.com'

            # prepare request time variable
            request_time = 0;
            # prepare request URL
            request_url = 'http://%s/%s/%s/%s/%s/%s/%s.htm' % (
                endpoint_domain,
                self.SEO_KEY,
                self.DISPLAY_CODE,
                self.BV_PRODUCT,
                self.PRODUCT_OR_CATEGORY,
                page_number,
                self.PRODUCT_ID )

            # prepare to catch exceptions
            try:

                # identify time before initiating request
                connect_start_time = datetime.datetime.now()
                # initiate request
                response_object = urllib2.urlopen(request_url, timeout=(float(self.TIMEOUT_MS) / 1000))
                # read request response
                response_data = response_object.read()
                # identify elapsed time since request was initiated
                request_time = (datetime.datetime.now() - connect_start_time)
                # convert result to milliseconds
                request_time = request_time.microseconds / 1000
                # save response data from request to output variable
                seo_contents = response_data

                # if response data was empty, return error
                if not seo_contents:
                    return '<!--no SEO file-->'

                # decide whether we should append a ? or & to add a parameter to the URL
                # if there is a query string
                if self.QUERY_STRING:
                    # use ampersands
                    page_url_query_prefix = '&'
                else:
                    # otherwise, use question mark
                    page_url_query_prefix = '?'

                # replace token in response with correct endpoint
                seo_contents = seo_contents.replace('{INSERT_PAGE_URI}', self.PAGE_URL + page_url_query_prefix)
                # add bvtimer code
                seo_contents = seo_contents + '<!--bvtimer %s-->' % request_time

            # catch HTTP exceptions, such as 403's, 500's, etc.
            except urllib2.HTTPError, e:
                # update output variable
                # @TODO: possibly make this less verbose for security reasons
                return '<!--no SEO file; server returned %s-->' % str(e)
            except urllib2.URLError, e:
                # did we not get a response within the time window?
                if '[errno 36]' in str(e.reason).lower():
                    return '<!--no SEO file; request timeout-->'
                # did we establish a connection within the time window?
                elif 'timed out' in str(e.reason).lower():
                    return '<!--no SEO file; connection timeout-->'
                else:
                    # go to next Exception block
                    raise
            except Exception, e:
                # return canned response for errors
                return '<!--no SEO file-->'

        return seo_contents

    # method for JavaScript display
    def inject_js(self):
        return """
<script type="text/javascript">
$BV.ui("rr", "show_reviews", {
    productId: "%s"
});
</script>
""" % self.PRODUCT_ID