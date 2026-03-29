<#include "/common/forms/card/card_editor.ftl">
<form hx-post="/forms/packs/${pack_id}/cards/create" hx-target="this" hx-swap="outerHTML">
    <@card_editor "Create card"></@card_editor>
</form>