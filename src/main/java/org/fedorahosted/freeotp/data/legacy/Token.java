/*
 * FreeOTP
 *
 * Authors: Nathaniel McCallum <npmccallum@redhat.com>
 *
 * Copyright (C) 2013  Nathaniel McCallum, Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fedorahosted.freeotp.data.legacy;

import com.google.android.apps.authenticator.Base32String;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Token {
    public static class TokenUriInvalidException extends Exception {
        private static final long serialVersionUID = -1108624734612362345L;
    }

    public static enum TokenType {
        HOTP, TOTP
    }

    private static char[] STEAMCHARS = new char[] {
            '2', '3', '4', '5', '6', '7', '8', '9', 'B', 'C',
            'D', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q',
            'R', 'T', 'V', 'W', 'X', 'Y'};

    private String algo;
    private String issuerExt;
    private String label;
    private byte[] secret;
    private TokenType type;
    private int digits;
    private long counter;
    private int period;

    public Token(String algo, String issuerExt, String label, byte[] secret, TokenType type, int digits, long counter, int period) {
        this.algo = algo;
        this.issuerExt = issuerExt;
        this.label = label;
        this.secret = secret;
        this.type = type;
        this.digits = digits;
        this.counter = counter;
        this.period = period;
    }

    public long getCounter() {
        return counter;
    }

    private String getHOTP(long counter) {
        // Encode counter in network byte order
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putLong(counter);

        // Create digits divisor
        int div = 1;
        for (int i = digits; i > 0; i--)
            div *= 10;

        // Create the HMAC
        try {
            Mac mac = Mac.getInstance("Hmac" + algo);
            mac.init(new SecretKeySpec(secret, "Hmac" + algo));

            // Do the hashing
            byte[] digest = mac.doFinal(bb.array());

            // Truncate
            int binary;
            int off = digest[digest.length - 1] & 0xf;
            binary = (digest[off] & 0x7f) << 0x18;
            binary |= (digest[off + 1] & 0xff) << 0x10;
            binary |= (digest[off + 2] & 0xff) << 0x08;
            binary |= (digest[off + 3] & 0xff);

            String hotp = "";
            if (issuerExt.equals("Steam")) {
                for (int i = 0; i < digits; i++) {
                    hotp += STEAMCHARS[binary % STEAMCHARS.length];
                    binary /= STEAMCHARS.length;
                }
            } else {
                binary = binary % div;

                // Zero pad
                hotp = Integer.toString(binary);
                while (hotp.length() != digits)
                    hotp = "0" + hotp;
            }

            return hotp;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }

    public int getDigits() {
        return digits;
    }

    public TokenType getType() {
        return type;
    }

    public String getSecret() {
        return Base32String.encode(secret);
    }

    public String getAlgorithm() {
        return algo;
    }

    public int getPeriod() {
        return period;
    }

    public String getLabel() {
        return label;
    }

    public String getIssuerExt() {
        return issuerExt;
    }
}