# Private Key Export AES-256-CBC
Sort sketch how a AES-256 it CBC key export can look like. 

# Usage

The project has two main classes. One in `ExportPrivateKey` to export a private key and one in `ImportPrivateKey` to import an encrypted private key. Both classes require two arguments. First, the path where to import/export a key to/from. And a password which should be used to secure the private key. 

# OpenSSL

To view the encrypted private key use the following `openSSL` command:

`openssl rsa -in <PATH_TO_KEY> -text` 

Enter your choosen password. 

# TODO

* Find a way to set interation count up to 100000
* Find a way to set salt and iv


