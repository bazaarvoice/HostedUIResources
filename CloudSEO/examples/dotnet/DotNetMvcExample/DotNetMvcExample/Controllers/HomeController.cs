using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using BvSeoSdk;

namespace DotNetMvcExample.Controllers
{
    public class HomeController : Controller
    {
        public String Index()
        {
            return new Bv(
                display_code: "12325",
                product_id: "data-gen-696yl2lg1kurmqxn88fqif5y2", 
                //The page_url is optional
                //page_url: "http://www.example.com/store/products/data-gen-696yl2lg1kurmqxn88fqif5y2/",
                seo_key: "agileville-78B2EF7DE83644CAB5F8C72F2D8C8491", //agileville
                bv_product:  BvProduct.REVIEWS, 
                //bot_detection: false, //by default bot_detection is set to true
                user_agent: "msnbot") //Setting user_agent for testing. Leave this blank in production.
                .getSeoWithSdk(System.Web.HttpContext.Current.Request);
        }

    }
}
