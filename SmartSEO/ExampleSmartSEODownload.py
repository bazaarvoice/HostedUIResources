#!/usr/bin env python

"""
This script is to be used a reference for downloading Bazaarvoice's
SmartSEO feeds.

Required dependencies:
    paramiko - installed in python import path for sftp

Configuration:
    Edit the variables at the end of this file following
    the line that says:
    if "__name__" == __main__:

    Required:
        environment
        protocol
        username
        output_directory
        output_backup

    Possibly required:
        private_key_file
        private_key_password
        password
        bv_path
        bv_filename
"""

import sys
import os
import shutil
import logging
import logging.handlers
from datetime import datetime

LOG_FILENAME = '/var/log/bv_smartseo_downloader.log'
_formatter = logging.Formatter("%(asctime)s - %(levelname)s - %(message)s")
_handler = logging.handlers.RotatingFileHandler(LOG_FILENAME, maxBytes=1048576, backupCount=5)
_handler.setFormatter(_formatter)
bv_logger = logging.getLogger('SmartSEODownloader')
bv_logger.addHandler(_handler)
bv_logger.setLevel(logging.DEBUG)

class SmartSEODownloader(object):
    def __init__(self, env='prod', protocol='sftp'):
        if env == 'prod':
            self.env = 'prod'
            if protocol == 'sftp':
                self.protocol = 'sftp'
                self.server = 'sftp.bazaarvoice.com'
            else:
                self.protocol = 'ftp'
                self.server = 'ftp.bazaarvoice.com'
        else:
            self.env = 'stg'
            if protocol == 'sftp':
                self.protocol = 'sftp'
                self.server = 'sftp-stg.bazaarvoice.com'
            else:
                self.protocol = 'ftp'
                self.server = 'ftp-stg.bazaarvoice.com'

    def connect(self, username, password=None, private_key=None,
            private_key_password=None):
        if self.protocol == 'sftp':
            bv_logger.debug("SFTP connection starting...")
            try:
                import paramiko
                _transport = paramiko.Transport((self.server, 22))
                bv_logger.debug("SFTP Transport created")
                if private_key:
                    _key = paramiko.RSAKey.from_private_key_file(private_key,
                            password=private_key_password)
                    _transport.connect(hostkey=None, username=username, pkey=_key)
                    bv_logger.debug("SFTP Private Key connection transport established")
                elif password:
                    _transport.connect(hostkey=None, username=username, password=password)
                    bv_logger.debug("SFTP password connection transport established")
                else:
                    bv_logger.exception("Need private key or password.")
                    raise Exception("Need private key or password.")
                self.connection = paramiko.SFTPClient.from_transport(_transport)
                bv_logger.info("SFTP connection successfully established.")
                return self.connection
            except Exception, e:
                bv_logger.exception("Unable to SFTP connect to %s: %s" % (self.server, e))
                raise Exception("Unable to SFTP connect to %s: %s" % (self.server, e))
        elif self.protocol == 'ftp':
            try:
                from ftplib import FTP
                self.connection = FTP(self.server)
                self.connection.login(username, password)
                bv_logger.info("FTP connection successfully established.")
                return self.connection
            except Exception, e:
                bv_logger.exception("Unable to FTP connect to %s: %s" % (self.server, e))
                raise Exception("Unable to FTP connect to %s: %s" % (self.server, e))
        else:
            bv_logger.error("Unknown protocol. Please use ftp or sftp.")
            raise Exception("Unknown protocol. Please use ftp or sftp.")

    def get(self, path, filename, destination=None):
        """
        This gets the filename from the path and stores it
        in destination or a NamedTemporaryFile.

        NamedTemporaryFiles are deleted automatically when this
        finishes. If 'destination' is specified, it is up to
        the user to delete the file.
        """
        bv_logger.debug("Attempting download...")
        if not self.connection:
            raise Exception("Connection not established.")
        if not path.endswith('/'):
            path = path + "/"
        if not destination:
            # just use a temporary file
            from tempfile import NamedTemporaryFile
            destination = NamedTemporaryFile().name
            bv_logger.debug("Temporary file created: %s" % destination)
        if self.protocol == 'sftp':
            try:
                self.connection.get(path+filename, destination)
                self.downloaded_file = destination
                bv_logger.info("File downloaded via SFTP: %s" % self.downloaded_file)
                return self.downloaded_file
            except Exception, e:
                bv_logger.exception("Unable to get file %s using SFTP: %s" % (path+filename, e))
                raise Exception("Unable to get file %s using SFTP: %s" % (path+filename, e))
        elif self.protocol == 'ftp':
            try:
                _localdest = open(destination, 'wb')
                self.connection.cwd(path)
                self.connection.retrbinary('RETR ' + filename, _localdest.write)
                self.downloaded_file = destination
                bv_logger.info("File downloaded via FTP: %s" % self.downloaded_file)
                return self.downloaded_file
            except Exception, e:
                bv_logger.exception("Unable to get file %s using FTP: %s" % (path+filename, e))
                raise Exception("Unable to get file %s using FTP: %s" % (path+filename, e))
        else:
            raise Exception("Unknown protocol.")

    def extract(self, outputdir, backupdir):
        """
        If get() was successfully run, then extract can be called
        to backup the existing outputdir and replace it
        with the new version just downloaded.
        """
        if self.downloaded_file:
            import zipfile
            try:
                _file_to_extract = zipfile.ZipFile(self.downloaded_file, "r")
            except:
                raise Exception("Unable to read downloaded file.")
            backupdir = os.path.join(backupdir, datetime.now().strftime("%Y%m%d-%H:%M:%S"))
            if not os.path.isdir(backupdir):
                try:
                    os.makedirs(backupdir)
                    bv_logger.debug("Created backup directory %s" % backupdir)
                except:
                    bv_logger.exception("Unable to create backup directory.")
                    raise Exception("Unable to create backup directory.")
            if os.path.isdir(outputdir):
                try:
                    shutil.move(outputdir, backupdir)
                    bv_logger.debug("Moved %s to %s" % (outputdir, backupdir))
                except:
                    bv_logger.exception("Unable to backup old files.")
                    raise Exception("Unable to backup old files.")
            else:
                try:
                    os.makedirs(outputdir)
                    bv_logger.debug("Created directory %s" % outputdir)
                except:
                    bv_logger.exception("Unable to create output directory.")
                    raise Exception("Unable to create output directory.")
            try:
                _file_to_extract.extractall(outputdir)
                for f in _file_to_extract.namelist():
                    bv_logger.info("Extracted %s" % outputdir+f)
            except:
                bv_logger.exception("Unable to extract new files.")
                raise Exception("Unable to extract new files.")

    def close(self):
        self.connection.close()
        bv_logger.debug("Connection closed.")

