package ua.leonidius.lptofreeotp.data;

public class LastPassAccount {

    /*
        {"accountID":"f4b69ff0-b52e-457e-92da-c9956d764588","lmiUserId":"","issuerName":"GitHub","originalIssuerName":"GitHub","userName":"Leonidius20","originalUserName":"Leonidius20","pushNotification":false,"secret":"djftjqo6754trq54","timeStep":30,"digits":6,"creationTimestamp":100001,"isFavorite":false,"algorithm":"SHA1","folderData":{"folderId":0,"position":0}}
     */

    public String accountID;
    public String lmiUserId;
    public String issuerName;
    public String originalIssuerName;
    public String userName;
    public String originalUserName;
    public boolean pushNotification;
    public String secret;
    public int timeStep;
    public int digits;
    public long creationTimestamp;
    public boolean isFavorite;
    public String algorithm;
    // public FolderData folderData;


}
