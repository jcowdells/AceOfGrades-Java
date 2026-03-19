<#include "/common/forms/input/form_entry.ftl">
<#include "/common/forms/input/form_color.ftl">
<#include "/common/forms/input/form_submit.ftl">
<div class="centre-space">
    <div class="arrange-across">
        <div>
            <@form_color form.getFrontColor() "Front color:" "front_color"></@form_color>
            <@form_color form.getBackColor() "Back color:" "back_color"></@form_color><br>
            <div class="form-submit">
                <input id="flip-button" type="button" value="Flip">
            </div>
        </div>
        <div id="card-container" class="form">
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