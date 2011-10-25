package com.bazaarvoice.prr.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
Copyright 2001-2004 The Apache Software Foundation.
Copyright 2006-2007 Bazaarvoice, Inc.

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

public class BazaarvoiceUtils {

    /**
     * Returns a String of hex characters that represents the encoded version
     * of the specified user ID and uses the default values for other user
     * attributes.
     *
     * @param userID the user ID to be encoded
     * @param sharedKey the shared encoding key
     * @return a String of hex characters that represents the encoded version of the specified user ID
     */
    public static String encodeUserID(String userID, String sharedKey) {
        if (userID == null) {
            return null;
        }
        return createUserToken(userID, sharedKey).toString();
    }

    /**
     * Returns a UserToken with a toString value that is a signed, hex-encoded
     * version of the specified user ID.
     * <p>
     * The most common usage of this function during login will look something
     * like this (replace "ABCDE" with the shared key from Bazaarvoice):
     * <pre>
     *   bvpageUrl = bvpageUrl.replaceAll("__USERID__", BazaarvoiceUtils.createUserToken(userID, "ABCDE").toString())
     * </pre>
     * Or, on a product page if the user has been authenticated already (assuming JSP):
     * <pre>
     *    <div id="BVCustomerID" style="display:none"><%=BazaarvoiceUtils.createUserToken(userID, "ABCDE")%></div>
     * </pre>
     *
     * @param userID the user ID to be encoded
     * @param sharedKey the encoding key shared with Bazaarvoice
     * @return a UserToken with a toString value that is a signed, hex-encoded
     *         version of the specified user ID.
     */
    public static UserToken createUserToken(String userID, String sharedKey) {
        if (userID == null) {
            return null;
        }
        return new UserToken(userID, sharedKey);
    }

    /**
     * A date-stamped stamped set of user attributes, including the user ID.
     */
    public static class UserToken {
        private final String sharedKey;
        private final StringBuffer buffer;

        /**
         * Constructs a UserToken with a specified user ID and the key to use
         *  to sign the user attributes.
         * @param userID the required user ID
         * @param sharedKey the encoding key shared with Bazaarvoice
         */
        public UserToken(String userID, String sharedKey) {
            this.sharedKey = sharedKey;
            this.buffer = new StringBuffer();
            Date today = new Date();
            add("date", new SimpleDateFormat("yyyyMMdd").format(today));
            add("userid", userID);
        }

        /**
         * Sets the user's nickname in their Bazaarvoice profile.  This nickname
         * is <em>required</em> if the Bazaarvoice application is configured to
         * accept profile nicknames in the user authentication string instead
         * of collecting nicknames in the submission form, and ignored otherwise.
         * @param userProfileName the user's nickname in their Bazaarvoice profile.
         * @return this UserToken, for method chaining.
         */
        public UserToken addProfileName(String userProfileName) {
            return add("username", userProfileName);
        }

        /**
         * Sets the default value of the user's e-mail address in the
         * Bazaarvoice submission form.  This e-mail address will be ignored
         * unless the Bazaarvoice application is configured to collect e-mail
         * addresses in the content submission form.
         * @param userEmailAddress the default value of the user's e-mail
         *        address in the Bazaarvoice submission form.
         * @return this UserToken, for method chaining.
         */
        public UserToken addEmailAddress(String userEmailAddress) {
            return add("emailaddress", userEmailAddress);
        }

        /**
         * Sets or clears the "Staff" badge for the user in their Bazaarvoice
         * profile.  If this is not specified, the user's "Staff" badge is
         * left unchanged and can be set via the Bazaarvoice Workbench UI.
         * This value will be ignored unless the Bazaarvoice application is
         * configured to accept badge values in the user authentication string.
         * @param userIsStaff true if the user should have the "Staff" badge.
         * @return this UserToken, for method chaining.
         */
        public UserToken addStaffAffiliation(boolean userIsStaff) {
            return add("affiliation", userIsStaff ? "staff" : "none");
        }

