<#include "/common/base.ftl">
<@content>
    <div class="packs-center">
        <h2><u>Choose a pack</u></h2>
        <div><br></div>
        <div class="packs-container">
            <div class="pack">
                <a href="/packs/create">Create new pack</a>
                <p>Make new cards or choose from existing</p><br>
            </div>
            <#list packs as pack>
                <div class="pack" style="background: ${pack.getFrontColor()}">
                    <a href="/packs/${pack.getID()}">${pack.getName()}</a>
                    <p>${pack.getDescription()}</p><br>
                    <small>Created by: ${pack.getCreator()}</small>
                </div>
            </#list>
        </div>
    </div>
</@content>