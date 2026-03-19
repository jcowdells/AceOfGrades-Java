<#include "/common/base.ftl">
<#include "/common/forms/input/redirect_button.ftl">
<@content>
    <div class="centre-wide">
        <div class="generic-container">
            <h3>Quiz</h3>
        </div>
        <#if is_creator>
            <div class="left-content generic-container">
                <h3>Edit</h3>
            </div>
            <div class="generic-container">
                <h3>Find cards</h3>
                <p>Click the button to find cards to add to this pack.</p>
                <@redirect_button "" "Find cards"></@redirect_button>
            </div>
        </#if>
        <#if user.role != "ANYONE">
            <div class="generic-container">
                <h3>Steal</h3>
                <p>Click the button to steal cards from this pack.</p>
                <@redirect_button "/packs/${pack_id}/steal/select" "Steal"></@redirect_button>
            </div>
        </#if>
        <#if is_creator>
            <div class="generic-container">
                <h3>Delete</h3>
            </div>
        </#if>
    </div>
</@content>