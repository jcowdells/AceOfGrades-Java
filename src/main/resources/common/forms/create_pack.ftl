<#include "/common/forms/form_entry.ftl">
<#include "/common/forms/form_checkbox.ftl">
<form hx-post="/api/create_pack" hx-target="this" hx-swap="outerHTML">
    <@form_entry form.getName() "Pack name:" "name" "text"></@form_entry>
    <@form_entry form.getDescription() "Description:" "description" "text"></@form_entry>
    <@form_entry form.getFrontColor() "Front colour:" "front_color" "color"></@form_entry>
    <@form_entry form.getBackColor() "Back colour:" "back_color" "color"></@form_entry>
    <@form_checkbox form.isPublic() "Public?:" "is_public"></@form_checkbox>
    <input type="submit" value="Create pack">
</form>