<#include "/common/forms/form_entry.ftl">
<#include "/common/forms/form_checkbox.ftl">
<#include "/common/forms/form_color.ftl">
<#include "/common/forms/form_submit.ftl">
<form hx-post="/forms/packs/create" hx-target="this" hx-swap="outerHTML">
    <@form_entry form.getName() "Pack name:" "name" "text"></@form_entry>
    <@form_entry form.getDescription() "Description:" "description" "text"></@form_entry>
    <@form_color form.getFrontColor() "Front colour:" "front_color"></@form_color>
    <@form_color form.getBackColor() "Back colour:" "back_color"></@form_color>
    <@form_checkbox form.isPublic() "Public?:" "is_public"></@form_checkbox>
    <@form_submit "Create pack"></@form_submit>
</form>