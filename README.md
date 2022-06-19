# 128-bit-AES-ECB
My own handcrafted 128-bit AES electronic code book.
128-bit only, no option to go for 192 or 256.
It's ECB, can't take any Initialization Vector.
Validated with AESAVS (https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Algorithm-Validation-Program/documents/aes/AESAVS.pdf) Section 6.4.1,
and NIST FIPS-197 (https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.197.pdf) appendix B and C1.
Crafted using pure python and kotlin, no library or extension used.
