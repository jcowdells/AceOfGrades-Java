<#include "/common/base.ftl">
<#include "/common/forms/card/card_list.ftl">
<@content>
    <@card_list "Choose a card to delete" "/cards/$(id)/delete"></@card_list>
</@content>