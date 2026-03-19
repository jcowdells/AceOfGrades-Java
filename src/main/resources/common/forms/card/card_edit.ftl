<form hx-post="/forms/cards/${card_id}/edit" hx-target="this" hx-swap="outerHTML">
    <input type="hidden" name="card_id" value="${card_id}">
    <#include "/common/forms/card/card_editor.ftl">
</form>