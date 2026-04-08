<#include "/common/base.ftl">
<@content>
    <div class="packs-center">
        <h2><u>Choose a destination pack</u></h2>
        <div><br></div>
        <div class="packs-container">
            <#list packs as pack>
                <div class="pack" style="background: ${pack.getFrontColor()}">
                    <a href="/packs/${pack_id}/cards/move?dest-id=${pack.getID()}">${pack.getName()}</a>
                    <p>${pack.getDescription()}</p><br>
                    <small>Created by: ${pack.getCreator()}</small>
                </div>
            </#list>
        </div>
    </div>
</@content>