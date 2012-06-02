package yobdc.util;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DESUtil {
	private byte[] key;
	
	public DESUtil(String pw) {
		key = new byte[8];
        System.arraycopy(pw.getBytes(), 0, key, 0, Math.min(key.length, pw.getBytes().length));
	}

	public byte[] EncryptByte(byte[] challenge) {
		SecretKeyFactory keyFactory;
		try {
			DESKeySpec desKeySpec = new DESKeySpec(mirrorBits(key));
			keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
			Cipher desCipher = Cipher.getInstance("DES/ECB/NoPadding");
			desCipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] textEncrypted = desCipher.doFinal(challenge);
			return textEncrypted;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private byte[] mirrorBits(byte[] k) {
		byte[] key = new byte[8];
		for (int i = 0; i < 8; i++) {
			byte s = k[i];
			s = (byte) (((s >> 1) & 0x55) | ((s << 1) & 0xaa));
			s = (byte) (((s >> 2) & 0x33) | ((s << 2) & 0xcc));
			s = (byte) (((s >> 4) & 0x0f) | ((s << 4) & 0xf0));
			key[i] = s;
		}
		return key;
	}
}