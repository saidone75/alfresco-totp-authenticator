function main()
{
    model.success = false;
    var connector = remote.connect("alfresco");
    if (args[1] == "generate-new-token" || args[1] == "activate")
    {
        var result = connector.get("/security/gensecret");
        if (result.status == 200)
        {
            model.success = true;
        }
    }
    else if (args[1] == "clear-token")
    {
        var result = connector.get("/security/setsecret?secret=");
        if (result.status == 200)
        {
            model.success = true;
        }
    }
}

main();
