<#include "/common/forms/form_entry.ftl">
<#include "/common/forms/form_color.ftl">
<#include "/common/forms/form_submit.ftl">
<form hx-post="/api/create_card" hx-target="this" hx-swap="outerHTML">
    <input type="hidden" name="pack_id" value="${pack_id}">
    <div class="centre-space">
        <div class="arrange-across">
            <div>
                <@form_color form.getFrontColor() "Front color:" "front_color"></@form_color>
                <@form_color form.getBackColor() "Back color:" "back_color"></@form_color><br>
                <div class="form-submit">
                    <input id="flip-button" type="button" value="Flip">
                </div>
            </div>
            <div class="card-container form">
                <div id="card-front">
                    <textarea id="front" name="front">${form.getFront().input}</textarea>
                </div>
                <div id="card-back">
                    <textarea id="back" name="back">${form.getBack().input}</textarea>
                </div>
            </div>
        </div>
        <div class="centre-wide">
            <#if form.getFront().hasErrors()>
                <div class="error-list">
                    <ul>
                        <#list form.getFront().errors as error>
                            <li>${error}</li>
                        </#list>
                    </ul>
                </div>
            </#if>
            <#if form.getBack().hasErrors()>
                <div class="error-list">
                    <ul>
                        <#list form.getBack().errors as error>
                            <li>${error}</li>
                        </#list>
                    </ul>
                </div>
            </#if>
            <@form_submit "Create card"></@form_submit>
        </div>
    </div>
</form>