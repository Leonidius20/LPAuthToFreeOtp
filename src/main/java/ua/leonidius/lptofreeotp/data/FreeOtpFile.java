package ua.leonidius.lptofreeotp.data;

import org.fedorahosted.freeotp.data.legacy.Token;

public class FreeOtpFile {

    String[] tokenOrder;
    Token[] tokens;

    public FreeOtpFile(String[] tokenOrder, Token[] tokens) {
        this.tokenOrder = tokenOrder;
        this.tokens = tokens;
    }

}
