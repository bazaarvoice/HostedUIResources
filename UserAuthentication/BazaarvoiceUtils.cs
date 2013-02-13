/*
Copyright 2006-2013 Bazaarvoice, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License.
*/

using System;
using System.Security.Cryptography;
using System.Text;
using System.Web;

namespace com.bazaarvoice.prr.util
{
    /// <summary>
    /// Utility functions for Bazaarvoice integration.
    /// </summary>
    public class BazaarvoiceUtils
    {
        /// Returns a String of hex characters that represents the encoded version of
        /// the specified user ID and uses the default values for other user attributes.
        public static string EncodeUserID(string userID, string sharedKey)
        {
            if (userID == null)
            {
                return null;
            }
            return CreateUserToken(userID, sharedKey).ToString();
        }

        /// Returns a UserToken with a ToString value that is a signed, hex-encoded
        /// version of the specified user ID.
        public static UserToken CreateUserToken(string userID, string sharedKey)
        {
            if (userID == null)
            {
                return null;
            }
            return new UserToken(userID, sharedKey);
        }

        public static string SignAndEncode(string value, string sharedKey)
        {
            MD5CryptoServiceProvider md5 = new MD5CryptoServiceProvider();
            UTF8Encoding utf8 = new UTF8Encoding();
            string signature = BytesToHexString(md5.ComputeHash(utf8.GetBytes(sharedKey + value)));
            string hexString = BytesToHexString(utf8.GetBytes(value));
            return signature + hexString;
        }

        private static string BytesToHexString(byte[] input)
        {
            StringBuilder sb = new StringBuilder(64);
            for (int i = 0; i < input.Length; i++)
            {
                sb.Append(String.Format("{0:X2}", input[i]));
            }
            return sb.ToString();
        }
    }

    /// A date-stamped stamped set of user attributes, including the user ID.
    public class UserToken
    {
        private string sharedKey;
        private StringBuilder buffer;

        /// Constructs a UserToken with a specified user ID and the key to use
        ///  to sign the user attributes.
        public UserToken(string userID, string sharedKey)
        {
            this.sharedKey = sharedKey;
            this.buffer = new StringBuilder();
            DateTime today = DateTime.Now;
            Add("date", today.Year + "-" + today.Month + "-" + today.Day);
            Add("userid", userID);
        }

        /// Sets the user's nickname in their Bazaarvoice profile.  This nickname
        /// is required if the Bazaarvoice application is configured to
        /// accept profile nicknames in the user authentication string instead
        /// of collecting nicknames in the submission form, and ignored otherwise.
        public UserToken AddProfileName(string userProfileName)
        {
            return Add("username", userProfileName);
        }

        /// Sets the default value of the user's e-mail address in the
        /// Bazaarvoice submission form.  This e-mail address will be ignored
        /// unless the Bazaarvoice application is configured to collect e-mail
        /// addresses in the content submission form.
        public UserToken AddEmailAddress(string userEmailAddress)
        {
            return Add("emailaddress", userEmailAddress);
        }

        /// Sets or clears the "Staff" badge for the user in their Bazaarvoice
        /// profile.  If this is not specified, the user's "Staff" badge is
        /// left unchanged and can be set via the Bazaarvoice Workbench UI.
        /// This value will be ignored unless the Bazaarvoice application is
        /// configured to accept badge values in the user authentication string.
        public UserToken AddStaffAffiliation(bool userIsStaff)
        {
            return Add("affiliation", userIsStaff ? "staff" : "none");
        }

        /// Sets the number of days in the future that the signed user
        /// authentication string expires.  After that date, the signed string
        /// will be considered invalid.
        ///
        /// By default, user authentication strings expire after one day.
        /// This method can be used to create authentication strings that last
        /// longer, which can be useful when creating pre-authenticated URLs in
        /// e-mails, for example.  The maximum lifespan of a user authentication
        /// string is 365 days unless the Bazaarvoice application is explicitly
        /// configured otherwise.
        public UserToken AddExpiresAfterNDays(int numberOfDays)
        {
            // signed user information must be valid for at least 1 day
            if (numberOfDays < 1)
            {
                numberOfDays = 1;
            }
            return Add("maxage", Convert.ToString(numberOfDays));
        }

        /// Adds a URL-style name/value pair to the user authentication string.
        private UserToken Add(string key, string value)
        {
            if (value != null && value.Length > 0)
            {
                if (this.buffer.Length > 0)
                {
                    this.buffer.Append("&");
                }
                this.buffer.Append(key);
                this.buffer.Append("=");
                this.buffer.Append(UrlEncode(value));
            }
            return this;
        }

        private string UrlEncode(string value)
        {
            return HttpUtility.UrlEncode(value, new UTF8Encoding());
        }

        /// Returns a signed, hex-encoded string containing a set of user attributes.
        public string ToString()
        {
            return BazaarvoiceUtils.SignAndEncode(this.buffer.ToString(), this.sharedKey);
        }
    }
}
