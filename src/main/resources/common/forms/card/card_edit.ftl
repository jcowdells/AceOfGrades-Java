<#include "/common/forms/card/card_editor.ftl">
<form hx-post="/forms/cards/${card_id}/edit" hx-target="this" hx-swap="outerHTML">
    <@card_editor "Update card"></@card_editor>
</form>