        /**
         * Sets the number of days in the future that the signed user
         * authentication string expires.  After that date, the signed string
         * will be considered invalid.
         * <p>
         * By default, user authentication strings expire after one day.
         * This method can be used to create authentication strings that last
         * longer, which can be useful when creating pre-authenticated URLs in
         * e-mails, for example.  The maximum lifespan of a user authentication
         * string is 365 days unless the Bazaarvoice application is explicitly
         * configured otherwise.
         * @param numberOfDays the number of days in the future that the signed
         *        user authentication string expires.
         * @return this UserToken, for method chaining.
         */
        public UserToken addExpiresAfterNDays(int numberOfDays) {
            // signed user information must be valid for at least 1 day
            if (numberOfDays < 1) {
                numberOfDays = 1;
            }
            return add("maxage", Integer.toString(numberOfDays));
        }

        /**
         * Sets custom values onto the UserToken object that will be signed
         * as part of the encoding process.
         * @param name the name of the attribute to be added.
         * @param value the value of the attribute to be added.
         * @return this UserToken, for method chaining.
         */
        public UserToken addCustomValue(String name, String value) {
            return add(name, value);
        }

        /**
         * Adds a URL-style name/value pair to the user authentication string.
         */
        private UserToken add(String key, String value) {
            if (value != null && value.length() > 0) {
                if (this.buffer.length() > 0) {
                    this.buffer.append('&');
                }
                this.buffer.append(key).append('=').append(urlEncode(value));
            }
            return this;
        }

        /**
         * Returns a signed, hex-encoded string containing a set of user attributes.
         * @return a signed, hex-encoded string containing a set of user attributes.
         */
        public String toString() {
            return signAndEncode(this.buffer.toString(), this.sharedKey);
        }
    }

    /**
     * Returns a String of hex characters contining an encoded version of
     * the specified string combined with a cryptographically secure signature
     * of the string.  The signature is extremely difficult to forge without
     * knowing the value of the sharedKey, so a correctly signed and encoded
     * string can be trusted as having come from someone who knows the shared key.
     *
     * @param string the string to be encoded
     * @param sharedKey the encoding key shared with Bazaarvoice
     * @return a String of hex characters that represents the encoded version of the specified string
     */
    public static String signAndEncode(String string, String sharedKey) {
        String signature = encodeHex(md5(sharedKey + string));
        String hexString = encodeHex(string.getBytes("UTF-8"));
        return signature + hexString;
    }

    /**
     * Returns a MessageDigest for the given <code>algorithm</code>.
     *
     * @param algorithm The MessageDigest algorithm name.
     * @return An MD5 digest instance.
     * @throws RuntimeException when a {@link java.security.NoSuchAlgorithmException} is caught,
     */
    private static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Returns an MD5 MessageDigest.
     *
     * @return An MD5 digest instance.
     * @throws RuntimeException when a {@link java.security.NoSuchAlgorithmException} is caught,
     */
    private static MessageDigest getMd5Digest() {
        return getDigest("MD5");
    }

    /**
     * Calculates the MD5 digest and returns the value as a 16 element
     * <code>byte[]</code>.
     *
     * @param data Data to digest
     * @return MD5 digest
     */
    private static byte[] md5(String data) {
        return getMd5Digest().digest(data.getBytes("UTF-8"));
    }

    /**
     * URL encodes the specified string using the UTF-8 encoding.
     *
     * @param string the string to encode
     * @return the URL encoded string
     * @throws RuntimeException when a {@link java.io.UnsupportedEncodingException} is caught,
     */
    private static String urlEncode(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);  // UTF-8 should always be supported
        }
    }

    /**
     * Used building output as Hex
     */
    private static final char[] DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    /**
     * Converts an array of bytes into an array of characters representing the hexidecimal values of each byte in order.
     * The returned array will be double the length of the passed array, as it takes two characters to represent any
     * given byte.
     *
     * @param data a byte[] to convert to Hex characters
     * @return A String containing hexidecimal characters
     */
    private static String encodeHex(byte[] data) {

        int l = data.length;

        char[] out = new char[l << 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }

        return new String(out);
    }
}
