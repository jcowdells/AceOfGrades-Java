<#include "/common/base.ftl">
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
            </div>
        </#if>
        <#if user.role != "ANYONE">
            <div class="generic-container">
                <h3>Steal</h3>
            </div>
        </#if>
        <#if is_creator>
            <div class="generic-container">
                <h3>Delete</h3>
            </div>
        </#if>
    </div>
</@content>