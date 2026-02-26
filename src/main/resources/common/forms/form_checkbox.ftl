<#macro form_checkbox form_data, form_label, form_name>
    <div class="form form-checkbox">
        <label>${form_label}<br>
            <input name="${form_name}" type="checkbox" value="${form_name}" ${form_data.input?then("checked", "")}/>
        </label><br>
    </div>
</#macro>