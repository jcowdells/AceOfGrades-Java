<#include "/common/forms/form_entry.ftl">
<#include "/common/forms/form_submit.ftl">
<form hx-post="/api/login" hx-target="this" hx-swap="outerHTML">
    <@form_entry form.getUsername() "Username:" "username" "text"></@form_entry>
    <@form_entry form.getPassword() "Password:" "password" "password"></@form_entry>
    <br>
    <@form_submit "Submit"></@form_submit>
</form>