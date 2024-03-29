# Private Key Export AES-256-CBC in PKCS#8 format
Sort sketch how a AES-256 it CBC key export can look like using

* PBKDF2 using HMAC with SHA-512 and 100000 iterations
* PKCS#8 encryption
* PEM encoding

# Usage

The project has two main classes. One in `ExportPrivateKey` to export a private key and one in `ImportPrivateKey` to import an encrypted private key. Both classes require two arguments. First, the path where to import/export a key to/from. And a password which should be used to secure the private key. 

# OpenSSL

## Decrypt

To view the decrypt private key use the following `openSSL` command:

`openssl rsa -in <PATH_TO_KEY> -text` 

Enter your chosen password.

## Verify usage of algorithm

`openssl asn1parse -i -dump < <PATH_TO_KEY>`

This will print the ASN.1 structure and interpret the hex values. Have a look at the headers.

```
    0:d=0  hl=4 l=2502 cons: SEQUENCE          
    4:d=1  hl=2 l= 112 cons:  SEQUENCE          
    6:d=2  hl=2 l=   9 prim:   OBJECT            :PBES2
   17:d=2  hl=2 l=  99 cons:   SEQUENCE          
   19:d=3  hl=2 l=  66 cons:    SEQUENCE          
   21:d=4  hl=2 l=   9 prim:     OBJECT            :PBKDF2
   32:d=4  hl=2 l=  53 cons:     SEQUENCE          
   34:d=5  hl=2 l=  32 prim:      OCTET STRING      
      0000 - f5 53 de d7 cb f8 02 18-46 b5 8c 03 c3 cc c9 c7   .S......F.......
      0010 - 5d 18 d8 0f 32 d0 12 7c-e2 1f 94 3b 3a 6a 7c e6   ]...2..|...;:j|.
   68:d=5  hl=2 l=   3 prim:      INTEGER           :0186A0
   73:d=5  hl=2 l=  12 cons:      SEQUENCE          
   75:d=6  hl=2 l=   8 prim:       OBJECT            :hmacWithSHA256
   85:d=6  hl=2 l=   0 prim:       NULL              
   87:d=3  hl=2 l=  29 cons:    SEQUENCE          
   89:d=4  hl=2 l=   9 prim:     OBJECT            :aes-256-cbc
  100:d=4  hl=2 l=  16 prim:     OCTET STRING      
      0000 - d6 d9 5b 15 27 27 44 83-6d e8 bb 91 77 4a 4f cf   ..[.''D.m...wJO.
  118:d=1  hl=4 l=2384 prim:  OCTET STRING      
      0000 - 74 00 d9 f5 72 ec fc 6b-c7 29 6b 5c 3e 1a eb b8   t...r..k.)k\>...
      0010 - 97 35 16 15 d3 aa ef 0e-3e 74 ff 47 0d f0 1f 73   .5......>t.G...s
      0020 - ee ec 4f 46 4b 24 6a c9-41 a0 5c 78 05 a3 73 3f   ..OFK$j.A.\x..s?
      0030 - 19 2f 5d 00 80 8b 2b dd-97 8e af 5a 97 34 4b 18   ./]...+....Z.4K.
      0040 - cf 8f 32 d0 d3 93 73 18-b4 db 78 d4 1f 17 75 db   ..2...s...x...u.
      0050 - 64 d7 f6 6e 7c d8 30 8f-3c 75 0f 56 0d 40 09 07   d..n|.0.<u.V.@..
      0060 - 67 84 bf f5 17 9e 19 a4-cb fc 0a 16 09 f5 ce ec   g...............
      [... encrypted private key ...]
```

We can see that **PBKDF2** is used as well as **HMACS with SHA-512** to derive the aes key. The hex value `0x0186A0` translates to our 100000 number of iterations.
Further, we can verify that AES-256 bit in CBC mode is used. 

# File Export

Most user friendly and secure way to migrate files from one system to another might be GPG (GNU Privacy Guard).
In the default configuration it uses some rounds of SHA-1 to derive a key from a given password. To improve this the following configuration was set:

* Encryption: AES-CFB-256 
  * CFB (Cipher Feedback Mode) is the only supported algorithm in GPG and is considered save ([NIST Recommendation for Block Cipher Modes](https://csrc.nist.gov/publications/detail/sp/800-38a/final)).  Since each block is XORed with the previous block a parallel processing is not possible.
* Key Derivation: 65011712 rounds of SHA-256    
  * SHA-256 with 65011712 (0xFF) iterations
  * Default is SHA-1 with 0x60 iterations.
  * Hash is salted using `SecureRandom`
  
# Usage

Run the `GPGExport` with specifing an input file and a password as arguments.
It produces a file in the same directory called `<YOUR_FILE>.gpg`. This file can be opened by GPGSuite (for Mac) or gpg4win in windows. 

## Inspecting Header of .gpg files

The used parameters can be verified by using the gpg commandline tools:

```
> gpg --list-packets key.pem.gpg 
gpg: keyserver option 'ca-cert-file' is obsolete; please use 'hkp-cacert' in dirmngr.conf
gpg: AES256 encrypted data
gpg: encrypted with 1 passphrase
# off=0 ctb=8c tag=3 hlen=2 plen=13
:symkey enc packet: version 4, cipher 9, s2k 3, hash 8
        salt C4098A18FCB0798E, count 65011712 (255)
# off=15 ctb=d2 tag=18 hlen=3 plen=2710 new-ctb
:encrypted data packet:
        length: 2710
        mdc_method: 2
# off=37 ctb=a3 tag=8 hlen=1 plen=0 indeterminate
:compressed packet: algo=1
# off=39 ctb=cb tag=11 hlen=3 plen=3484 new-ctb
:literal data packet:
        mode b (62), created 1571314317, name="key.pem",
        raw data: 3471 bytes
expr: syntax error
[: Missing argument at index 2
```

S2K:
* 3 - Version 3 using salt and iterations

Hash:
* 1: MD5
* 2: SHA1
* 3: RIPEMD160
* 4: DOUBLE_SHA
* 5: MD2
* 6: TIGER_192
* 7: HAVAL_5_160
* **8: SHA256**
* 9: SHA384
* 10: SHA512
* 11: SHA224

Algorithm:
* 0: NULL
* 1: IDEA
* 2: TRIPLE_DES
* 3: CAST5
* 4: BLOWFISH
* 5: SAFER
* 6: DES
* 7: AES_128
* **8: AES_192**
* 9: AES_256
* 10: TWOFISH
* 11: CAMELLIA_128
* 12: CAMELLIA_192
* 13: CAMELLIA_256