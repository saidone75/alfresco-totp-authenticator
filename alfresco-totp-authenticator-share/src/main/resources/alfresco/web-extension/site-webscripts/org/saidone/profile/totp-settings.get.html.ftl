<@markup id="css" >
    <#-- CSS Dependencies -->
    <@link href="${url.context}/res/components/profile/profile.css" group="profile"/>
</@>

<@markup id="widgets">
    <@createWidgets group="profile"/>
</@>

<@markup id="html">
    <@uniqueIdDiv>
        <#assign el=args.htmlid?html>
        <div id="${el}-body" class="profile totp-settings">
            <div class="header-bar">${msg("label.totp-actual-configuration")}</div>
            <div class="row">
                <span class="fieldlabelright">${msg("label.totp-secret")}:</span>
                <#if result.secret!="">
                    <span class="fieldvalue">${result.secret}</span>
                <#else>
                    <span class="fieldvalue">${msg("label.totp-deactivated")}</span>
                </#if>
            </div>
            <#if result.secret!="">
                <div class="photorow">
                    <img class="photoimg" src="${result.dataUri}" />
                </div>
            </#if>
            <hr/>
            <form id="${el}-form" action="${url.context}/service/components/profile/totp-settings" method="post">
                <#if result.secret!="">
                    <div class="row">
                        <div class="buttons">
                            <button id="${el}-button-generate-new-token" name="generate-new-token" value="generate-new-token">${msg("button.totp-generate-new-token")}</button>
                            <button id="${el}-button-clear-token" name="clear-token" value="clear-token">${msg("button.totp-clear-token")}</button>
                        </div>
                    </div>
                <#else>
                    <div class="row">
                        <div class="buttons">
                            <button id="${el}-button-activate" name="activate" value="activate">${msg("button.totp-activate")}</button>
                        </div>
                    </div>
                </#if>
            </form>
        </div>
    </@>
</@>
