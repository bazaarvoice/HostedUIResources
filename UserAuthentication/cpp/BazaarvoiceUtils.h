// BazaarvoiceUtils.h: interface for the BazaarvoiceUtils class.
//
// Class is used to get a String of hex characters that represents
// the encoded version of the specified user ID.
//
// Copyright 2013 Bazaarvoice, Inc. 
//
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
//
//	http://www.apache.org/licenses/LICENSE-2.0 
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
// implied. See the License for the specific language governing
// permissions and limitations under the License.
//
//////////////////////////////////////////////////////////////////////


class BVUserToken;

class BazaarvoiceUtils
{
public:
    static char* EncodeUserID(const char* userID, const char* sharedKey);

    static BVUserToken* CreateUserToken(const char* userID, const char* sharedKey);

private:
    static char* SignAndEncode(const char* string, const char* sharedKey);
    static char* EncodeWithMD5(const unsigned char* string);
    static char* EncodeHex(const char* string);
    static char* UrlEncode(const char* string);

    friend class BVUserToken;
};

class BVUserToken
{
public:
    // Constructs a UserToken with a specified user ID and the key to use
    //  to sign the user attributes.
    BVUserToken(const char* userID, const char* sharedKey);

    ~BVUserToken();

    // Sets the user's nickname in their Bazaarvoice profile.  This nickname
    // is required if the Bazaarvoice application is configured to
    // accept profile nicknames in the user authentication string instead
    // of collecting nicknames in the submission form, and ignored otherwise.
    void AddProfileName(const char* userProfileName);

    // Sets the default value of the user's e-mail address in the
    // Bazaarvoice submission form.  This e-mail address will be ignored
    // unless the Bazaarvoice application is configured to collect e-mail
    // addresses in the content submission form.
    void AddEmailAddress(const char* userEmailAddress);

    // Sets or clears the "Staff" badge for the user in their Bazaarvoice
    // profile.  If this is not specified, the user's "Staff" badge is
    // left unchanged and can be set via the Bazaarvoice Workbench UI.
    // This value will be ignored unless the Bazaarvoice application is
    // configured to accept badge values in the user authentication string.
    void AddStaffAffiliation(bool userIsStaff);

    // Sets the number of days in the future that the signed user
    // authentication string expires.  After that date, the signed string
    // will be considered invalid.
    //
    // By default, user authentication strings expire after one day.
    // This method can be used to create authentication strings that last
    // longer, which can be useful when creating pre-authenticated URLs in
    // e-mails, for example.  The maximum lifespan of a user authentication
    // string is 365 days unless the Bazaarvoice application is explicitly
    // configured otherwise.
    void AddExpiresAfterNDays(int numberOfDays);

    // Returns a signed, hex-encoded string containing a set of user attributes.
    // It is the caller's responsibility to delete [] the result.
    char* ToString();

private:
    // Adds a URL-style name/value pair to the user authentication string.
    void Add(const char* key, const char* value);

    const char* _sharedKey;
    char* _buffer;
    int _bufferAllocated;
};