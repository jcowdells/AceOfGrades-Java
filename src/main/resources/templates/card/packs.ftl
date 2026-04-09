<#include "/common/base.ftl">
<#include "/common/forms/card/pack_list.ftl">
<@content>
    <@pack_list "Choose a pack" "/packs/$(pack_id)">
        <div class="card" onclick="window.location.href='/packs/create';">
            <h4>Create new pack</h4>
            <p>Make new cards or choose from existsing</p>
        </div>
    </@pack_list>
</@content>