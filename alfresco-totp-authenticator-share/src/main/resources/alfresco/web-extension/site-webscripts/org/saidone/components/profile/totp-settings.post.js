function main()
{
    model.success = false;
    model.userId = args["user-id"];
    var connector = remote.connect("alfresco");
    if (args["action"] == "generate-new-token" || args["action"] == "activate")
    {
        var result = connector.get("/security/gensecret?user=" + args["user-id"]);
        if (result.status == 200)
        {
            model.success = true;
        }
    }
    else if (args["action"] == "clear-token")
    {
        var result = connector.get("/security/setsecret?user=" + args["user-id"]);
        if (result.status == 200)
        {
            model.success = true;
        }
    }
}

main();
