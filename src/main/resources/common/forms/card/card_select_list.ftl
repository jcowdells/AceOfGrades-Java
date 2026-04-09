<#include "select_list.ftl">
<#macro card_select_list title button_post button_message>
    <div class="spacious-container">
        <@select_list "${title}">
            <div class="generic-container">
                <button class="form-button" hx-post="${button_post}" hx-vals="js:{dest_id: ${dest_id}, cards: selected_cards}" hx-target="#cards-center" hx-swap="outerHTML">${button_message}</button>
            </div>
        </@select_list>
    </div>
</#macro>