<#include "/common/forms/input/form_submit.ftl">
<div id="cards-center">
    <h2><u>Select cards</u></h2>
    <div id="cards-container">
        <#list cards as card>
            <div class="card prevent-select" style="background: ${card.getFrontColor()}" data-card-id="${card.getID()}" data-selected="${card.getSelected()}">
                <span>&#9989;</span>
                <p>${card.getFront()}</p>
            </div>
        </#list>
    </div>
    <div class="generic-container">
        <button hx-post="/forms/packs/${pack_id}/steal" hx-vals="js:{dest_id: ${dest_id}, cards: selected_cards}" hx-target="#cards-center" hx-swap="outerHTML">Steal cards</button>
    </div>
</div>