<#include "/common/base.ftl">
<#include "/common/forms/card/card_select_list.ftl">
<@content>
    <@card_select_list "Select cards to steal" "/forms/packs/${pack_id}/cards/steal" "Steal cards"></@card_select_list>
</@content>
<script src="/static/card_select.js"></script>