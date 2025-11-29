<#include "/common/forms/form_entry.ftl">
<form hx-post="/api/register" hx-target="this" hx-swap="outerHTML">
    <@form_entry form.getUsername() "Username:" "username" "text"></@form_entry>
    <@form_entry form.getEmailAddress() "Email Address:" "email_address" "email"></@form_entry>
    <@form_entry form.getPassword() "Password:" "password" "password"></@form_entry>
    <@form_entry form.getPasswordRepeat() "Repeat Password:" "password_repeat" "password"></@form_entry>
    <input type="submit" value="Submit">
</form>