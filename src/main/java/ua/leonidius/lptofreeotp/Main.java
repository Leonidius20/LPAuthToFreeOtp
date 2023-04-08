package ua.leonidius.lptofreeotp;

import com.google.android.apps.authenticator.Base32String;
import com.google.gson.Gson;
import org.fedorahosted.freeotp.data.legacy.Token;
import ua.leonidius.lptofreeotp.data.FreeOtpFile;
import ua.leonidius.lptofreeotp.data.LastPassFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Base32String.DecodingException, IOException {
        // prompt user to enter file path using scanner
        System.out.print("Enter file path: ");
        var scanner = new Scanner(System.in);
        String filePath = scanner.nextLine();

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File does not exist");
            return;
        }

        Gson gson = new Gson();

        // read file using gson
        LastPassFile lastPassFile = gson.fromJson(Files.newBufferedReader(Path.of(filePath)), LastPassFile.class);

        // convert to freeotp tokens
        Token[] tokens = Arrays.stream(lastPassFile.accounts)
                .map(account -> {
                    try {
                        // maybe counter should be creationTimestamp?
                        return new Token(account.algorithm, account.issuerName, account.userName, Base32String.decode(account.secret), Token.TokenType.TOTP, account.digits, 0, account.timeStep);
                    } catch (Base32String.DecodingException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .toArray(Token[]::new);

        // crate token order
        String[] tokenOrder = Arrays.stream(tokens)
                .map(token -> token.getIssuerExt() + ":" + token.getLabel())
                .toArray(String[]::new);

        // create freeotp file
        var freeOTPFile = new FreeOtpFile(tokenOrder, tokens);

        // convert token go json using Gson and save to output.json

        var writer = new BufferedWriter(new FileWriter("output.json"));
        gson.toJson(freeOTPFile, writer);
        writer.close();
    }

}
