package it.buffolollo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * La classe Encryption permette di utilizzare svariati algoritmi di criptazione
 */
public class Encryption {
   /**
    * Algoritmo SHA-256
    *
    * @param s Stringa da criptare
    * @return Stringa criptata
    * @throws NoSuchAlgorithmException Algoritmo non disponibile
    */
   public static String sha256(String s) throws NoSuchAlgorithmException {
      // acquisisci algoritmo SHA-256 da MessageDigest
      MessageDigest md = MessageDigest.getInstance("SHA-256");

      // utilizza l'algoritmo acquisito per criptare in un array di byte (hash) la
      // stringa
      byte[] hash = md.digest(s.getBytes());

      // converti l'hash in stringa esadecimale
      StringBuilder hexString = new StringBuilder();
      for (byte b : hash) {
         hexString.append(String.format("%02x", b));
      }

      // ritorno la stringa
      return hexString.toString();
   }
}