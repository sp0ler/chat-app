1. Convert our ".jks" file to ".p12" (PKCS12 key store format):
 
keytool -importkeystore -srckeystore oldkeystore.jks -destkeystore newkeystore.p12 -deststoretype PKCS12
 
1.1. List new keystore file contents:
 
keytool -deststoretype PKCS12 -keystore truststore.p12 -list
 
2. Extract pem (certificate) from ".p12" keysotre file:
 
openssl pkcs12 -nokeys -in truststore.p12 -out certfile.pem
 
3. Extract unencrypted key file from ".p12" keysotre file:
 
openssl pkcs12 -nocerts -nodes -in truststore.p12 -out keyfile.key

keytool -genkeypair -alias chatapp -keyalg RSA -keysize 4096 -validity 3650 -dname "CN=chatapp,OU=denis,O=deev,C=RU" -ext "SAN:c=DNS:host.docker.internal,IP:192.168.0.0" -keypass changeit -keystore keystore.p12 -storeType PKCS12 -storepass changeit

            mode: replicated
            replicas: 2