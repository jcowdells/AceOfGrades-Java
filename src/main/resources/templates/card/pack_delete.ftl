<#include "/common/base.ftl">
<#include "/common/forms/input/form_submit.ftl">
<@content>
    <div class="generic-container left-content">
        <h3>Delete pack</h3>
        <p>You are deleting: "${pack_name} - ${pack_description}"</p>
        <p>By deleting this pack, you will not only delete the pack, but also any cards that belong to this pack.</p>
        <p>Cards that were stolen into this pack will not be deleted. Cards from this pack that other people have stolen <b>will</b> be deleted.</p>
        <p><b>This action is irreversible.</b></p>
        <p>Click the button to confirm that you want to delete this pack.</p>
        <form class="form" hx-post="/forms/packs/${pack_id}/delete">
            <@form_submit "Delete pack - &quot;${pack_name}&quot;"></@form_submit>
        </form>
    </div>
</@content>