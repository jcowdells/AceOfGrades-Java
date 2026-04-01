<#include "/common/forms/input/form_entry.ftl">
<#include "/common/forms/input/form_submit.ftl">
<form hx-post="/packs/${pack_id}/spotlights/create">
    <div>
        <@form_entry form.getName() "Spotlight name:" "spotlight-name" "text"></@form_entry>
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
    <button class="form-button" type="submit" hx-vals="js:{cards: selected_cards}">Create spotlight</button>
</form>
<script src="/static/card_select.js"></script>