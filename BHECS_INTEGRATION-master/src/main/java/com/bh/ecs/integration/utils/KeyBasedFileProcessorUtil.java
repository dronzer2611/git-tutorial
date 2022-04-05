
    
package com.bh.ecs.integration.utils;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;

public class KeyBasedFileProcessorUtil {
	
    private KeyBasedFileProcessorUtil() {
		throw new IllegalStateException("KeyBasedFileProcessorUtil class");
	}
    private static Logger log = LoggerFactory.getLogger(KeyBasedFileProcessorUtil.class);

	public static PGPPublicKey readPublicKey(InputStream in) throws IOException, PGPException {
        in = PGPUtil.getDecoderStream(in);


        PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(in);


        //
        // we just loop through the collection till we find a key suitable for
        // encryption, in the real
        // world you would probably want to be a bit smarter about this.
        //


        //
        // iterate through the key rings.
        //
        Iterator<?> rIt = pgpPub.getKeyRings();


        while (rIt.hasNext()) {
            PGPPublicKeyRing kRing = (PGPPublicKeyRing) rIt.next();
            Iterator<?> kIt = kRing.getPublicKeys();


            while (kIt.hasNext()) {
                PGPPublicKey k = (PGPPublicKey) kIt.next();


                if (k.isEncryptionKey()) {
                    return k;
                }
            }
        }


        throw new IllegalArgumentException("Can't find encryption key in key ring.");
    }


    public static void encryptFile(OutputStream out, File fileName, PGPPublicKey encKey, boolean armor,
            boolean withIntegrityCheck) throws IOException, NoSuchProviderException {
        if (armor) {
            out = new ArmoredOutputStream(out);
        }


        try {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();


            PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(PGPCompressedData.ZIP);


            PGPUtil.writeFileToLiteralData(comData.open(bOut), PGPLiteralData.BINARY, fileName);


            comData.close();


            PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(PGPEncryptedData.CAST5, withIntegrityCheck,
                    new SecureRandom(), "BC");


            cPk.addMethod(encKey);


            byte[] bytes = bOut.toByteArray();


            OutputStream cOut = cPk.open(out, bytes.length);


            cOut.write(bytes);


            cOut.close();


            out.close();
        } catch (PGPException e) {	
            if (e.getUnderlyingException() != null) {
               e.getUnderlyingException();
            }
        }
    }


    public static void decryptFile(InputStream in, OutputStream out, InputStream keyIn, char[] passwd)
    {

		try {

			Security.addProvider(new BouncyCastleProvider());

			in = PGPUtil.getDecoderStream(in);

			PGPObjectFactory pgpF = new PGPObjectFactory(in);
			PGPEncryptedDataList enc;

			Object o = pgpF.nextObject(); // the first object might be a PGP marker
			if (o instanceof PGPEncryptedDataList) {
				enc = (PGPEncryptedDataList) o;
			} else {
				enc = (PGPEncryptedDataList) pgpF.nextObject();
			}

			// // find the secret key //
			Iterator<PGPPublicKeyEncryptedData> it = enc.getEncryptedDataObjects();
			PGPPrivateKey sKey = null;
			PGPPublicKeyEncryptedData pbe = null;

			while (sKey == null && it.hasNext()) {
				pbe = it.next();

				sKey = findSecretKey(keyIn, pbe.getKeyID(), passwd);
			}

			if (sKey == null) {
				throw new IllegalArgumentException("Secret key for message not found.");
			}

			InputStream clear = pbe.getDataStream(sKey, "BC");

			PGPObjectFactory plainFact = new PGPObjectFactory(clear);

			Object message = plainFact.nextObject();

			if (message instanceof PGPCompressedData) {
				PGPCompressedData cData = (PGPCompressedData) message;
				PGPObjectFactory pgpFact = new PGPObjectFactory(cData.getDataStream());

				message = pgpFact.nextObject();
			}

			if (message instanceof PGPLiteralData) {
				PGPLiteralData ld = (PGPLiteralData) message;

				InputStream unc = ld.getInputStream();
				int ch;

				while ((ch = unc.read()) >= 0) {
					out.write(ch);
				}
			} else if (message instanceof PGPOnePassSignatureList) {
				throw new PGPException("Encrypted message contains a signed message - not literal data.");
			} else {
				throw new PGPException("Message is not a simple encrypted file - type unknown.");
			}

			if (pbe.isIntegrityProtected() && !pbe.verify()) {
				
					throw new PGPException("Message failed integrity check");
				
			}

		} catch (Exception e) {
			log.error("Exception in decrypting the file", e);
		}
}
    
   private static PGPPrivateKey findSecretKey(InputStream keyIn, long keyID,
            char[] passwd)
           
            throws IOException, PGPException, NoSuchProviderException {
            PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(
            org.bouncycastle.openpgp.PGPUtil.getDecoderStream(keyIn));
           
            PGPSecretKey pgpSecKey = pgpSec.getSecretKey(keyID);
           
            if (pgpSecKey == null) { return null; }
           
            return pgpSecKey.extractPrivateKey(passwd, "BC"); }
    
    
}
 