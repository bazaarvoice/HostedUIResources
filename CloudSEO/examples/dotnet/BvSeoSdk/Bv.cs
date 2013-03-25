using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Text.RegularExpressions;
using System.Web;

namespace BvSeoSdk
{
    public class Bv
    {
        private string _displayCode;
        private string _productId;
        private string _pageUrl;
        private string _seoKey;
        private string _bvProduct;
        private string _userAgent;
        private bool _staging;
        private bool _hostedDisplay;
        private bool _botDetection;
        private int _timeoutMs;
        private string _botRegexString;
        private string _productOrCategory;

        public Bv(String display_code,
            String product_id,
            String seo_key,
            String bv_product,
            bool staging = true,
            bool hosted_display = false,
            int timeout_ms = 1000,
            String bot_regex_string= "(msnbot|googlebot|teoma|bingbot|yandexbot|yahoo)",
            bool bot_detection = true,
            String user_agent = "",
            String page_url = "",
            String product_or_category="product"
            )
        {
            _botRegexString = bot_regex_string;
            _timeoutMs = timeout_ms;
            _hostedDisplay = hosted_display;
            _staging = staging;
            _userAgent = user_agent;
            _bvProduct = bv_product;
            _seoKey = seo_key;
            _pageUrl = page_url;
            _productId = product_id;
            _displayCode = display_code;
            _productOrCategory = product_or_category;
            _botRegexString = bot_regex_string;
            _botDetection = bot_detection;
        }

        public string ProductOrCategory
        {
            get { return _productOrCategory; }
            set { _productOrCategory = value; }
        }

        public String getSeoWithDisplay(HttpRequest request)
        {
            return string.Format("{0}{1}", getSeoWithSdk(request), injectJs());
        }

        public String getSeoWithSdk(HttpRequest request)
        {
            int bvpage = 1;
            
            if(!isBot(request))
                return "<!--No bots detected-->";
            
            //determine page #
            bvpage = GetBvpage(request, bvpage);

            //determine endpoint
            String endpoint = (_staging) ? "seo-stg.bazaarvoice.com" : "seo.bazaarvoice.com";

            DateTime startTime = DateTime.Now;
            int timeTakenInMs = 0;
            String response = "";
            try
            {
                response = httpGet(String.Format("http://{0}/{1}/{2}/{3}/{4}/{5}/{6}.htm",
                                                        endpoint, _seoKey, _displayCode, _bvProduct,
                                                        _productOrCategory, bvpage, _productId));
                timeTakenInMs = (DateTime.Now - startTime).Milliseconds;
            }
            catch(Exception ex)
            {
                return "<!--Request errored out-->"; 
            }

            if (String.IsNullOrEmpty(response))
                return "<!--No SEO File-->";

            string queryPrefix, basePage = _pageUrl;
            if (String.IsNullOrEmpty(basePage))
            {
                //use the current url as the base url
                //Clear bvrrp query parameter from basepage if it already exists
                String bvrrpQuery = request.Url.Query.TrimStart('?').Split('&').FirstOrDefault(x => x.StartsWith("bvrrp"));
                basePage = request.Url.OriginalString;
                if (!String.IsNullOrEmpty(bvrrpQuery))
                {
                    basePage = basePage.Replace("?" + bvrrpQuery, "");
                    basePage = basePage.Replace("&" + bvrrpQuery, "");
                }
            }

            //decide if we should append a ? or & to add a parameter to the url
            queryPrefix = (basePage.Contains("?")) 
                                    ? "&"
                                    : "?";
            

            //replace token in response with correct endpoint
            response = response.Replace("{INSERT_PAGE_URI}", basePage + queryPrefix);

            //add bvtimer code
            response += String.Format("<!--bvtime {0}-->", timeTakenInMs);
            
            return response;

        }

        private bool isBot(HttpRequest request)
        {
            if (String.IsNullOrEmpty(_userAgent))
                _userAgent = request.UserAgent;

            if (_userAgent != null)
                return (new Regex(_botRegexString, RegexOptions.IgnoreCase).Match(_userAgent).Success) && _botDetection;
            return false;
        }

        private int GetBvpage(HttpRequest request, int bvpage)
        {
            if (request.QueryString.AllKeys.Count() > 0)
            {
                if (!String.IsNullOrEmpty(request.QueryString["bvpage"]))
                    bvpage = Int32.Parse(request.QueryString["bvpage"]);
                else if (!String.IsNullOrEmpty(request.QueryString["bvrrp"]))
                {
                    //search the querystring for a number
                    Match m = new Regex(@"\/(\d+?)\/[^\/]+$", RegexOptions.IgnoreCase).Match(request.Url.Query);
                    if (m.Success)
                    {
                        try
                        {
                            bvpage = Int32.Parse(m.Groups[0].Captures[0].Value.Split('/')[1]);
                        }
                        catch(Exception ex)
                        {
                            Console.WriteLine("error occurred in extracting the page number. Exception: " + ex.Message);
                        }
                    }
                    
                }

            }
            return bvpage;
        }

        private String httpGet(String url)
        {
            WebRequest wrGetUrl = WebRequest.Create(url);
            wrGetUrl.Timeout = 1000; //Timeout at 1 second

            Stream objStream = wrGetUrl.GetResponse().GetResponseStream();
            StreamReader objReader = new StreamReader(objStream);

            String result = "";
            String line = objReader.ReadLine();
            while (line != null)
            {
                result += line;
                line = objReader.ReadLine();
            }
            return result;
        } 

        private String injectJs()
        {
            return
                string.Format(@"<script type=""text/javascript"">$BV.ui(""rr"",""show_reviews"", {{productId: ""{0}""}});</script>", _productId); 
        } 

    }
}
