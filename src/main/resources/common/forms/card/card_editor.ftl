<#include "/common/forms/input/form_entry.ftl">
<#include "/common/forms/input/form_color.ftl">
<#include "/common/forms/input/form_submit.ftl">
<#macro card_editor button_message>
    <div class="centre-space">
        <div class="arrange-across">
            <div>
                <@form_color form.getFrontColor() "Front color:" "front_color"></@form_color>
                <@form_color form.getBackColor() "Back color:" "back_color"></@form_color><br>
                <div class="form-submit">
                    <input id="flip-button" type="button" value="Flip">
                </div>
            </div>
            <div id="edit-container" class="form">
                <div id="edit-front">
                    <textarea id="front" name="front">${form.getFront().input}</textarea>
                </div>
                <div id="edit-back">
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
            <@form_submit "${button_message}"></@form_submit>
            <#if message??>
                <p>${message}</p>
            </#if>
        </div>
    </div>
</#macro>