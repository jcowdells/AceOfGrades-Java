<#include "/common/base.ftl">
<#include "/common/forms/card/pack_list.ftl">
<@content>
    <@pack_list "Choose a pack to steal from" "/packs/$(pack_id)/cards/steal?dest-id=$(dest_id)"></@pack_list>
</@content>