if __name__ == "__main__":
    """
    This code is run if the script is run directly.

    Another option is to import the SmartSEODownloader class
    into your existing python modules with this as an example.

    """

    ### REQUIRED ### Set variables if you run this directly
    ## 'stg' or 'prod'
    environment = "stg"
    ## 'sftp' or 'ftp'
    protocol = "sftp"
    ## BV supplied username
    username = "username"
    ## Path to RSA private key file
    private_key_file = "/path/to/.ssh/id_rsa"
    ## Passphrase for an encrypted private key, if required
    #private_key_password = "SecretPassphrase"
    ## password not required for sftp public key auth
    #password = 'mysecretpassword'
    ## full path to SmartSEO output dir
    output_directory = "/full/path/to/outputdir/"
    ## full path to SmartSEO backup directory
    output_backup = "/full/path/to/backupdir/"
    ## Customize as necessary
    bv_path = "feeds"
    bv_filename = "bv_%s_smartseo.zip" % username

    try:
        downloader = SmartSEODownloader(environment, protocol)
        try:
            # Private key login
            downloader.connect(username, private_key=private_key_file,
                    private_key_password=private_key_password)
            print "Success: SFTP private key connection"
        except NameError:
            downloader.connect(username, password=password)
            print "Success: %s password connection" % protocol
        except:
            raise
        try:
            downloader.get(bv_path, bv_filename)
            print "Success: Downloaded %s" % bv_filename
            downloader.extract(output_directory, output_backup)
            print "Success: Extracted output to %s" % output_directory
        except:
            raise
    except Exception, e:
        print "Failed: %s" % e
    else:
        downloader.close()

