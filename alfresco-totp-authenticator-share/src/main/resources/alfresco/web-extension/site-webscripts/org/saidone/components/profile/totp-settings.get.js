function main()
{
    var connector = remote.connect("alfresco");
    var result = connector.get("/security/getsecret");
    if (result.status == 200)
    {
        model.result = JSON.parse(result).data;
    }
    var totpSettings = {
        id : "totp-settings",
        name : "Alfresco.totp-settings",
        options : {
        }
    };
    model.widgets = [totpSettings];
}

main();
