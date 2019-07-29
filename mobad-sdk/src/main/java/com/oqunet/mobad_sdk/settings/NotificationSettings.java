package com.oqunet.mobad_sdk.settings;


public class NotificationSettings {
    // The project number you obtained earlier in the Google Cloud Console.
    public static String SenderId = "386732088732";

    public static String ApId = "1:386732088732:android:2dc971a43f9885b1";

    // Use the name of your notification hub that appears in the hub blade in the Azure Portal.
    public static String HubName = "SDKHub";

    // The DefaultListenAccessSignature connection string for your hub.
    public static String HubListenConnectionString = "Endpoint=sb://mobadhubs.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=8eSHuM0jXlhvpabh8WyfpIovzTHKvF8ibkaoz59nodw=";

    public static String HubFullAccess = "<your DefaultFullSharedAccessSignature>";
}
