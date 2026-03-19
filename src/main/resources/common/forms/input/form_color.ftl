<#macro form_color form_data, form_label, form_name>
    <#if form_data.hasErrors()>
        <div class="form-color form-error">
            <label>${form_label}<br>
                <input name="${form_name}" id=${form_name} type="color" value="${form_data.input}">
            </label><br>
        </div>
        <div class="error-list">
            <ul>
                <#list form_data.errors as error>
                    <li>${error}</li>
                </#list>
            </ul>
        </div>
    <#else>
        <div class="form-color">
            <label>${form_label}<br>
                <input name="${form_name}" id=${form_name} type="color" value="${form_data.input}">
            </label><br>
        </div>
    </#if>
</#macro>