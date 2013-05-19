import os, re, datetime, urllib2

class BV:

    seo_contents    = ''
    SDK_VERSION     = '1.0'
    message_buffer  = ''

    def __init__(
            self,
            product_id,
            deployment_zone_id,
            bv_product,
            page_url,
            cloud_key,
            user_agent,
            current_request_query_string = {},
            product_or_category = 'product',
            staging = True,
            include_display_integration_code = False,
            timeout_ms = 1000,
            internal_file_path = None,
            bot_regex_string = '(msnbot|googlebot|teoma|bingbot|yandexbot|yahoo)',
            bot_detection = True,
            cache_enabled = True
        ):

        self.PRODUCT_ID             = product_id
        self.DEPLOYMENT_ZONE_ID     = deployment_zone_id
        self.BV_PRODUCT             = bv_product
        self.PAGE_URL               = page_url
        self.PRODUCT_OR_CATEGORY    = product_or_category
        self.CLOUD_KEY              = cloud_key
        self.STAGING                = staging
        self.TIMEOUT_MS             = timeout_ms
        self.USER_AGENT             = user_agent
        self.INTERNAL_FILE_PATH     = internal_file_path
        self.BOT_REGEX_STRING       = re.compile(bot_regex_string, re.I)
        self.BOT_DETECT             = bot_detection
        self.QUERY_STRING           = current_request_query_string
        self.seo_contents           = self.seo(cache_enabled=cache_enabled)
        self.INCLUDE_DISPLAY_INTEGRATION_CODE = include_display_integration_code

    # define getter method
    def getSeo(self, cache_enabled=True, withDisplay=True):
        if withDisplay or self.INCLUDE_DISPLAY_INTEGRATION_CODE:
            return '%s%s' % (self.seo(cache_enabled=cache_enabled), self.inject_js())
        return self.seo(cache_enabled=cache_enabled)

    # convenience function
    def getSeoWithDisplay(self):
        return self.getSeo(withDisplay=True)

    # get page number using query dictionary object
    def getPageNumber(self, query):

        page_number = query.get('bvpage', None) or query.get('bvrrp', None) or query.get('bvqap', None) or query.get('bvsyp', None)

        # some frameworks provide a list of values for the same key
        if isinstance(page_number, list):
            page_number = page_number[0]
        elif isinstance(page_number, str):
            page_number_search = re.search(r'\/(\d+?)\/[^\/]+$', page_number)

            if page_number_search is not None:
                page_number = page_number_search.group(1)

        # default to page 1, if we didn't find a number
        if not isinstance(page_number, int):
            page_number = 1

        return page_number

    def msgBuffer(self, message):
        if message:
            self.message_buffer += message

    def getMessagesFromBuffer(self):
        return self.message_buffer

    # where the real work gets done
    def seo(self, cache_enabled=True):

        if self.seo_contents != '' and cache_enabled is True:
            return self.seo_contents

        # parse query string
        if isinstance(self.QUERY_STRING, dict):
            query = self.QUERY_STRING
        else:
            raise ValueError, "Query string data must be passed as a dictionary object."

        page_number = self.getPageNumber(query)
        bv_reveal   = query.get('bvreveal', None)

        # If debugging is enabled, output all the input values, except for the cloud key
        if bv_reveal == 'debug':
            input_parameters = ', '.join('%s: %s' % item for item in vars(self).items()).replace(self.CLOUD_KEY, '')
            self.msgBuffer(self.msg_output(input_parameters))

        # Bot or not: only return non-empty string for bots, unless bvreveal is set or bot detection is disabled
        if (self.BOT_DETECT is False) or (self.BOT_REGEX_STRING.search(self.USER_AGENT)) or (bv_reveal in ('bot', 'debug')):

            # If internal file path is specified, open and output the contents of the file. Then exit.
            if self.INTERNAL_FILE_PATH is not None:
                try:
                    # Assumes file will never be greater than available memory to Python process
                    # @TODO: make this safer
                    f = file(self.INTERNAL_FILE_PATH, 'r')
                    self.msgBuffer(self.msg_output('Internal data read'))
                    self.msgBuffer(f.read())
                    return self.getMessagesFromBuffer()
                except:
                    self.msgBuffer(self.msg_output('Unable to access specified internal file path.'))

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
                self.CLOUD_KEY,
                self.DEPLOYMENT_ZONE_ID,
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
                    self.msgBuffer(self.msg_output('no SEO file'))
                    return self.getMessagesFromBuffer()

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
                self.msgBuffer(self.msg_output('timer {0}'.format(request_time)))

            # catch HTTP exceptions, such as 403's, 500's, etc.
            except urllib2.HTTPError, e:
                # update output variable
                self.msgBuffer(self.msg_output('no SEO file; server returned {0}'.format(str(e))))
                return self.getMessagesFromBuffer()
            except urllib2.URLError, e:
                # did we not get a response within the time window?
                if '[errno 36]' in str(e.reason).lower():
                    self.msgBuffer(self.msg_output('no SEO file; request timeout'))
                    return self.getMessagesFromBuffer()
                # did we establish a connection within the time window?
                elif 'timed out' in str(e.reason).lower():
                    self.msgBuffer(self.msg_output('no SEO file; connection timeout'))
                    return self.getMessagesFromBuffer()
                else:
                    # go to next Exception block
                    raise
            except Exception, e:
                # return canned response for errors
                self.msgBuffer(self.msg_output('no SEO file'))
                return self.getMessagesFromBuffer()

        else:
            self.msgBuffer(self.msg_output('JavaScript-only display'))
            return self.getMessagesFromBuffer()

        if seo_contents:
            self.msgBuffer(seo_contents)

        return self.getMessagesFromBuffer()

    # Formatted messaging output
    def msg_output(self, message):
        return '<!--BVSEO:dz: {0}|sdk:{1}-y|msg: {2}-->'.format(self.DEPLOYMENT_ZONE_ID, self.SDK_VERSION, message)

    # method for JavaScript display
    def inject_js(self):
        return """
<script type="text/javascript">
$BV.ui("rr", "show_reviews", {
    productId: "%s"
});
</script>
""" % self.PRODUCT_ID