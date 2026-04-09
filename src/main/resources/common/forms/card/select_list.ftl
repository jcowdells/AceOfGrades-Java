<#macro select_list title>
    <#include "/common/forms/input/form_submit.ftl">
    <h2><u>${title}</u></h2>
    <div id="cards-center">
        <div id="cards-container">
            <#list cards as card>
                <div class="card prevent-select" style="background: ${card.getFrontColor()}" data-card-id="${card.getID()}" data-selected="${card.getSelected()}">
                    <span>&#9989;</span>
                    <p>${card.getFront()}</p>
                </div>
            </#list>
        </div>
        <#nested>
    </div>
</#macro>