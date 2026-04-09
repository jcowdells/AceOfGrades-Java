<#include "/common/base.ftl">
<#include "/common/forms/card/pack_list.ftl">
<@content>
    <@pack_list "Choose a destination pack" "/packs/$(pack_id)/cards/move?dest-id=$(dest_id)"></@pack_list>
</@content>