<#include "/common/forms/input/form_submit.ftl">
<form hx-post="/forms/packs/${pack_id}/spotlights/create" hx-swap="innerHTML" hx-target="#spotlight-form" hx-vals="js:{cards: selected_cards}">
    <div id="spotlight-form">
        <#include "/common/forms/card/spotlight_form.ftl">
    </div>
    <div>
        <div id="spotlights-center">
            <h2><u>Select cards</u></h2>
            <div id="cards-container">
                <#list cards as card>
                    <div class="card prevent-select" style="background: ${card.getFrontColor()}" data-card-id="${card.getID()}" data-selected="${card.getSelected()}">
                        <span>&#9989;</span>
                        <p>${card.getFront()}</p>
                    </div>
                </#list>
            </div>
        </div>
    </div>
    <input class="form-button" type="submit" value="Create spotlight">
</form>