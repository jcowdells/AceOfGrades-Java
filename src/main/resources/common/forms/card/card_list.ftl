<#-- button_redirect will replace $(id) with the card_id -->
<#macro card_list title button_redirect>
    <div class="spacious-container">
        <h2><u>${title}</u></h2>
        <div class="packs-center">
            <div id="packs-container">
                <#list cards as card>
                    <div class="card" style="background: ${card.getFrontColor()}" onclick="window.location.href='${button_redirect?replace("$(id)", card.getID()?string)}';">
                        <p>${card.getFront()}</p>
                    </div>
                </#list>
            </div>
        </div>
    </div>
</#macro>