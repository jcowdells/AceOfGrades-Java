<#include "/common/base.ftl">
<@content>
    <div class="packs-center">
        <h2><u>Choose a card to delete</u></h2>
        <div><br></div>
        <div class="packs-container">
            <#list cards as card>
                <div class="card" style="background: ${card.getFrontColor()}" onclick="window.location.href='/cards/${card.getID()}/delete';">
                    <p>${card.getFront()}</p>
                </div>
            </#list>
        </div>
    </div>
</@content>