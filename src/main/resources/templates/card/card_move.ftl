<#include "/common/base.ftl">
<#include "/common/forms/card/card_select_list.ftl">
<@content>
    <@card_select_list "Select cards to move" "/forms/packs/${pack_id}/cards/move" "Move cards"></@card_select_list>
</@content>
<script src="/static/card_select.js"></script>