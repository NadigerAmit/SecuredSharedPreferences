# Secured SharedPreferences

## The Android secure storage is based on JCA (Java cryptography architecture )

If the device is rooted , the information stored in the shared preference is no more secure.

In order to make full proof from App perspective , irrespective of the device on which app is installed , 
we can use Android keyStore  with encryption ,decryption techniques 

The objective is to store  the store sensitive information , so that no one but just that app on that device can access the sensitive Info.

## This is achieved as below .

1. Create & load  the Keystore .
2. Generate the keys (Symmetric or Asymmetric.)
3. Encrypt the sensitive information.
        can uses symmetric  or combination of asymmetric and symmetric algo
4. Keys used will be stored in keystore
5. Encrypted information will be stored in shared preference or other storage.
6. When need to access the information , get the encrypted info from storage and decrypt it using keys stored in key store.
        Here keys are stored in keystore  , so no one but app can retrieve these keys.
         Since Keys are safe , our sensitive info is also safe.
## Components involved in this process 
### Keystore
    KeyStore objects are obtained by using one of the KeyStore getInstance(type) static factory methods.
    Here Type can be many types ; Like  jks, pkcs12, "AndroidKeyStore ,etc..All these types are provided by different providers.
    For Android we use : "AndroidKeyStore" provider.
    Implementation looks like KeyStore.getInstance(ANDROID_KEY_STORE); // keyStore.provider.name= AndroidKeyStore and keyStore.type = AndroidKeyStore
    Default Keystore  type in Android device is BKS (BouncyCastle) <= It should not be used. 
    KeyStore.getInstance(KeyStore.getDefaultType()); <+= here KeyStore.getDefaultType() return BKS.
### Key Generations
     1. Key generator  (Symmetric )
     2. Key Pair generator (Asymmetric)
          a. Master key which will be symmetric key 
          b. This master key will be encrypted by asymmetric keys using public key which is stroed in key-store , this is called wrapping 
          c. encrypted master key will be stored in shared preference.
          d. During decryption , reverse of  steps ii,iii will be done. (This unwrapping)
### Cipher
#### Type of  ciphers:
 ##### Block: 
 Process entire blocks at a time, usually many bytes in length.
 If there is not enough data to make a complete input block, the data must be padded to match cipher's block size.
 The padded bytes are then stripped off during the decryption phase.
       EX : "PKCS5PADDING, ,PKCS7Padding,PKCS1Padding ,etc
##### Stream:
Process incoming data one small unit (typically a byte or even a bit) at a time. 
This allows for ciphers to process an arbitrary amount of data without padding.

##### Modes of Operation :
When encrypting using a simple block cipher, two identical blocks of plaintext will always produce an identical block of cipher text.
Cryptanalysts trying to break the ciphertext will have an easier job if they note blocks of repeating text.
In order to add more complexity to the text, feedback modes use the previous block of output to alter the input blocks before applying the encryption algorithm. 
The first block will need an initial value, and this value is called the initialization vector (IV)
IV can be random and need not be secret .
Modes EX:
     1. CBC (Cipher Block Chaining),  <=  each cipher data block depends on all plain data blocks processed up to that point.
          To make each message unique, an initialization vector must be used in the first block.
     2. CFB (Cipher Feedback Mode), 
     3. OFB (Output Feedback Mode). 
     4. ECB (Electronic Codebook Mode)  <= ECB ciphertexts are the same if they use the same plaintext/key , Dont use for encryption.
          Note: ECB mode is the easiest block cipher mode to use and is the default in the JDK/JRE. 
          ECB works well for single blocks of data, but absolutely should not be used for multiple data blocks

###### 1. Creating the Cipher Object:
    1. mCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");  <=  asymmetric cipher
    2. mCipher = Cipher.getInstance("AES/CBC/PKCS7Padding);    <=  symmetric cipher
###### Initializing a Cipher Object
     A Cipher object obtained via getInstance must be initialized for one of four modes, which are defined as final integer constants in the Cipher class.
     1. ENCRYPT_MODE  => Encryption of data.
     2. DECRYPT_MODE  =>  Decryption of data.
     3. WRAP_MODE        =>  Wrapping a java.security.Key into bytes so that the key can be securely transported.
          Process of encrypting the  previously encrypted secret symmetric key(Master key ) with public key .
     4. UNWRAP_MODE   =>  Unwrapping of a previously wrapped key into a java.security.Key object.
         Process of decrypting  the previously encrypted secret symmetric key(Master Key)  with private key .

EncryptedData  : The output of  encryption and input to Decryption.
### Initialization Vector (IV) 
    The first block will need an initial value, and this value is called the initialization vector (IV)
    IV can be random and need not be secret .
### Storage media
    SharedPreference
## Master Key generation :
This is independent of keyStore .(Used in device with api >=18 && <23  for big data )
Create symmetric key(MASTER) with one of default Java Providers.  The most common default Java provider in android is the cut version of BC provider created by the popular third party Java cryptographic library provider — Bouncy Castle.
Encrypt / decrypt message with it. 
Then encrypt this key raw data with RSA public key and save it in shared preference or somewhere.
 On decryption, get encrypted raw key data,  decrypt it with RSA private key and use it for message decryption.
 
### Types of Encryption/Decryption:
 There are 3 types of Encryption/ Decryption

#### Symmetric(secret key)        
same secret key to both encrypt and decrypt the data.
#### Asymmetric(public key cryptography)
uses a public(Encryption)/private(Decryption) key pair to encrypt data(Usually case ). In below case Reverse is used (Public: Decrypt , Private: Encrypt). Asymmetric is not suitable for large data , Please see Hybrid for this.
 In case of Digital signature authorization => the authority uses their private key to encrypt the contents of the certificate, and this cipher text is attached to the certificate as its digital signature. 
Anyone can decrypt this signature using the authority’s public key, and verify that it results in the expected decrypted value. 
Only the authority can encrypt content using the private key, and so only the authority can actually create a valid signature in the first place.
#### Hybrid (Symmetric + Asymmetric ) 
   Symmetric (Secret) Key used to encrypt, decrypts the actual info , This secret key it self encrypted using Asymmetric (public/private).
### Overall flow management of Keystore , encryption ,Decryption , Cipher :
1. Create a class called "CryptoService.java" which abstracts  following component  . So  it acts as block box containing "Keystore , cipher (encryption/Decryption )".
2. It provides interfaces for below functionality:
     
     a. Encryption 
     
     b. Decryption 
     
     c. Storing the encrypted Master Key in shared preference (in device APIL<23)  
3. "CryptoService". class handles below decisions based on android version(API level ) and type of encryption ,Decryption 
    
    a. If API Level >=23
        
        i. Create Master key (Android symmetric key ) if API level >=23
    
    b. If API Level <23 and >=18
        
        i. Create Master key (default  java provided symmetric key) if API level <23 and >=18.
       
       ii. Create the Keppair(Private + Public) if   if API level <23 and >=18. Using "KeyPairGeneratorSpec "
            
            1. If API level <23 and <= 18 => we must use Using "KeyPairGeneratorSpec " for generating the key pair
            
            2. If API level >23 we must use "KeyGenParameterSpec" as "KeyPairGeneratorSpec " is deprecated <= does not happen as we use symmetric key if API >23. If we want use specifically asymmetric crypto in device API>23 , then use "KeyGenParameterSpec".
      
      iii. Encrypt the Master Key generated in i step with public key (This is called wrap)  <= when need to store in shared preference or secure transfer of hardware-based keys.
       
       iV. Decrypt the Master Key encrypted in iii step with privatekey (This is called unwrap) <= when need to encrypt the sensitive information.


