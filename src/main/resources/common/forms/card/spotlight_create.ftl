<#include "/common/forms/input/form_submit.ftl">
<#include "select_list.ftl">
<form hx-post="/forms/packs/${pack_id}/spotlights/create" hx-swap="innerHTML" hx-target="#spotlight-form" hx-vals="js:{cards: selected_cards}">
    <div id="spotlight-form">
        <#include "/common/forms/card/spotlight_form.ftl">
    </div>
    <@select_list "Select cards">
        <input class="form-button" type="submit" value="Create spotlight">
    </@select_list>
</form>