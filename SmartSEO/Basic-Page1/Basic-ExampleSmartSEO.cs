
using System;
using System.IO;
using System.Text;
using System.Text.RegularExpressions;
using System.Web;
using System.Web.UI;

namespace ExampleSmartSEOCode
{


	public partial class Default : System.Web.UI.Page
	{

		private void Page_Load(Object sender, EventArgs args) {
			Response.ContentType = "text/html";
			Response.Write("<html><head><title>sample SmartSEO page</title></head><body>");
			
			Response.Write(getBazaarvoiceInjectionContainer("1234-en_us", "123456", @"C:\path\to\smartseo\"));
			
			Response.Write("</body></html>");
				
		}
		
		/// <summary>
		/// Retrieve the Bazaarvoice container div and all indexable Smart SEO content to be rendered on the page.
		/// </summary>
		/// <param name="displayCode">
		/// A <see cref="String"/> - The display code for which Smart SEO content is generated.  This should be the same display 
		/// code that is the root directory of the Smart SEO archive retrieved from the Bazaarvoice server.
		/// </param>
		/// <param name="productId">
		/// A <see cref="String"/> - The ID of the product for which this product details page is being rendered.
		/// </param>
		/// <param name="smartSEOPathPrefix">
		/// A <see cref="String"/> - The absolute path to the location where your Smart SEO archive has been extracted.
		/// This absolute path should not contain your display code.
		/// </param>
		/// <returns>
		/// A <see cref="String"/> - The Bazaarvoice container div and all indexable Smart SEO content to be rendered on the page.
		/// </returns>
		private String getBazaarvoiceInjectionContainer(String displayCode, String productId, String smartSEOPathPrefix) {
			StringBuilder cntnrBuf = new StringBuilder();
			cntnrBuf.Append("<div id=\"BVRRContainer\">" + Environment.NewLine);
			
			try {
				
				//Make sure the smartSEOPathPrefix ends with a trailing slash
				if (!smartSEOPathPrefix.EndsWith("\\")) {
					smartSEOPathPrefix += "\\";
				}
				
				//Get the name of the file from disk that we should use to include in the page.
				String rrSmartSEOFilename = smartSEOPathPrefix + displayCode + "/reviews/product/1/" + Server.UrlEncode(productId).Replace("+", "%20") + ".htm";
				
				//Verify the file exists
				//Verify no strange ..\ hacking attempts
				if (File.Exists(rrSmartSEOFilename)
				     && Path.GetDirectoryName(Path.GetFullPath(rrSmartSEOFilename)).StartsWith(smartSEOPathPrefix)) {
					//Read in the file contents
					String fileContents = "";
					fileContents = File.ReadAllText(rrSmartSEOFilename, System.Text.UTF8Encoding.UTF8);
					cntnrBuf.Append(fileContents);
				}
				
				
			} catch (Exception ex) {
				//TODO: Log exception
				Console.WriteLine(ex.ToString());
			} finally {
				cntnrBuf.Append("</div>" + Environment.NewLine);
			}
			
			return cntnrBuf.ToString();
		}
		
	}
}
