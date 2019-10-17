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
    0:d=0  hl=4 l=2536 cons: SEQUENCE          
    4:d=1  hl=3 l= 145 cons:  SEQUENCE          
    7:d=2  hl=2 l=   9 prim:   OBJECT            :PBES2
   18:d=2  hl=3 l= 131 cons:   SEQUENCE          
   21:d=3  hl=2 l=  98 cons:    SEQUENCE          
   23:d=4  hl=2 l=   9 prim:     OBJECT            :PBKDF2
   34:d=4  hl=2 l=  85 cons:     SEQUENCE          
   36:d=5  hl=2 l=  64 prim:      OCTET STRING      
      0000 - 92 df fb 7f 60 67 c1 d8-c4 bd 8a 3a 9a d7 f5 92   ....`g.....:....
      0010 - c7 a9 0e 83 db 8e 26 85-53 aa ec 5e 2e 9d ff e3   ......&.S..^....
      0020 - 70 5b 1e 1b 38 81 31 48-b0 95 5f 19 63 a4 45 2f   p[..8.1H.._.c.E/
      0030 - 54 b1 26 87 0c 5e 96 d0-d9 51 41 7c d5 62 75 06   T.&..^...QA|.bu.
  102:d=5  hl=2 l=   3 prim:      INTEGER           :0186A0
  107:d=5  hl=2 l=  12 cons:      SEQUENCE          
  109:d=6  hl=2 l=   8 prim:       OBJECT            :hmacWithSHA512
  119:d=6  hl=2 l=   0 prim:       NULL              
  121:d=3  hl=2 l=  29 cons:    SEQUENCE          
  123:d=4  hl=2 l=   9 prim:     OBJECT            :aes-256-cbc
  134:d=4  hl=2 l=  16 prim:     OCTET STRING      
      0000 - f7 f7 f0 8d b5 84 46 c7-5e 85 b3 2c b8 38 60 57   ......F.^..,.8`W
  152:d=1  hl=4 l=2384 prim:  OCTET STRING      
      0000 - 88 f8 fb ea cb 05 0b eb-5f ec 68 ee 93 98 d8 0b   ........_.h.....
      0010 - 4f 00 6c 78 08 5d 62 ca-7d 4d 5b 9e c8 71 97 bd   O.lx.]b.}M[..q..
      0020 - 7d bd ad 75 28 ab a4 31-92 d9 6f 75 00 76 a3 d4   }..u(..1..ou.v..
      0030 - 9b 2f 7f a3 ef c5 67 15-da 64 68 65 e1 fa 6f 28   ./....g..dhe..o(
      [... encrypted private key content ... ]
```

We can see that **PBKDF2** is used as well as **HMACS with SHA-512** to derive the aes key. The hex value `0x0186A0` translates to our 100000 number of iterations.
Further, we can verify that AES-256 bit in CBC mode is used. 

