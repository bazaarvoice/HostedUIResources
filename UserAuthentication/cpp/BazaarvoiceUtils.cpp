// BazaarvoiceUtils.cpp: implementation of the BazaarvoiceUtils class.
//
// Class is used to get a String of hex characters that represents
// the encoded version of the specified user ID.
//
// Copyright 2006 Bazaarvoice, Inc. 
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

#include "BazaarvoiceUtils.h"
#include "md5.h"
#include "string.h"
#include "time.h"

char* BazaarvoiceUtils::EncodeUserID(const char* userID, const char* sharedKey)
{
    if (userID == 0) {
        return 0;
    }

    BVUserToken* userToken = CreateUserToken(userID, sharedKey);
    char* result = userToken->ToString();
    delete userToken;
    return result;
}

BVUserToken* BazaarvoiceUtils::CreateUserToken(const char* userID, const char* sharedKey)
{
    if (userID == 0) {
        return 0;
    }
    return new BVUserToken(userID, sharedKey);
}

char* BazaarvoiceUtils::SignAndEncode(const char* string, const char* sharedKey)
{
    char* first = new char[strlen(sharedKey) + strlen(string) + 1];
    strcpy(first, sharedKey);
    strcat(first, string);

    char* signature = EncodeWithMD5((unsigned char*)first);
    char* hexString = EncodeHex(string);

    char* result = new char[strlen(signature) + strlen(hexString) + 1];
    strcpy(result, signature);
    strcat(result, hexString);

    delete [] first;
    delete [] hexString;
    delete [] signature;

    return result;
}

char* BazaarvoiceUtils::EncodeWithMD5(const unsigned char* string)
{
    MD5* md5 = new MD5();
    md5->update((unsigned char*)string, strlen((const char*)string));
    md5->finalize();
    char* hex = (char*)md5->hex_digest();
    delete md5;
    return hex;
}

char* BazaarvoiceUtils::EncodeHex(const char* string)
{
    const char* codes = "0123456789abcdef";
    int length = strlen(string);
    char* result = new char[length*2+1];
    for(int i=0; i<length; i++) {
        result[i*2] = codes[(0xF0 & string[i]) >> 4];
        result[i*2+1] = codes[0x0F & string[i]];
    }
    result[length*2]='\0';
    return result;
}

char* BazaarvoiceUtils::UrlEncode(const char* string)
{
    // note: this only works correctly for 7-bit ASCII characters.  if full Unicode
    // is required you must use a different URL encoder that supports the UTF-8 charset.
    const char* codes = "0123456789abcdef";
    char* result = new char[strlen(string)*3 + 1];
    char* dst = result;
    for (const char* src=string; *src; src++) {
        if (isalnum(*src) || *src == '.' || *src == '-') {
            *dst++ = *src;
        } else {
            *dst++ = '%';
            *dst++ = codes[(0xF0 & *src) >> 4];
            *dst++ = codes[0x0F & *src];
        }
    }
    *dst++ = '\0';
    return result;
}

// Constructs a UserToken with a specified user ID and the key to use
//  to sign the user attributes.
BVUserToken::BVUserToken(const char* userID, const char* sharedKey)
{
    _sharedKey = sharedKey;
    _buffer = new char[32];
    _buffer[0] = '\0';
    _bufferAllocated = 32;

    time_t now;
    time(&now);
    struct tm* timeFields = gmtime(&now);
    char dateString[20];
    sprintf(dateString, "%d-%d-%d", timeFields->tm_year+1900, timeFields->tm_mon+1, timeFields->tm_mday);
    Add("date", dateString);

    Add("userid", userID);
}

BVUserToken::~BVUserToken()
{
    delete [] _buffer;
}

void BVUserToken::AddProfileName(const char* userProfileName)
{
    Add("username", userProfileName);
}

void BVUserToken::AddEmailAddress(const char* userEmailAddress)
{
    Add("emailaddress", userEmailAddress);
}

void BVUserToken::AddStaffAffiliation(bool userIsStaff)
{
    Add("affiliation", userIsStaff ? "staff" : "none");
}

void BVUserToken::AddExpiresAfterNDays(int numberOfDays)
{
    // signed user information must be valid for at least 1 day
    if (numberOfDays < 1) {
        numberOfDays = 1;
    }
    char daysString[20];
    sprintf(daysString, "%d", numberOfDays);
    Add("maxage", daysString);
}

char* BVUserToken::ToString()
{
    return BazaarvoiceUtils::SignAndEncode(_buffer, _sharedKey);
}

void BVUserToken::Add(const char* key, const char* value)
{
    if (value && *value) {
        char* urlEncodedValue = BazaarvoiceUtils::UrlEncode(value);

        // resize the buffer if necessary
        int bufferLength = strlen(_buffer);
        int required = 1 + strlen(key) + 1 + strlen(urlEncodedValue) + 1;  // count 1 for '&', '=', '\0'
        if (bufferLength + required > _bufferAllocated) {
            int newAllocated = _bufferAllocated * 2;
            char* temp = new char[newAllocated];
            strcpy(temp, _buffer);
            delete [] _buffer;
            _buffer = temp;
            _bufferAllocated = newAllocated;
        }

        // add &name=urlEncodedValue to the end of the buffer
        if (bufferLength > 0) {
            strcat(_buffer, "&");
        }
        strcat(_buffer, key);
        strcat(_buffer, "=");
        strcat(_buffer, urlEncodedValue);

        // cleanup
        delete [] urlEncodedValue;
    }
}
