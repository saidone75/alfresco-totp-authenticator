<#if success == true>
    <script>
     var pageIndex = document.location.href.lastIndexOf('/');
     document.location.href = document.location.origin + "/share/page/user/${userId}/totp-settings";
    </script>
<#else>
    "success": ${success?string}
</#if>
