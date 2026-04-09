<#include "/common/forms/input/form_entry.ftl">
<#include "/common/forms/input/form_submit.ftl">
<form hx-post="/forms/register" hx-target="this" hx-swap="outerHTML">
    <@form_entry form.getUsername() "Username:" "username" "text"></@form_entry>
    <@form_entry form.getEmailAddress() "Email Address:" "email_address" "email"></@form_entry>
    <@form_entry form.getPassword() "Password:" "password" "password"></@form_entry>
    <@form_entry form.getPasswordRepeat() "Repeat Password:" "password_repeat" "password"></@form_entry>
    <br>
    <@form_submit "Submit"></@form_submit>
</form>