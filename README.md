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
# Keystore
    KeyStore objects are obtained by using one of the KeyStore getInstance(type) static factory methods.
    Here Type can be many types ; Like  jks, pkcs12, "AndroidKeyStore ,etc..All these types are provided by different providers.
    For Android we use : "AndroidKeyStore" provider.
    Implementation looks like KeyStore.getInstance(ANDROID_KEY_STORE); // keyStore.provider.name= AndroidKeyStore and keyStore.type = AndroidKeyStore
    Default Keystore  type in Android device is BKS (BouncyCastle) <= It should not be used. 
    KeyStore.getInstance(KeyStore.getDefaultType()); <+= here KeyStore.getDefaultType() return BKS.
# Key Generations
     1. Key generator  (Symmetric )
     2. Key Pair generator (Asymmetric)
          a. Master key which will be symmetric key 
          b. This master key will be encrypted by asymmetric keys using public key which is stroed in key-store , this is called wrapping 
          c. encrypted master key will be stored in shared preference.
          d. During decryption , reverse of  steps ii,iii will be done. (This unwrapping)
# Cipher
# Initialization Vector (IV) 
# Storage media

# Master Key generation :
This is independent of keyStore .(Used in device with api >=18 && <23  for big data )
Create symmetric key(MASTER) with one of default Java Providers.  The most common default Java provider in android is the cut version of BC provider created by the popular third party Java cryptographic library provider — Bouncy Castle.
Encrypt / decrypt message with it. 
Then encrypt this key raw data with RSA public key and save it in shared preference or somewhere.
 On decryption, get encrypted raw key data,  decrypt it with RSA private key and use it for message decryption.

