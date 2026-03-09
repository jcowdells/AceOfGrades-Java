<form hx-post="/forms/packs/${pack_id}/cards/create" hx-target="this" hx-swap="outerHTML">
    <input type="hidden" name="pack_id" value="${pack_id}">
    <#include "/common/forms/card_editor.ftl">
</form>