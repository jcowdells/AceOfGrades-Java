<#macro form_entry form_data, form_label, form_name, form_type>
    <#if form_data.hasErrors()>
        <div class="form form-error">
            <label>${form_label}<br>
                <input name="${form_name}" type="${form_type}" value="${form_data.input}">
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
        <div class="form">
            <label>${form_label}<br>
                <input name="${form_name}" type="${form_type}" value="${form_data.input}">
            </label><br>
        </div>
    </#if>
</#macro>