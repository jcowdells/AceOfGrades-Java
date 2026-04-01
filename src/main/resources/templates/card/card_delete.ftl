<#include "/common/base.ftl">
<#include "/common/forms/input/form_submit.ftl">
<@content>
    <div class="generic-container left-content">
        <h3>Delete card</h3>
        <p>You are deleting: "${card_front}"</p>
        <p>By deleting this card, you will not only delete it from this pack, but from <b>all packs</b> that have stolen this card.</p>
        <p><b>This action is irreversible.</b></p>
        <p>Click the button to confirm that you want to delete this card.</p>
        <form class="form" hx-post="/forms/cards/${card_id}/delete">
            <@form_submit "Delete card - &quot;${card_front}&quot;"></@form_submit>
        </form>
    </div>
</@content>