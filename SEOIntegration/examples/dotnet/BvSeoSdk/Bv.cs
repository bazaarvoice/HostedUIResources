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
        private string _deploymentZoneId;
        private string _productId;
        private string _pageUrl;
        private string _seoKey;
        private string _bvProduct;
        private string _userAgent;
        private bool _staging;
        private bool _hostedDisplay;
        private bool _botDetection;
        private bool _includeDisplayIntegrationCode;
        private int _timeoutMs;
        private string _botRegexString;
        private string _internalFilePath;
        private string _productOrCategory;
        private string _commentStub = "<!--BVSEO|dz:{0}|sdk:v1.0-n|msg:{1} -->";

        public Bv(String deploymentZoneID,
            String product_id,
            String cloudKey,
            String bv_product,
            bool staging = true,
            bool hosted_display = false,
            int timeout_ms = 1000,
            String bot_regex_string= "(msnbot|googlebot|teoma|bingbot|yandexbot|yahoo)",
            bool bot_detection = true,
            bool includeDisplayIntegrationCode=false,
            String internalFilePath="",
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
            _seoKey = cloudKey;
            _pageUrl = page_url;
            _productId = product_id;
            _deploymentZoneId = deploymentZoneID;
            _productOrCategory = product_or_category;
            _botRegexString = bot_regex_string;
            _botDetection = bot_detection;
            _includeDisplayIntegrationCode = includeDisplayIntegrationCode;
            _internalFilePath = internalFilePath;
        }

        private String getBvComment(String message)
        {
            return String.Format(_commentStub, _deploymentZoneId, message);
        }

        public string ProductOrCategory
        {
            get { return _productOrCategory; }
            set { _productOrCategory = value; }
        }

        private String getSeoWithDisplay(HttpRequest request)
        {
            return string.Format("{0}{1}", getSeoResults(request), injectJs());
        }

        public String getSeoWithSdk(HttpRequest request)
        {
            return _includeDisplayIntegrationCode ? getSeoWithDisplay(request) : getSeoResults(request);
        }

        private String getSeoResults(HttpRequest request)
        {
            int bvpage = 1;
            
            if(!isBot(request))
                return getBvComment("JavaScript-only Display");
            
            //determine page #
            bvpage = GetBvpage(request, bvpage);

            //determine endpoint
            String endpoint = (_staging) ? "seo-stg.bazaarvoice.com" : "seo.bazaarvoice.com";

            DateTime startTime = DateTime.Now;
            int timeTakenInMs = 0;
            String response = "";
            try
            {
                if (!String.IsNullOrEmpty(_internalFilePath))
                    response = getFile(Path.Combine(_internalFilePath, String.Format("{0}\\{1}\\{2}\\{3}\\{4}.htm",
                                                                                     _deploymentZoneId, _bvProduct,
                                                                                     _productOrCategory, bvpage,
                                                                                     _productId)));
                else
                    response = httpGet(String.Format("http://{0}/{1}/{2}/{3}/{4}/{5}/{6}.htm",
                                                     endpoint, _seoKey, _deploymentZoneId, _bvProduct,
                                                     _productOrCategory, bvpage, _productId));
                timeTakenInMs = (DateTime.Now - startTime).Milliseconds;
            }
            catch(Exception ex)
            {
                return  getBvComment("Error: Request errored out - " + ex.Message); 
            }

            if (String.IsNullOrEmpty(response))
                return getBvComment("WARNING: No SEO File");

            string basePage = _pageUrl;
            if (String.IsNullOrEmpty(basePage))
            {
                //use the current url as the base url
                basePage = request.Url.OriginalString;
            }
           
            //Clear bvrrp,bvqap, or bvsyp query parameter from basepage if it already exists
            if (basePage.Split('?').Length > 1)
            { 
                basePage = (basePage.Split('?')[0] + "?" + String.Join("&", basePage.Split('?')[1].Split('&').ToList().FindAll(
                    x => !x.StartsWith("bvrrp=") && !x.StartsWith("bvqap=") && !x.StartsWith("bvsyp=")).ToArray())).TrimEnd('?');
              
            }
            
            //decide if we should append a ? or & to add a parameter to the url
            string queryPrefix = (basePage.Contains("?")) 
                                     ? "&"
                                     : "?";
            

            //replace token in response with correct endpoint
            response = response.Replace("{INSERT_PAGE_URI}", basePage + queryPrefix);

            //add bvtimer code
            if (!isBvRevealADebug(request))
                response += getBvComment(String.Format("timer {0}ms", timeTakenInMs));
            else
            {
                response += getBvComment(String.Format
                    ("timer {0}ms, parameters: bvProduct: {1}, Deployment Zone: {2}, BotDetection: {3}, BotRegex: {4}, includeDisplayIntegration: {5}, internalFilePath: {6}, pageUrl: {7}, productId: {8}, ProductType: {9}, SEOKey: {10}, staging: {11}, userAgent: {12}",
                    timeTakenInMs, _bvProduct, _deploymentZoneId, _botDetection, _botRegexString,
                    _includeDisplayIntegrationCode, _internalFilePath, _pageUrl, _productId, _productOrCategory, _seoKey, _staging, _userAgent));
            }
            
            return response;

        }

        private string getFile(string fileName)
        {
           return File.ReadAllText(fileName);
        }

        private bool isBot(HttpRequest request)
        {
            if (String.IsNullOrEmpty(_userAgent))
                _userAgent = request.UserAgent;

            if (!_botDetection) //bot detection is disabled - assume this is a bot
                return true;

            if (isBvRevealABot(request) || isBvRevealADebug(request)) //assume this is a bot  
                return true;

            if (_userAgent != null)
                return (new Regex(_botRegexString, RegexOptions.IgnoreCase).Match(_userAgent).Success);

            return true;
        }

        private bool isBvRevealABot(HttpRequest request)
        {
            if (request.QueryString.AllKeys.Contains("bvreveal"))
            {
                return "bot".Equals(request.QueryString["bvreveal"]);
            }
            return false;
        }

        private bool isBvRevealADebug(HttpRequest request)
        {
            if (request.QueryString.AllKeys.Contains("bvreveal"))
            {
                return "debug".Equals(request.QueryString["bvreveal"]);
            }
            return false;
        }

        private int GetBvpage(HttpRequest request, int bvpage)
        {
            if (request.QueryString.AllKeys.Count() > 0)
            {
                if (!String.IsNullOrEmpty(request.QueryString["bvpage"]))
                    bvpage = Int32.Parse(request.QueryString["bvpage"]);
                else if (!String.IsNullOrEmpty(request.QueryString["bvrrp"]) ||
                        !String.IsNullOrEmpty(request.QueryString["bvqap"]) ||
                        !String.IsNullOrEmpty(request.QueryString["bvsyp"]))
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
            if (_bvProduct == BvProduct.QUESTIONS)
                return
                string.Format(@"<script type=""text/javascript"">$BV.ui(""qa"",""show_questions"", {{productId: ""{0}""}});</script>", _productId); 
            return
                string.Format(@"<script type=""text/javascript"">$BV.ui(""rr"",""show_reviews"", {{productId: ""{0}""}});</script>", _productId); 
        } 

    }
}
