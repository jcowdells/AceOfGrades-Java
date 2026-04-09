<#-- button_redirect will replace $(pack_id) with the pack id, and $(dest_id) with the dest id. -->
<#macro pack_list title button_redirect>
    <div class="spacious-container">
        <h2><u>${title}</u></h2>
        <div class="packs-center">
            <div class="packs-container">
                <#nested>
                <#list packs as pack>
                    <div class="card" style="background: ${pack.getFrontColor()}"
                         onclick="window.location.href='${button_redirect?replace("$(pack_id)", pack_id!pack.getID())?replace("$(dest_id)", pack.getID())}';">
                        <h4>${pack.getName()}</h4>
                        <p>${pack.getDescription()}</p><br>
                        <small>Created by: ${pack.getCreator()}</small>
                    </div>
                </#list>
            </div>
        </div>
    </div>
</#macro>