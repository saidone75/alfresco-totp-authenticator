function main()
{
    var userId = page.url.templateArgs["userid"];
    if (userId == null)
    {
        userId = user.name;
    }

    model.activeUserProfile = (userId == null || userId == user.name);

    var connector = remote.connect("alfresco");
    var result = connector.get("/security/getsecret?user=" + userId);
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
